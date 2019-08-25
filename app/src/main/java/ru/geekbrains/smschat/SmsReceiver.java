package ru.geekbrains.smschat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicInteger;

import ru.geekbrains.smschat.database.MessageEntity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SmsReceiver extends BroadcastReceiver {

    private OnMessageReceivedListener listener;
    private static AtomicInteger messageId = new AtomicInteger(0);
    static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String smsFromPhone = messages[0].getDisplayOriginatingAddress();
            StringBuilder body = new StringBuilder();
            for (SmsMessage message : messages) {
                body.append(message.getMessageBody());
            }
            String bodyText = body.toString();
            notify(context, smsFromPhone, bodyText);

            MessageEntity message = new MessageEntity(smsFromPhone, bodyText, true);
            if (listener != null) {
                listener.onMessageReceived(message);
            }

        }
    }

    private void notify(Context context, String addressFrom, String message) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showOldNotifications(context, addressFrom, message);
        } else {
            showNewNotifications(context, addressFrom, message);
        }
    }

    @SuppressLint("NewApi")
    private void showNewNotifications(Context context, String from, String message) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(from)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME", importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(
                new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(notificationChannel);
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    private void showOldNotifications(Context context, String addressFrom, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.format("Sms [%s]", addressFrom))
                .setContentText(message);
        Intent resultIntent = new Intent(context, SmsReceiver.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(messageId.incrementAndGet(), builder.build());
    }

    public void setListener(OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(MessageEntity message);
    }
}

