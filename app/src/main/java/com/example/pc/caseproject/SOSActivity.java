package com.example.pc.caseproject;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SOSActivity extends AppCompatActivity implements OnMapReadyCallback {

    String sender_address, aed_address, date;
    Double sender_latitude, sender_longitude, aed_latitude, aed_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

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
        }

        FragmentManager fm=getSupportFragmentManager();
        SupportMapFragment f=(SupportMapFragment)fm.findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(SOSActivity.this);

    }
    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng now = new LatLng(sender_latitude, sender_longitude);
        MarkerOptions sendermarkerOptions = new MarkerOptions();
        sendermarkerOptions.position(now);
        map.addMarker(sendermarkerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 15));

    }

}

