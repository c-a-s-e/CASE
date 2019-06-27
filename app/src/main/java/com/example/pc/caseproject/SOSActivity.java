package com.example.pc.caseproject;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SOSActivity extends AppCompatActivity implements EmergencyDialogFragment.DialogActionListener {

    String sender_address, aed_address, date;
    Double sender_latitude, sender_longitude, aed_latitude, aed_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

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
        }
        showDialog();
    }

    private void showDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        EmergencyDialogFragment popup = new EmergencyDialogFragment();
        popup.setDialogActionListener(this);
        popup.show(fm, "popup");
        ft.commit();
    }

    @Override
    public void onDialogDismiss() {
    }
}

