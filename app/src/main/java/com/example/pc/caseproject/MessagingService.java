package com.example.pc.caseproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private final double locationRange = 0.5; //AED를 주변으로 이 범위 안에 있을 때만 푸시 발생

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Log.e("type", data.get("type"));
        if (data.get("sender-token").equals(FirebaseInstanceId.getInstance().getToken())) return;
        if (data.get("type").equals("accept")) {
            Intent intent = new Intent();
            intent.setAction("accepted");
            sendBroadcast(intent);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return; //자기 위치 파악 불가하면 그냥 무시
            SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("sender-token", data.get("sender-token"));
            editor.putString("sender_address", data.get("sender_address"));
            editor.putString("sender_latitude", data.get("sender_latitude"));
            editor.putString("sender_longitude", data.get("sender_longitude"));
            editor.putString("date", data.get("date"));
            editor.putString("aed_address", data.get("aed_address"));
            editor.putString("aed_latitude", data.get("aed_latitude"));
            editor.putString("aed_longitude", data.get("aed_longitude"));


            //Receiver 위치 파악
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            editor.putString("my_latitude", myLocation.getLatitude() + "");
            editor.putString("my_longitude", myLocation.getLongitude() + "");
            editor.apply();

            //Sender가 본인이 아니고, 위치가 일정 범위 안에 있는 경우
            if (//!data.get("sender-token").equals(FirebaseInstanceId.getInstance().getToken()) &&
                    Math.abs(myLocation.getLatitude() - Double.parseDouble(data.get("aed_latitude"))) <= locationRange &&
                            Math.abs(myLocation.getLongitude() - Double.parseDouble(data.get("aed_longitude"))) <= locationRange)
                showNotification("주변에서 위급상황 발생", data.get("sender_address") + "에서 위급상황 발생. AED를 가져다주세요");
        }
    }

    private void showNotification(String title, String message) {
        Intent intent = new Intent(this, SOSActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*
            intent.putExtra("sender-token",data.get("sender-token"));
            intent.putExtra("sender_address",data.get("sender_address"));
            intent.putExtra("sender_latitude",data.get("sender_latitude"));
            intent.putExtra("sender_longitude",data.get("sender_longitude"));
            intent.putExtra("date",data.get("date"));
            intent.putExtra("aed_address",data.get("aed_address"));
            intent.putExtra("aed_latitude",data.get("aed_latitude"));
            intent.putExtra("aed_longitude",data.get("aed_longitude"));
            intent.putExtra("my_latitude",myLocation.getLatitude());
            intent.putExtra("my_longitude",myLocation.getLongitude());
            */
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "aed_alarm_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_red_48dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "aed_alarm_channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d("fcm-message", "Refreshed token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
    }
}
