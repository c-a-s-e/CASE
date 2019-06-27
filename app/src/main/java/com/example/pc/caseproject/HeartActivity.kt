package com.example.pc.caseproject

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_heart.*
import java.util.concurrent.TimeUnit


class HeartActivity : AppCompatActivity(), CPRButton.PulseUpdateListener, EmergencyDialogFragment.DialogActionListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart)
        setSupportActionBar(toolbar as? Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        cprButton.pulseUpdateListener = this
        cprButton.isClickable = false
        pauseButton.setOnClickListener { cprButton.stop() }
        text911()
        showDialog()
        stepView.go(1, true)
    }

    override fun onPause() {
        super.onPause()
        cprButton.stop()
        mediaPlayer.release()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("CheckResult")
    private fun showDialog() {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val popup = EmergencyDialogFragment()
        popup.dialogActionListener = this
        popup.show(fm, "popup")
        ft.commit()

        Observable.create<String> { emitter ->
            emitter.onNext("")
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    popup.dismiss()
                }
    }

    override fun onDialogDismiss() {
        cprButton.start()
    }

    override fun updateTime(seconds: Long) {
        time.text = "${(seconds / 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
    }

    override fun updatePulse() {
        mediaPlayer.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(300)
        }
    }

    private fun text911() {
        val content = "테스트"
        val number = "1"
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, content, null, null)
        } catch (e: Throwable) {
            Toast.makeText(this, "119 fail", LENGTH_LONG).show()
        }
    }
}
