package com.example.pc.caseproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.cprButton)
    CPRButton cprButton;
    @BindView(R.id.aedButton)
    Button aedButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<Integer> missingPermissions;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS};

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        requestAllPermissions();
    }

    //주변 AED 찾기 버튼 누르면 실행될 메서드 입니다.
    @OnClick(R.id.cprButton)
    public void onCPRButtonClicked(View v) {
        Intent intent = new Intent(this, HeartActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.aedButton)
    public void onAEDButtonClicked(View v) {
        Intent intent = new Intent(this,NearAEDActivity.class);
        startActivity(intent);

        /*Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha_anim);
        myAnim.setRepeatMode(Animation.RESTART);

        AED_FIND_REQUEST myAedRequest = new AED_FIND_REQUEST();
        Location myLocation = findMyLocation();
        myAedRequest.setMyLatitude(myLocation.getLatitude());
        myAedRequest.setMyLongtitiude(myLocation.getLongitude());
        //이 메서드 안에서 자동으로 액티비티 넘어갑니다.
        AEDandSOScallUtil.getAEDdataFromAPI(this,myLocation,myAedRequest,false,true);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void requestAllPermissions() {
        if (missingPermissions == null) {
            requestPermissions(permissions, 87);
        } else if (missingPermissions.size() > 0) {
            for (int i : missingPermissions) {
                requestPermissions(new String[]{permissions[missingPermissions.get(i)]}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 87) {
            if (grantResults.length > 0) {
                missingPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        missingPermissions.add(i);
                    }
                }
            }
        }
    }

    /*
    //자신의 현재 위치를 파악하는 메서드 입니다.
    public Location findMyLocation() {
        //**gps 기능이 켜졌는지 확인하는 코드가 필요합니다,
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestAllPermissions();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            Toast.makeText(getApplicationContext(), "먼저 위치 권한을 확인해주세요", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.notification:
                try {
                    showDialog();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog() throws ParseException {
        SharedPreferences myPrefs = this.getSharedPreferences("sFile", MODE_PRIVATE);
        String sender_address = myPrefs.getString("sender_address", null);
        if(sender_address != null)
        {
            String date = myPrefs.getString("date", null);
            String Y = date.substring(29, 34);
            String M = date.substring(4, 7);
            switch (M)
            {
                case "Jan":
                    M = "01";
                    break;
                case "Feb":
                    M = "02";
                    break;
                case "Mar":
                    M = "03";
                    break;
                case "Apr":
                    M = "04";
                    break;
                case "May":
                    M = "05";
                    break;
                case "Jun":
                    M = "06";
                    break;
                case "Jul":
                    M = "07";
                    break;
                case "Aug":
                    M = "08";
                    break;
                case "Sep":
                    M = "09";
                    break;
                case "Oct":
                    M = "10";
                    break;
                case "Nov":
                    M = "11";
                    break;
                case "Dec":
                    M = "12";
                    break;
            }

            String D = date.substring(8, 10);
            String HH = date.substring(11, 19);
            date = Y + '-' + M + '-' + D + ' '+ HH;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date reqdate = formatter.parse(date);

            Date time = new Date();
            long sec = (time.getTime()-reqdate.getTime())/1000;
            Log.d("lala3", Long.toString(sec));

            if(sec < 10) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                EmergencyDialogFragment popup = new EmergencyDialogFragment();
                popup.show(fm, "popup");
                ft.commit();
            }
        }
    }
}