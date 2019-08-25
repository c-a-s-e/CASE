package com.example.pc.caseproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindAEDActivity extends AppCompatActivity implements OnMapReadyCallback {

    String sender_address, aed_address, date, sender_token, nowAddress;
    Double sender_latitude, sender_longitude, aed_latitude, aed_longitude, aed2_latitude, aed2_longitude, aed3_latitude, aed3_longitude;
    TextView aedTextView, senderAddress, myAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_aed);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("주변 AED 위치");

//        Geocoder mGeoCoder = new Geocoder(FindAEDActivity.this, Locale.ENGLISH);
//        List<Address> address;
//
//        try {
//            address = mGeoCoder.getFromLocation(my_latitude, my_longitude, 1);
//            if (address != null && address.size() > 0) {
//                nowAddress = address.get(0).getAddressLine(0);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        SupportMapFragment mapFragment = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(FindAEDActivity.this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        LocationList location = getLocation("");
        sender_latitude=location.latitude;
        sender_longitude=location.longitude;

        LatLng sender = new LatLng(sender_latitude, sender_longitude);
        MarkerOptions sendermarkerOptions = new MarkerOptions();
        sendermarkerOptions.position(sender);

        Circle circle = map.addCircle(new CircleOptions()
        .center(new LatLng(sender_latitude, sender_longitude))
        .radius(10)
        .strokeColor(Color.RED)
        .fillColor(Color.RED));

//        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.sendermarker);
//        Bitmap bitmap = bitmapdraw.getBitmap();
//        Bitmap senderMarker = Bitmap.createScaledBitmap(bitmap, 104, 148, false);
//        sendermarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(senderMarker));
//        map.addMarker(sendermarkerOptions);

//        LatLng now = new LatLng(my_latitude, my_longitude);
//        MarkerOptions mymarkerOptions = new MarkerOptions();
//        mymarkerOptions.position(now);
//
//
//        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.mymarker);
//        Bitmap bitmap2 = bitmapdraw2.getBitmap();
//        Bitmap myMarker = Bitmap.createScaledBitmap(bitmap2, 104, 148, false);
//        mymarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(myMarker));
//        map.addMarker(mymarkerOptions);

        LocationList aed1 = getLocation("Aed1");
        aed_latitude=aed1.latitude;
        aed_longitude=aed1.longitude;

        TextView aed1Address = (TextView)findViewById(R.id.aed1);
        aed1Address.setText(aed1.address);

        LatLng aed1Location = new LatLng(aed_latitude, aed_longitude);
        MarkerOptions aedmarkerOptions = new MarkerOptions();
        aedmarkerOptions.position(aed1Location);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.aed_1);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap aedMarker = Bitmap.createScaledBitmap(bitmap, 104, 168, false);
        aedmarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(aedMarker));
        map.addMarker(aedmarkerOptions);

        LocationList aed2 = getLocation("Aed2");
        aed2_latitude=aed2.latitude;
        aed2_longitude=aed2.longitude;

        TextView aed2Address = (TextView)findViewById(R.id.aed2);
        aed2Address.setText(aed2.address);

        LatLng aed2Location = new LatLng(aed2_latitude, aed2_longitude);
        MarkerOptions aedmarkerOptions2 = new MarkerOptions();
        aedmarkerOptions2.position(aed2Location);

        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.aed_2);
        Bitmap bitmap2 = bitmapdraw2.getBitmap();
        Bitmap aedMarker2 = Bitmap.createScaledBitmap(bitmap2, 104, 168, false);
        aedmarkerOptions2.icon(BitmapDescriptorFactory.fromBitmap(aedMarker2));
        map.addMarker(aedmarkerOptions2);

        LocationList aed3 = getLocation("Aed3");
        aed3_latitude=aed3.latitude;
        aed3_longitude=aed3.longitude;

        TextView aed3Address = (TextView)findViewById(R.id.aed3);
        aed3Address.setText(aed3.address);

        LatLng aed3Location = new LatLng(aed3_latitude, aed3_longitude);
        MarkerOptions aedmarkerOptions3 = new MarkerOptions();
        aedmarkerOptions3.position(aed3Location);

        BitmapDrawable bitmapdraw3 = (BitmapDrawable) getResources().getDrawable(R.drawable.aed_3);
        Bitmap bitmap3 = bitmapdraw3.getBitmap();
        Bitmap aedMarker3 = Bitmap.createScaledBitmap(bitmap3, 104, 168, false);
        aedmarkerOptions3.icon(BitmapDescriptorFactory.fromBitmap(aedMarker3));
        map.addMarker(aedmarkerOptions3);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sender, 17));

    }

    //중국인지 아닌지 체크 중국 아니면 정상 코드 작동시키면 됩니다.
    private static boolean outOfChina(double lat, double lon) {
        //작동 안되면 반대로..
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    class LocationList{

        public double latitude;
        public double longitude;
        public String address;

        public  LocationList(double la,double lo, String ad){
            latitude = la;
            longitude = lo;
            address = ad;
        }
    }
    private LocationList getLocation(String str){
        switch(str){
            case "Fire1": return new LocationList(29.8679664 ,121.5321831,"China Zhejiang Sheng, Ningbo Shi, Haishu Qu, Wujiatang Rd 108 Long, 1号-7号");
            case "Fire2": return new LocationList(29.8682428,121.6058129,"China, Zhejiang Sheng, Ningbo Shi, Jiang Dong Qu, 世纪大道世纪大道北段675号新天地国际商务大楼10号楼401");
            case "Aed1" : return new LocationList( 29.8660814 , 121.5566111,"China Life Tower Haishu Qu, Ningbo Shi, Zhejiang Sheng China");
            case "Aed2" : return new LocationList( 29.866688, 121.555978 ,"Haishu, Ningbo, Zhejiang, China");
            case "Aed3" : return new LocationList( 29.865306, 121.556984,"China, Zhejiang Sheng, Ningbo Shi, Haishu Qu, 灵桥路");
            default: return new LocationList( 29.866231 , 121.557556,"China Life Tower Haishu Qu, Ningbo Shi, Zhejiang Sheng China"); //대회장
        }
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

