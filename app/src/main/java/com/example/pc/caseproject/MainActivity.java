package com.example.pc.caseproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static int LOCATION_REQ_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
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

}
