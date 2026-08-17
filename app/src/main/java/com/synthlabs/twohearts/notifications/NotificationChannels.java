package com.synthlabs.twohearts.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.synthlabs.twohearts.R;

/** Notification channels, created once at app start. */
public final class NotificationChannels {

    public static final String REMINDERS = "reminders";
    public static final String DATES = "important_dates";
    public static final String PERIOD = "period";
    public static final String DAILY = "daily";

    private NotificationChannels() { }

    public static void ensureChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }
        create(manager, context, REMINDERS, R.string.channel_reminders,
                NotificationManager.IMPORTANCE_HIGH);
        create(manager, context, DATES, R.string.channel_dates,
                NotificationManager.IMPORTANCE_HIGH);
        // Period notifications stay discreet: no lock-screen preview of details.
        NotificationChannel period = new NotificationChannel(PERIOD,
                context.getString(R.string.channel_period), NotificationManager.IMPORTANCE_DEFAULT);
        period.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
        manager.createNotificationChannel(period);
        create(manager, context, DAILY, R.string.channel_daily,
                NotificationManager.IMPORTANCE_LOW);
    }

    private static void create(NotificationManager manager, Context context,
                               String id, int nameRes, int importance) {
        manager.createNotificationChannel(
                new NotificationChannel(id, context.getString(nameRes), importance));
    }
}
