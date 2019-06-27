package com.example.pc.caseproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class SOSActivity extends AppCompatActivity implements OnMapReadyCallback {

    String sender_address, aed_address, date, sender_token, nowAddress;
    Double sender_latitude, sender_longitude, aed_latitude, aed_longitude, my_latitude, my_longitude;
    TextView aedTextView, senderAddress, myAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
        setActionBar((Toolbar) findViewById(R.id.toolbar));
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowHomeEnabled(true);
        //getActionBar().setTitle("주변 AED 위치");
        showDialog();


        /*
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

        }else{ */
            sender_token = sharedPreferences.getString("sender-token", null);
            sender_address = sharedPreferences.getString("sender_address", null);
            sender_latitude = Double.parseDouble(sharedPreferences.getString("sender_latitude", null));
            sender_longitude = Double.parseDouble(sharedPreferences.getString("sender_longitude", null));
            date = sharedPreferences.getString("date", null);
            aed_address = sharedPreferences.getString("aed_address", null);
            aed_latitude = Double.parseDouble(sharedPreferences.getString("aed_latitude", null));
            aed_longitude = Double.parseDouble(sharedPreferences.getString("aed_longitude", null));
            my_latitude =  Double.parseDouble(sharedPreferences.getString("my_latitude", null));
            my_longitude = Double.parseDouble(sharedPreferences.getString("my_longitude", null));
        //}
        AEDandSOScallUtil.sendAccept(this,sender_token);

        Geocoder mGeoCoder = new Geocoder(SOSActivity.this, Locale.KOREA);
        List<Address> address;

        try{
            if(mGeoCoder !=null){
                address=mGeoCoder.getFromLocation(my_latitude, my_longitude, 1);
                if(address != null && address.size()>0){
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress = currentLocationAddress;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }


        aedTextView=findViewById(R.id.address);
        aedTextView.setText(aed_address);

        senderAddress=findViewById(R.id.myAddress);
        senderAddress.setText(sender_address);

        myAddress=findViewById(R.id.aedAddress);
        myAddress.setText(nowAddress);


        FragmentManager fm=getSupportFragmentManager();
        SupportMapFragment f=(SupportMapFragment)fm.findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(SOSActivity.this);

    }
    private void showDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LoadingDialogFragment popup = new LoadingDialogFragment();
        popup.show(getSupportFragmentManager(), "loading");
        ft.commit();
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

