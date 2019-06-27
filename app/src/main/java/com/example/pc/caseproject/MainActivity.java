package com.example.pc.caseproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    //private RequestQueue queue;
    private final static int LOCATION_REQ_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
        Log.d("mytoken",FirebaseInstanceId.getInstance().getToken()+"토큰입니다");
    }

    //주변 AED 찾기 버튼 누르면 실행될 메서드 입니다.
    public void AEDFindButtonClicked(View v){
        AED_FIND_REQUEST myAedRequest = new AED_FIND_REQUEST();
        Location myLocation = findMyLocation();
        myAedRequest.setMyLatitude(myLocation.getLatitude());
        myAedRequest.setMyLongtitiude(myLocation.getLongitude());
        //이 메서드 안에서 자동으로 액티비티 넘어갑니다.
        AEDandSOScallUtil.getAEDdataFromAPIandSet(MainActivity.this,myLocation,myAedRequest,false,true);
    }


    public void getLocationPermission(){
        //위치권한 수용 요구하기
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(check == PackageManager.PERMISSION_GRANTED) return; //수신 권한 이미 존재
        else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(getApplicationContext(),"위치 권한을 허락해야 서비스 이용이 가능합니다.", Toast.LENGTH_LONG);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQ_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==LOCATION_REQ_CODE){
            if(grantResults!=null && grantResults.length!=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Log.d("Permission","사용자가 권한 승인");
            else Log.d("permission","사용자가 권한 거부");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //자신의 현재 위치를 파악하는 메서드 입니다.
    public Location findMyLocation(){
        //**gps 기능이 켜졌는지 확인하는 코드가 필요합니다,
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            getLocationPermission();
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            return myLocation;
        }
        else{
            Toast.makeText(getApplicationContext(),"먼저 위치 권한을 확인해주세요",Toast.LENGTH_LONG).show();
            return null;
        }
    }



}
