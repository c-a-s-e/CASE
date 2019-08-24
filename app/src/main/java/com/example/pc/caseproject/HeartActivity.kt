package com.example.pc.caseproject

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import butterknife.BindView
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_heart.*


class HeartActivity : AppCompatActivity(), CPRButton.PulseUpdateListener, AEDUtil.APIListener {

    private lateinit var myRequest: AED_FIND_REQUEST

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    private lateinit var receiver: MessageBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart)
        setSupportActionBar(toolbar as? Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "흉부 압박"
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        myRequest = AED_FIND_REQUEST()
        val myLocation = findMyLocation()
        myRequest.myLatitude = myLocation?.latitude
        myRequest.myLongitude = myLocation?.longitude
        AEDUtil.getAEDData(this, myLocation!!, myRequest, this, true)

        cprButton.pulseUpdateListener = this
        cprButton.isClickable = false
        cprButton.background = resources.getDrawable(R.drawable.cpr_button_static)
        val reward_button = findViewById(R.id.reward_Button) as Button

        pauseButton.setOnClickListener { cprButton.stop() }
        cprButton.setOnClickListener { cprButton.start() }

        reward_button.setOnClickListener {
            val intent = Intent(this, RewardActivity::class.java)
            intent.putExtra("user_flag", 0)
            startActivity(intent)
        }

        text911()
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("accepted")
        receiver = MessageBroadcastReceiver()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        cprButton.stop()
        mediaPlayer.release()
        unregisterReceiver(receiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun update() {
        stepView.go(1, true)
    }

    override fun updateTime(seconds: Long) {
        time.text = "압박 시간 ${(seconds / 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
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
            stepView.go(0, true)
        } catch (e: Throwable) {
            Toast.makeText(this, "119 fail", LENGTH_LONG).show()
            stepView.go(-1, true)
        }
    }

    private fun findMyLocation(): Location? {
        //**gps 기능이 켜졌는지 확인하는 코드가 필요합니다,
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val myLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else {
            Toast.makeText(applicationContext, "먼저 위치 권한을 확인해주세요", Toast.LENGTH_LONG).show()
            null
        }
    }

    inner class MessageBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            stepView.go(2, true)
        }
    }

    data class Measurement(
            val time: Long,
            val value: Float
    ) {
        override fun toString(): String {
            return "$time, $value\n"
        }
    }
}
