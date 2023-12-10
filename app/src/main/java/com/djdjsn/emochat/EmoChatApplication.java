package com.djdjsn.emochat;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class EmoChatApplication extends Application {


    public static final String CHANNEL_NAME_FOREGROUND = "foreground";
    public static final String CHANNEL_ID_FOREGROUND = "com.djdjsn.emochat.foreground";

    public static final String CHANNEL_NAME_CHAT = "chat";
    public static final String CHANNEL_ID_CHAT = "com.djdjsn.emochat.chat";

    @Override
    public void onCreate() {
        super.onCreate();

        // 앱이 실행될 때 노티피케이션 채널을 등록한다
        createNotificationChannels();
    }

    private void createNotificationChannels() {

        // 포그라운드 노티피케이션 채널을 생성한다
        NotificationChannel foregroundChannel = new NotificationChannel(
                CHANNEL_ID_FOREGROUND,
                CHANNEL_NAME_FOREGROUND,
                NotificationManager.IMPORTANCE_LOW
        );

        // 채팅 노티피케이션 채널을 등록한다
        NotificationChannel chatChannel = new NotificationChannel(
                CHANNEL_ID_CHAT,
                CHANNEL_NAME_CHAT,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(foregroundChannel);
        manager.createNotificationChannel(chatChannel);
    }

}
