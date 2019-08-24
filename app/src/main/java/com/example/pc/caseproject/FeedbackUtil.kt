package com.example.pc.caseproject

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class FeedbackUtil {
    lateinit var feedbackListener: FeedbackListener
    private var compressionIndex = 0
    private val alpha = 0.7f
    private val positivePeakThreshold = 20.0f
    private val negativePeakThreshold = -10.0f
    private var lastPositivePeak: Measurement? = null
    private var lastNegativePeak: Measurement? = null
    private var lastIntersection: Measurement? = null
    val smoothMeasurements = Array(5 * 3600 * 50) { Measurement(-1, 0, 0.0) }
    val compressions = mutableListOf<Compression>()
    private val smoothSubject: PublishSubject<Measurement> by lazy { PublishSubject.create<Measurement>() }
    private val sliceSubject: PublishSubject<Measurement> by lazy { PublishSubject.create<Measurement>() }
    private val feedbackSubject: PublishSubject<Compression> by lazy { PublishSubject.create<Compression>() }

    init {
        setUpPublishSubjects()
    }

    fun pushMeasurement(measurement: Measurement) {
        smoothSubject.onNext(measurement)
    }

    @SuppressLint("CheckResult")
    fun setUpPublishSubjects() {
        smoothSubject.subscribeOn(Schedulers.io())
                .subscribe({
                    it.index.let { idx ->
                        smoothMeasurements[idx] = it
                        smoothMeasurements.takeLast(2).apply {
                            smoothMeasurements[idx-1].value = this[0].value * (1 - alpha) / 2 +
                                    this[1].value * alpha +
                                    this[2].value * (1 - alpha) / 2
                            Log.e("smooth", smoothMeasurements[idx - 1].toString())
                            sliceSubject.onNext(smoothMeasurements[idx - 1])
                        }
                    }
                },
                        { error -> Log.e("error", error.message) },
                        { Log.e("subject", "complete") })

        sliceSubject.subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.value < negativePeakThreshold) {
                        lastNegativePeak = it
                        Log.e("peak", "neg")
                        if (lastPositivePeak != null) {
                            val intersection = smoothMeasurements.sliceArray(lastPositivePeak?.index!!..lastNegativePeak?.index!!).first { m -> m.value < 0 }
                            if (lastIntersection != null) {
                                val compression = Compression(compressionIndex, smoothMeasurements.sliceArray(lastIntersection?.index!! - 1..intersection.index).toMutableList())
                                feedbackSubject.onNext(compression)
                                compressionIndex++
                                feedbackListener.onDrawChart(compression.measurements)
                                Log.e("comp", compressionIndex.toString())
                            }
                            lastIntersection = intersection
                        }
                        lastPositivePeak = null
                    } else if (it.value > positivePeakThreshold) {
                        Log.e("peak", "pos")
                        lastPositivePeak = it
                        lastNegativePeak = null
                    }
                },
                        { error -> Log.e("error", error.message) },
                        { Log.e("complete", "slice") })

        feedbackSubject.subscribeOn(Schedulers.io())
                .subscribe({
                    compressions.add(it)
                    compressions.takeLast(5).apply {
                        val avgO2P = Pair(map { c -> c.o2pDistance.first }.average(), map { c -> c.o2pDistance.second }.average())
                        val avgO2N = Pair(map { c -> c.o2nDistance.first }.average(), map { c -> c.o2nDistance.second }.average())
                        val avgO2L = Pair(map { c -> c.o2lDistance.first }.average(), map { c -> c.o2lDistance.second }.average())
                    }
                },
                        { error -> Log.e("error", error.message) },
                        {})
    }

    interface FeedbackListener {
        fun onDrawChart(data: MutableList<Measurement>)
        fun onFeedbackResult()
    }

    data class Measurement(
            val index: Int,
            val time: Long,
            var value: Double
    ) {
        override fun toString(): String {
            return "#$index: $time, $value\n"
        }
    }

    data class Compression(val index: Int, val measurements: MutableList<Measurement>) {
        private val positivePeak: Measurement
            get() = measurements.maxBy { it.value } as Measurement
        private val negativePeak: Measurement
            get() = measurements.minBy { it.value } as Measurement
        private val origin: Measurement
            get() = measurements.take(2).let {
                return Measurement(-1, it.map { m -> m.time }.average(), it.map { m -> m.value }.average())
            }
        private val last: Measurement
            get() = measurements.takeLast(2).let {
                return Measurement(-1, it.map { m -> m.time }.average(), it.map { m -> m.value }.average())
            }
        val o2pDistance: Pair<Long, Double>
            get() = Pair(positivePeak.time - origin.time, positivePeak.value - origin.value)
        val o2nDistance: Pair<Long, Double>
            get() = Pair(negativePeak.time - origin.time, negativePeak.value - origin.value)
        val o2lDistance: Pair<Long, Double>
            get() = Pair(last.time - origin.time, last.value - origin.value)

        override fun toString(): String {
            return "$o2pDistance, $o2nDistance, $o2lDistance"
        }

        private fun List<Long>.average(): Long {
            return sum() / size
        }
    }
}