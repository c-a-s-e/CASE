package com.example.pc.caseproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double patientLatitude, patientLongitude, myLatitude, myLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
//        getSupportActionBar().hide();
//
//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
        patientLatitude = 29.866231;
        patientLongitude = 121.557556;
        myLatitude = 29.8679664;
        myLongitude = 121.5321831;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_navigation);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng patient = new LatLng(patientLatitude, patientLongitude);
        MarkerOptions patientMarker = new MarkerOptions();
        patientMarker.position(patient);
        mMap.addMarker(patientMarker);

        LatLng myLocation = new LatLng(myLatitude, myLongitude);
        MarkerOptions myMarker = new MarkerOptions();
        myMarker.position(myLocation);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.location);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap myMarkerBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
        myMarker.icon(BitmapDescriptorFactory.fromBitmap(myMarkerBitmap));
        mMap.addMarker(myMarker);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void otherAEDbuttonClicked(View v){
        Intent intent = new Intent(this, FindAEDActivity.class);
        startActivity(intent);
    }
}
