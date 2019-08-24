package com.example.pc.caseproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.bumptech.glide.Glide
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

class ProviderActivity : AppCompatActivity() {
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
        setSupportActionBar(toolbar as Toolbar)
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

        Glide.with(this).load(R.drawable.aed_location).into(location)
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
}
