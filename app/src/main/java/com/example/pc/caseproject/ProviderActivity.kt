package com.example.pc.caseproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_provider.*
import java.io.IOException
import java.util.*

class ProviderActivity : AppCompatActivity(), OnMapReadyCallback {
    private val senderToken by lazy { intent.extras?.getString("sender-token") }
    private val senderLat by lazy { intent.extras?.getString("sender_latitude")?.toDouble() }
    private val senderLong by lazy { intent.extras?.getString("sender_longitude")?.toDouble() }
    private val aedLat by lazy { intent.extras?.getString("aed_latitude")?.toDouble() }
    private val aedLong by lazy { intent.extras?.getString("aed_longitude")?.toDouble() }
    private val myLat by lazy { intent.extras?.getDouble("my_latitude") }
    private val myLong by lazy { intent.extras?.getDouble("my_longitude") }

    private lateinit var currentAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider)
        setActionBar(toolbar as Toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.title = "주변 AED 위치"
        showDialog()
        accept.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
        }
        otherAED.setOnClickListener {
            val intent = Intent(this, FindAEDActivity::class.java)
            startActivity(intent)
        }

        AEDUtil.sendAccept(this, senderToken)

        val mGeoCoder = Geocoder(this@ProviderActivity, Locale.ENGLISH)
        val address: List<Address>?

        try {
            address = mGeoCoder.getFromLocation(myLat!!, myLong!!, 1)
            if (address != null && address.isNotEmpty()) {
                val currentLocationAddress = address[0].getAddressLine(0).toString()
                currentAddress = currentLocationAddress
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val fm = supportFragmentManager
        val f = fm.findFragmentById(R.id.map) as SupportMapFragment?
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@ProviderActivity)
    }

    private fun showDialog() {
        val ft = supportFragmentManager.beginTransaction()
        val popup = LoadingDialogFragment()
        popup.show(supportFragmentManager, "loading")
        ft.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(map: GoogleMap) {
        Log.e("got", aedLat.toString())
        val sender = LatLng(senderLat!!, senderLong!!)
        val sendermarkerOptions = MarkerOptions()
        sendermarkerOptions.position(sender)

        val bitmapdraw = resources.getDrawable(R.drawable.sendermarker) as BitmapDrawable
        val bitmap = bitmapdraw.bitmap
        val senderMarker = Bitmap.createScaledBitmap(bitmap, 104, 148, false)
        sendermarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(senderMarker))
        map.addMarker(sendermarkerOptions)

        val now = LatLng(myLat!!, myLong!!)
        val mymarkerOptions = MarkerOptions()
        mymarkerOptions.position(now)


        val bitmapdraw2 = resources.getDrawable(R.drawable.mymarker) as BitmapDrawable
        val bitmap2 = bitmapdraw2.bitmap
        val myMarker = Bitmap.createScaledBitmap(bitmap2, 104, 148, false)
        mymarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(myMarker))
        map.addMarker(mymarkerOptions)

        val aed = LatLng(aedLat!!, aedLong!!)
        val aedmarkerOptions = MarkerOptions()
        aedmarkerOptions.position(aed)


        val bitmapdraw3 = resources.getDrawable(R.drawable.aed) as BitmapDrawable
        val bitmap3 = bitmapdraw3.bitmap
        val aedMarker = Bitmap.createScaledBitmap(bitmap3, 88, 80, false)
        aedmarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(aedMarker))
        map.addMarker(aedmarkerOptions)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 15f))

    }
}
