package com.example.pc.caseproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
        private final double locationRange = 0.21; //AED를 주변으로 이 범위 안에 있을 때만 푸시 발생

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            Log.d("accept","메시지받음");
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return; //자기 위치 파악 불가하면 그냥 무시

            Log.d("accept","메시지받음");
            Map<String, String> data = remoteMessage.getData();

            //Receiver 위치 파악
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //Sender가 본인이 아니고, 위치가 일정 범위 안에 있는 경우
            /*if(//!data.get("sender-token").equals(FirebaseInstanceId.getInstance().getToken()) &&
                    Math.abs(myLocation.getLatitude()-Double.parseDouble(data.get("aed_latitude")))<=locationRange &&
                            Math.abs(myLocation.getLongitude()-Double.parseDouble(data.get("aed_longitude")))<=locationRange)*/
                showNotification("주변에서 위급상황 발생",data.get("sender_address")+"에서 위급상황 발생. AED를 가져다주세요");

        }

        private void showNotification(String title, String message) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId = "aed_alarm_channel_id";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.noti_image)
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

        private void sendRegistrationToServer(String token){
        }
}
