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
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.support.v7.widget.Toolbar
import android.app.ActionBar
import com.bumptech.glide.Glide
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
        if (myLocation==null){
            Toast.makeText(applicationContext, "현재 위치를 받아올 수 없습니다. \n잠시후에 다시 시도하세요", Toast.LENGTH_LONG).show()
            this.finish()
        }
        myRequest.myLatitude = myLocation?.latitude
        myRequest.myLongitude = myLocation?.longitude
        AEDUtil.getAEDData(this, myLocation!!, myRequest, this, true)

        cprButton.pulseUpdateListener = this
        cprButton.isClickable = false
        cprButton.background = resources.getDrawable(R.drawable.cpr_button_static)
        cprButton.setOnClickListener { cprButton.start() }
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
        mediaPlayer.release()
        unregisterReceiver(receiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    override fun update() {
    }

    override fun updatePulse() {
        mediaPlayer.start()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(300)
        }
        Glide.with(this).load(R.drawable.mylocation).into(chestImage)
    }

    override fun pausePulse() {
        Glide.with(this).load(R.drawable.cpr_logo).into(chestImage)
        cprButton.stop()
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

        }
    }
}
