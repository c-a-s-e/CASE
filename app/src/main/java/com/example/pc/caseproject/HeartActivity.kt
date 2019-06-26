package com.example.pc.caseproject

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_heart.*


class HeartActivity : AppCompatActivity(), CPRButton.PulseUpdateListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart)
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        cprButton.pulseUpdateListener = this
        cprButton.isClickable = false
        cprButton.start()
    }

    override fun onPause() {
        super.onPause()
        cprButton.stop()
        mediaPlayer.release()
    }

    override fun updateTime(seconds: Long) {
        time.text = "${(seconds/60).toString().padStart(2,'0')}:${(seconds%60).toString().padStart(2,'0')}"
    }

    override fun updatePulse() {
        mediaPlayer.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(300)
        }
    }
}
