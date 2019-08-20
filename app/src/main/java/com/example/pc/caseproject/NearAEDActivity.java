package com.example.pc.caseproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
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

public class NearAEDActivity extends FragmentActivity implements OnMapReadyCallback, AEDUtil.APIListener {
    double mylatitude, mylongitude, aedlatitude, aedlongitude;
    private AED_FIND_REQUEST myRequest;
    private SupportMapFragment mapFragment;
    String aedAddress, nowAddress;
    TextView aedTextView, myAddress, aedTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_aed);
        setActionBar((Toolbar) findViewById(R.id.toolbar));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("주변 AED 위치");
        showDialog();


        myRequest = new AED_FIND_REQUEST();
        Location myLocation = findMyLocation();

        mylatitude = myLocation.getLatitude();
        mylongitude = myLocation.getLongitude();
        myRequest.setMyLatitude(mylatitude);
        myRequest.setMyLongitude(mylongitude);

        Geocoder mGeoCoder = new Geocoder(NearAEDActivity.this, Locale.KOREA);
        List<Address> address;

        try {
            if (mGeoCoder != null) {
                address = mGeoCoder.getFromLocation(mylatitude, mylongitude, 1);
                if (address != null && address.size() > 0) {
                    nowAddress = address.get(0).getAddressLine(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AEDUtil.getAEDdataFromAPI(this, myLocation, myRequest, false, true, this);
        mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(NearAEDActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LoadingDialogFragment popup = new LoadingDialogFragment();
        popup.show(getSupportFragmentManager(), "loading");
        ft.commit();
    }

    @Override
    public void update() {
        mapFragment.getMapAsync(NearAEDActivity.this);
        aedAddress = myRequest.getAedAddress();
        Log.d("위치 ", aedAddress);

        myAddress = findViewById(R.id.myAddress);
        myAddress.setText(nowAddress);

        aedTextView2 = findViewById(R.id.aedAddress);
        aedTextView2.setText(aedAddress);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        aedlatitude = myRequest.aedLatitude;
        aedlongitude = myRequest.aedLongitude;

        LatLng now = new LatLng(mylatitude, mylongitude);
        MarkerOptions mymarkerOptions = new MarkerOptions();
        mymarkerOptions.position(now);


        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mymarker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap myMarker = Bitmap.createScaledBitmap(bitmap, 104, 148, false);
        mymarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(myMarker));
        map.addMarker(mymarkerOptions);


        LatLng aed = new LatLng(aedlatitude, aedlongitude);
        MarkerOptions aedmarkerOptions = new MarkerOptions();
        aedmarkerOptions.position(aed);


        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.aed);
        Bitmap bitmap2 = bitmapdraw2.getBitmap();
        Bitmap aedMarker = Bitmap.createScaledBitmap(bitmap2, 88, 80, false);
        aedmarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(aedMarker));
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
