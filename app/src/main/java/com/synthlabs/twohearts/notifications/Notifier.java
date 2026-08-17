package com.synthlabs.twohearts.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.core.Prefs;
import com.synthlabs.twohearts.data.repo.NotificationRepository;
import com.synthlabs.twohearts.ui.MainActivity;

/**
 * Posts a notification AND records it in the in-app notification centre, so the
 * owner can still find the message after swiping the system notification away.
 */
public final class Notifier {

    private Notifier() { }

    public static void show(Context context, String channel, int notificationId,
                            String title, String body, long reminderId) {
        new NotificationRepository().log(title, body, channel, reminderId);

        if (!channelEnabled(context, channel)) {
            return;
        }

        Intent open = new Intent(context, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent openIntent = PendingIntent.getActivity(context, notificationId, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_th_heart)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(openIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (NotificationChannels.PERIOD.equals(channel)) {
            builder.setVisibility(Notification.VISIBILITY_PRIVATE);
        }

        if (reminderId > 0) {
            builder.addAction(0, context.getString(R.string.action_snooze),
                    NotificationActionReceiver.snoozeIntent(context, reminderId, notificationId));
            builder.addAction(0, context.getString(R.string.action_mark_done),
                    NotificationActionReceiver.doneIntent(context, reminderId, notificationId));
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            try {
                manager.notify(notificationId, builder.build());
            } catch (SecurityException ignored) {
                // Notification permission not granted; the in-app log still has it.
            }
        }
    }

    private static boolean channelEnabled(Context context, String channel) {
        switch (channel) {
            case NotificationChannels.DATES:
                return Prefs.getBool(context, Prefs.KEY_NOTIF_DATES, true);
            case NotificationChannels.PERIOD:
                return Prefs.getBool(context, Prefs.KEY_NOTIF_PERIOD, true);
            case NotificationChannels.DAILY:
                return Prefs.getBool(context, Prefs.KEY_NOTIF_DAILY, false);
            default:
                return Prefs.getBool(context, Prefs.KEY_NOTIF_REMINDERS, true);
        }
    }
}
