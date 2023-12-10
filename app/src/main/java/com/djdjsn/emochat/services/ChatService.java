package com.djdjsn.emochat.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.djdjsn.emochat.EmoChatApplication;
import com.djdjsn.emochat.R;
import com.djdjsn.emochat.data.PreferencesData;
import com.djdjsn.emochat.data.messagenotice.MessageNoticeRepository;
import com.djdjsn.emochat.data.user.User;
import com.djdjsn.emochat.data.user.UserRepository;
import com.djdjsn.emochat.ui.auth.AuthActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ChatService extends Service {

    public static final int NOTIFICATION_ID_FOREGROUND = 100;
    public static final int NOTIFICATION_ID_CHAT = 101;

    private NotificationManager notificationManager;

    private PreferencesData preferencesData;
    private MessageNoticeRepository messageNoticeRepository;
    private UserRepository userRepository;

    private Thread thread;
    private boolean isRunning;


    public ChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        preferencesData = new PreferencesData(getApplication());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        messageNoticeRepository = new MessageNoticeRepository(firestore);
        userRepository = new UserRepository(firestore);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(NOTIFICATION_ID_FOREGROUND, createForegroundNotification());

        isRunning = true;
        thread = new Thread(() -> {
            while (isRunning) {
                try {
                    String currentUid = preferencesData.getCurrentUid();
                    if (currentUid != null) {
                        messageNoticeRepository.getRecentNoticeOf(currentUid,
                                messageNotice -> {
                                    if (messageNotice != null) {
                                        if (!preferencesData.isInChat()) {
                                            userRepository.getUser(messageNotice.getSenderUid(),
                                                    this::sendChatNotification,
                                                    Throwable::printStackTrace);
                                        }
                                        messageNoticeRepository.deleteNoticesOf(currentUid);
                                    }
                                },
                                Throwable::printStackTrace);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isRunning) {
            isRunning = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void sendChatNotification(User sender) {
        if (sender != null) {
            notificationManager.notify(NOTIFICATION_ID_CHAT, createChatNotification(sender));
        }
    }

    private Notification createForegroundNotification() {

        // 서비스 실행 시 노티피케이션을 구성하여 리턴한다
        Notification notification;

        notification = new Notification.Builder(this, EmoChatApplication.CHANNEL_ID_FOREGROUND)
                .setContentIntent(getActivityPendingIntent())
                .setContentTitle("EmoChat")
                .setContentText("EmoChat 실행 중")
                .setSmallIcon(R.drawable.ic_chat_service)
                .build();

        return notification;
    }


    private Notification createChatNotification(User sender) {

        String contentText = String.format(Locale.getDefault(),
                "%s님이 메세지를 보내셨습니다", sender.getNickname());

        return new Notification.Builder(this, EmoChatApplication.CHANNEL_ID_CHAT)
                .setContentIntent(getActivityPendingIntent())
                .setContentTitle("새 메세지")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_chat_service)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build();
    }

    private PendingIntent getActivityPendingIntent() {

        Intent activityIntent = new Intent(this, AuthActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}