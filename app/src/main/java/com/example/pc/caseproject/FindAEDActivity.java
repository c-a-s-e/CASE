package com.example.pc.caseproject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindAEDActivity extends AppCompatActivity implements OnMapReadyCallback {

    String sender_address, aed_address, date, sender_token, nowAddress;
    Double sender_latitude, sender_longitude, aed_latitude, aed_longitude, my_latitude, my_longitude;
    TextView aedTextView, senderAddress, myAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_aed);
        setActionBar((Toolbar) findViewById(R.id.toolbar));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("주변 AED 위치");

        Geocoder mGeoCoder = new Geocoder(FindAEDActivity.this, Locale.ENGLISH);
        List<Address> address;

        try {
            address = mGeoCoder.getFromLocation(my_latitude, my_longitude, 1);
            if (address != null && address.size() > 0) {
                nowAddress = address.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(FindAEDActivity.this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng sender = new LatLng(sender_latitude, sender_longitude);
        MarkerOptions sendermarkerOptions = new MarkerOptions();
        sendermarkerOptions.position(sender);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.sendermarker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap senderMarker = Bitmap.createScaledBitmap(bitmap, 104, 148, false);
        sendermarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(senderMarker));
        map.addMarker(sendermarkerOptions);

        LatLng now = new LatLng(my_latitude, my_longitude);
        MarkerOptions mymarkerOptions = new MarkerOptions();
        mymarkerOptions.position(now);


        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.mymarker);
        Bitmap bitmap2 = bitmapdraw2.getBitmap();
        Bitmap myMarker = Bitmap.createScaledBitmap(bitmap2, 104, 148, false);
        mymarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(myMarker));
        map.addMarker(mymarkerOptions);

        LatLng aed = new LatLng(aed_latitude, aed_longitude);
        MarkerOptions aedmarkerOptions = new MarkerOptions();
        aedmarkerOptions.position(aed);


        BitmapDrawable bitmapdraw3 = (BitmapDrawable) getResources().getDrawable(R.drawable.aed);
        Bitmap bitmap3 = bitmapdraw3.getBitmap();
        Bitmap aedMarker = Bitmap.createScaledBitmap(bitmap3, 88, 80, false);
        aedmarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(aedMarker));
        map.addMarker(aedmarkerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 15));

    }

    /*
    public Location findMyLocation() {
        //**gps 기능이 켜졌는지 확인하는 코드가 필요합니다,
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            Toast.makeText(getApplicationContext(), "먼저 위치 권한을 확인해주세요", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    */
}

