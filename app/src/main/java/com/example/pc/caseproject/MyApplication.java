package com.example.pc.caseproject;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class MyApplication extends Application {
        @Override
        public void onCreate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel =
                        new NotificationChannel("aed_alarm_channel_id",
                                "aed_alarm_channel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("aed_alarm_channel"); notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.GREEN); notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            super.onCreate();
        }
    }
