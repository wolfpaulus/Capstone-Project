package com.techcasita.android.hwy67.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.techcasita.android.hwy67.MainActivity;
import com.techcasita.android.hwy67.R;
import com.techcasita.android.hwy67.TimePreference;
import com.techcasita.android.hwy67.content.UpdateService;

/**
 * If the BroadcastReceiver (com.google.android.gms.gcm.GcmReceiver) receives a message with
 * an Action: com.google.android.c2dm.intent.RECEIVE it will be handled here.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String LOG_TAG = MyGcmListenerService.class.getName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        final String topic = data.getCharSequence("topic", "").toString();
        if ("Demo".equals(topic)) {
            final String uid = data.getCharSequence("uid", "").toString();
            if (0 == uid.length() || !uid.equals(Secure.getString(getContentResolver(), Secure.ANDROID_ID))) {
                Log.d(LOG_TAG, "demo msg. ignored");
                return;
            }
        }

        Log.d(LOG_TAG, "GCM Notification Received, therefore start UpdateService");
        startService(new Intent(this, UpdateService.class));

        if (!TimePreference.isNowQuietTime(this)) {
            final String title = String.format("%s ( %s )", data.getCharSequence("title", "Hwy67").toString(), topic);
            final String content = data.getCharSequence("content", "News Alert").toString();
            final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
            final Notification notification =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_hwy67)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setContentIntent(contentIntent)
                            .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

            Log.d(LOG_TAG, "message: " + title + "/" + content + "/" + topic);
        }
    }
}
