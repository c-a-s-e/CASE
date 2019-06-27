package com.example.pc.caseproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SOSActivity extends AppCompatActivity implements OnMapReadyCallback {

    String sender_address, aed_address, date;
    Double sender_latitude, sender_longitude, aed_latitude, aed_longitude, my_latitude, my_longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

        //푸시 눌렀을 때,
        Intent intent = getIntent();
        if(intent != null){
            sender_address = intent.getStringExtra("sender_address");
            aed_address = intent.getStringExtra("aed_address");
            sender_latitude = Double.parseDouble(intent.getStringExtra("sender_latitude"));
            sender_longitude = Double.parseDouble(intent.getStringExtra("sender_longitude"));
            aed_latitude = Double.parseDouble(intent.getStringExtra("aed_latitude"));
            aed_longitude = Double.parseDouble(intent.getStringExtra("aed_longitude"));
            date=intent.getStringExtra("date");
            my_latitude=intent.getDoubleExtra("my_latitude", 0);
            my_longtitude=intent.getDoubleExtra("my_longitude", 0);

        }else{
            sharedPreferences.getString("seder-token", null);
            sharedPreferences.getString("sender_address", null);
            sharedPreferences.getString("sender_latitude", null);
            sharedPreferences.getString("sender_longitude", null);
            sharedPreferences.getString("data", null);
            sharedPreferences.getString("aed_address", null);
            sharedPreferences.getString("aed_latitude", null);
            sharedPreferences.getString("aed_longitude", null);

        }

        if(intent == null){
            aed_address=sharedPreferences.getString("sender_address", null);
        }



        FragmentManager fm=getSupportFragmentManager();
        SupportMapFragment f=(SupportMapFragment)fm.findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(SOSActivity.this);

    }
    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng sender = new LatLng(sender_latitude, sender_longitude);
        MarkerOptions sendermarkerOptions = new MarkerOptions();
        sendermarkerOptions.position(sender);
        map.addMarker(sendermarkerOptions);

        LatLng now = new LatLng(my_latitude, my_longtitude);
        MarkerOptions nowmarkerOptions = new MarkerOptions();
        nowmarkerOptions.position(now);
        map.addMarker(nowmarkerOptions);

        LatLng aed = new LatLng(aed_latitude, aed_longitude);
        MarkerOptions aedmarkerOptions = new MarkerOptions();
        aedmarkerOptions.position(aed);
        map.addMarker(aedmarkerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 15));

    }
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

}

