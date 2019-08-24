package com.example.pc.caseproject

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toolbar
import com.example.pc.caseproject.FeedbackUtil.Measurement
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_heart.*
import java.io.File
import java.io.FileWriter


class HeartActivity : AppCompatActivity(), CPRButton.PulseUpdateListener, AEDUtil.APIListener, SensorEventListener, FeedbackUtil.FeedbackListener {

    private lateinit var myRequest: AED_FIND_REQUEST

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    private lateinit var receiver: MessageBroadcastReceiver

    private val sensorManager: SensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val accelerometer: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) }

    private val interval = 10
    private var measurementIndex = 0
    private var lastTime: Long = -1
    private var collectData = false
    private val compositeDisposable by lazy { CompositeDisposable() }
    private val feedbackUtil by lazy { FeedbackUtil() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart)
        setActionBar(toolbar as? Toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.title = "흉부 압박"
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
        pauseButton.setOnClickListener { cprButton.stop() }
        cprButton.setOnClickListener {
            cprButton.start()
            collectData = true
            lastTime = System.currentTimeMillis()
        }
        text911()
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("accepted")
        receiver = MessageBroadcastReceiver()
        registerReceiver(receiver, intentFilter)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        feedbackUtil.feedbackListener = this
    }

    override fun onPause() {
        super.onPause()
        cprButton.stop()
        mediaPlayer.release()
        unregisterReceiver(receiver)
        sensorManager.unregisterListener(this)
        saveMeasurements()
        saveCompressions()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (collectData) {
            event?.apply {
                if (sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                    val zAccel = values[2]
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTime > interval) {
                        val measurement = Measurement(measurementIndex, currentTime, zAccel.toDouble())
                        feedbackUtil.pushMeasurement(measurement)
                        lastTime = currentTime
                        measurementIndex++
                    }
                }
            }
        }
    }

    private fun saveMeasurements() {
        val folder = Environment.getExternalStorageDirectory().absolutePath
        val fileWriter = FileWriter(File(folder, "data.csv"))
        val saveDisposable = Observable.fromIterable(feedbackUtil.smoothMeasurements.toMutableList())
                .map {
                    fileWriter.write(it.toString())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { Log.d("measurement", it.toString()) },
                        { error -> Log.e("csv", error.message) },
                        {
                            fileWriter.close()
                        }
                )
        compositeDisposable.add(saveDisposable)
    }

    private fun saveCompressions() {
        val folder = Environment.getExternalStorageDirectory().absolutePath
        val fileWriter = FileWriter(File(folder, "compressions.csv"))
        val saveDisposable = Observable.fromIterable(feedbackUtil.compressions.toMutableList())
                .map {
                    fileWriter.write(it.toString())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { Log.d("compression", it.toString()) },
                        { error -> Log.e("csv", error.message) },
                        { fileWriter.close() }
                )
        compositeDisposable.add(saveDisposable)
    }

    override fun onDrawChart(data: MutableList<Measurement>) {
        runOnUiThread {
            val entries = mutableListOf<Entry>()
            data.forEach { entries.add(Entry((it.time - 1566398100000).toFloat(), it.value.toFloat())) }
            val dataSet = LineDataSet(entries, "data")
            dataSet.color = Color.RED
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    }

    override fun onFeedbackResult() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
}
