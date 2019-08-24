package com.example.pc.caseproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    public CallListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String state = intent.getStringExtra(tm.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(tm.EXTRA_INCOMING_NUMBER);

        if (state.equals(tm.EXTRA_STATE_RINGING)) {
            //((MainActivity)context).autoAnswer();

            try {
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audio.setStreamVolume(AudioManager.STREAM_RING,
                            audio.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);
                } catch (Exception ex) {
                    System.out.println("통화 안됨");
                }

                audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                        audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                audio.setMode(AudioManager.MODE_NORMAL);
                audio.setWiredHeadsetOn(false);
                audio.setSpeakerphoneOn(true);

            }
            if (state.equals(tm.EXTRA_STATE_OFFHOOK)) {

            }

        }
    interface CallListener{
        void onCallReceived();
    }

}
