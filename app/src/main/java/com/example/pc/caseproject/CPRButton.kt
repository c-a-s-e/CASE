package com.example.pc.caseproject

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class CPRButton : Button {
    constructor(context: Context) : super(context) {
        compositeDisposable = CompositeDisposable()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        compositeDisposable = CompositeDisposable()
    }

    lateinit var pulseUpdateListener: PulseUpdateListener
    private var compositeDisposable: CompositeDisposable
    private val bpm: Long = 100
    fun start() {
        val compositeDisposable = CompositeDisposable()
        stop()
        compositeDisposable.add(Observable.interval(0, 60 * 1000 / bpm, TimeUnit.MILLISECONDS)
                .take(31)
                .flatMap {
                    return@flatMap Observable.create<Long> { emitter ->
                        emitter.onNext(it)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.toInt() == 30) {
                        pulseUpdateListener.pausePulse()
                        text = "Check"
                    } else {
                        text = it.toString()
                        pulseUpdateListener.updatePulse()
                        Log.e("pulse", it.toString())
                    }
                }, {}, {}))
    }

    fun stop() {
        compositeDisposable.dispose()
    }

    interface PulseUpdateListener {
        fun updatePulse()
        fun pausePulse()
    }
}