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
        background=context.resources.getDrawable(R.drawable.cpr_button_selector)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        compositeDisposable = CompositeDisposable()
        background=context.resources.getDrawable(R.drawable.cpr_button_selector)
    }

    lateinit var pulseUpdateListener:PulseUpdateListener
    private var compositeDisposable: CompositeDisposable
    private val bpm:Long=100
    fun start() {
        val pulseDisposable =
                Observable.interval(0, 60*1000/bpm, TimeUnit.MILLISECONDS)
                        .flatMap {
                            return@flatMap Observable.create<Long> { emitter ->
                                Log.d("IntervalExample", "Create")
                                emitter.onNext(it)
                                emitter.onComplete()
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            text = it.toString()
                            pulseUpdateListener.updatePulse()
                            Log.d("IntervalExample", it.toString())
                        }
        val timeDisposable =
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .flatMap {
                            return@flatMap Observable.create<Long> { emitter ->
                                emitter.onNext(it)
                                emitter.onComplete()
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            pulseUpdateListener.updateTime(it)
                            Log.d("IntervalExample", it.toString())
                        }
        compositeDisposable.add(pulseDisposable)
        compositeDisposable.add(timeDisposable)
    }

    fun stop() {
        compositeDisposable.dispose()
    }

    interface PulseUpdateListener{
        fun updateTime(seconds:Long)
        fun updatePulse()
    }
}