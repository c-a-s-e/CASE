package com.example.pc.caseproject;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                CallWaitService.strstate = "Ring";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                CallWaitService.strstate = "Off";
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                CallWaitService.strstate = "ID";
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }

}
