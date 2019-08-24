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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

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
            Intent intent = new Intent(this, ProviderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("sender-token", data.get("sender-token"));
            intent.putExtra("sender_address", data.get("sender_address"));
            intent.putExtra("sender_latitude", data.get("sender_latitude"));
            intent.putExtra("sender_longitude", data.get("sender_longitude"));
            intent.putExtra("date", data.get("date"));
            intent.putExtra("aed_address", data.get("a[ed_address"));
            intent.putExtra("aed_latitude", data.get("aed_latitude"));
            intent.putExtra("aed_longitude", data.get("aed_longitude"));
            Log.e("msg", data.get("aed_latitude")+"");

            //Receiver 위치 파악
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            intent.putExtra("my_latitude", myLocation.getLatitude());
            intent.putExtra("my_longitude", myLocation.getLongitude());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            showNotification("주변에서 위급상황 발생", data.get("sender_address") + "에서 위급상황 발생. AED를 가져다주세요", pendingIntent);
        }
    }

    private void showNotification(String title, String message, PendingIntent pendingIntent) {
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
