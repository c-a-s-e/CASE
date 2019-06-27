package com.example.pc.caseproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
    CPRButton cprButton;
    @BindView(R.id.aedButton)
    Button aedButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<Integer> missingPermissions;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS};
    EmergencyDialogFragment popup;
    ShowcaseDialog showcase;

    @Override
    protected void onResume() {
        if (popup != null) popup.dismiss();
        openTutorial();
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
        openTutorial();
    }

    //주변 AED 찾기 버튼 누르면 실행될 메서드 입니다.
    @OnClick(R.id.cprButton)
    public void onCPRButtonClicked(View v) {
        Intent intent = new Intent(this, HeartActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.aedButton)
    public void onAEDButtonClicked(View v) {
        Intent intent = new Intent(this, NearAEDActivity.class);
        startActivity(intent);
    }

    public void openTutorial() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("sFile", MODE_PRIVATE);
        boolean tutorial_opened = sharedPreferences.getBoolean("tutorial_flag", false);
        if (!tutorial_opened) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            showcase = new ShowcaseDialog();
            showcase.show(getSupportFragmentManager(), "showcase");
            ft.commit();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("tutorial_flag", true);
            editor.apply();
        }
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
            Log.d("lala3", Long.toString(sec));

            if (sec < 600) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                popup = new EmergencyDialogFragment();
                popup.message = HH + " 근처에서\nSOS 요청이 왔습니다.\n수락하시겠습니까?";
                popup.show(fm, "popup");
                ft.commit();
            }
        }
    }
}