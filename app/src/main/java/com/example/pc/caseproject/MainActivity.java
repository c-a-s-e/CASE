package com.example.pc.caseproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

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
    Button cprButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<Integer> missingPermissions;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ANSWER_PHONE_CALLS};
    EmergencyDialogFragment popup;

    @Override
    protected void onResume() {
        if (popup != null) popup.dismiss();
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
        phoneNumber();
    }

    //주변 AED 찾기 버튼 누르면 실행될 메서드 입니다.
    @OnClick(R.id.cprButton)
    public void onCPRButtonClicked(View v) {
        requestAllPermissions();
        notificationinit();

        Intent intent = new Intent(this, HeartActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = this.getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tutorial_flag", false);
        editor.apply();
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
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
            case 0: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;
            }
        }
    }

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
        if (sender_address != null) {
            String date = myPrefs.getString("date", null);
            String Y = date.substring(29, 34);
            String M = date.substring(4, 7);
            switch (M) {
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
            date = Y + '-' + M + '-' + D + ' ' + HH;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date reqdate = formatter.parse(date);

            Date time = new Date();
            long sec = (time.getTime() - reqdate.getTime()) / 1000;

            if (sec < 600) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                popup = new EmergencyDialogFragment();
                popup.message = HH + " 근처에서\nSOS 요청이 왔습니다.\n수락하시겠습니까?";
                popup.show(fm, "popup");
                ft.commit();
            }
        } else {
            Toast.makeText(this, "현재 SOS 요청이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void phoneNumber(){
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String pn;
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        pn = tm.getLine1Number();
        if(pn.startsWith("+82")){
            pn=pn.replace("+82","0");
            AEDUtil.PHONE_NUM=pn;
        }
        Log.d("phone", pn);
    }

    public void notificationinit() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Intent backStartIntent = new Intent(MainActivity.this, CallWaitService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("channel1", "1번채널", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("1번채널입니다");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
            startForegroundService(backStartIntent);

        } else {
            startService(backStartIntent);
        }
    }
}