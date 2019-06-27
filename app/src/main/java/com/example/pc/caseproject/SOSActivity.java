package com.example.pc.caseproject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SOSActivity extends AppCompatActivity implements EmergencyDialogFragment.DialogActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
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
    public void onDialogDismiss()
    {

    }
}

