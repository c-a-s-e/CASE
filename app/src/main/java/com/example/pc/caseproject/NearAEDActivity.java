package com.example.pc.caseproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearAEDActivity extends FragmentActivity implements OnMapReadyCallback {
    double mylatitude, mylongitude, aedlatitude, aedlongitude;
    String aedAddress;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_aed);

        tv=findViewById(R.id.textView);

        FragmentManager fm=getSupportFragmentManager();
        SupportMapFragment f=(SupportMapFragment)fm.findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(NearAEDActivity.this);

        Intent intent = getIntent();
        aedAddress=intent.getStringExtra("aed_address");

        tv.setText(aedAddress);

    }

    @Override
    public void onMapReady(final GoogleMap map) {

        Intent intent = getIntent();
        AED_FIND_REQUEST myRequest = intent.getParcelableExtra("AED_find_request");

        mylatitude=myRequest.myLatitude;
        mylongitude=myRequest.myLongtitiude;

        aedlatitude=myRequest.aedLatitude;
        aedlongitude=myRequest.aedLongtitude;

        LatLng now = new LatLng(mylatitude, mylongitude);
        MarkerOptions mymarkerOptions = new MarkerOptions();
        mymarkerOptions.position(now);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.mymarker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap myMarker = Bitmap.createScaledBitmap(bitmap, 104, 148, false);
        mymarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(myMarker));
        map.addMarker(mymarkerOptions);


        LatLng aed = new LatLng(aedlatitude, aedlongitude);
        MarkerOptions aedmarkerOptions = new MarkerOptions();
        aedmarkerOptions.position(aed);


        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.aedmarker);
        Bitmap bitmap2 = bitmapdraw2.getBitmap();
        Bitmap aedMarker = Bitmap.createScaledBitmap(bitmap2, 104, 148, false);
        mymarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(aedMarker));
        map.addMarker(aedmarkerOptions);


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 15));

    }
}
