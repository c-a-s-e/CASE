package com.example.pc.caseproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class CallWaitService extends Service {
    TelephonyManager telephonyManager;
    static public String strstate = "";
    Boolean isworking = false;
    int count =0;

    public CallWaitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ServiceStart() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, "Channel1")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Connected through SDL")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(Notification.PRIORITY_DEFAULT);


            Notification notification = builder.build();

            startForeground(1, notification);

        }
        else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);

            notificationManager.createNotificationChannel(notificationChannel);
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Connected through SDL")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .build();

            startForeground(1, notification);
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Action1")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceStart();
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        Toast.makeText(getApplication(),"시작!", Toast.LENGTH_LONG).show();
        if(isworking == false) {
            Thread bt = new Thread(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                    setAudioSoundMax();
                    boolean ring = true;
                    while (true) {
                        try {
                            Thread.sleep(200);
                            count++;
                             System.out.println(count);
                            if (count >= 5500) {
                                isworking= false;
                                return;

                            }
                            // System.out.println("시작");
                            ring = getTelecomState(ring);


                        } catch (Exception e) {
                            Log.e("BaseService", e.toString());
                        }
                    }
                }
            });
            bt.start();
            isworking = true;
        }


        return START_STICKY;
    }

    public boolean getTelecomState(boolean ring) {
        TelecomManager tm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tm = (TelecomManager) getApplication().getSystemService(Context.TELECOM_SERVICE);


            if (tm == null) {
                return false;
            }
            if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            tm.acceptRingingCall();
            setAudioSpeakPhone();

        }
        return false;
    }





    private void setAudioSoundMax(){
        AudioManager audio = (AudioManager)getApplication().getSystemService(Context.AUDIO_SERVICE);

        try {
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audio.setStreamVolume(AudioManager.STREAM_RING,
                    audio.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);
        }
        catch(Exception ex)
        {
            System.out.println("통화 안됨");
        }

        audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        audio.setMode(AudioManager.MODE_NORMAL);
        audio.setWiredHeadsetOn(false);
        audio.setSpeakerphoneOn(true);

    }

    private void setAudioSpeakPhone(){
        AudioManager audio = (AudioManager)getApplication().getSystemService(Context.AUDIO_SERVICE);
        audio.setWiredHeadsetOn(false);
        audio.setSpeakerphoneOn(true);
    }

}
