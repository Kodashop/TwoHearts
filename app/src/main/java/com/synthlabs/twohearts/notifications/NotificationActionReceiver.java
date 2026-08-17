package com.synthlabs.twohearts.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synthlabs.twohearts.core.AppConfig;
import com.synthlabs.twohearts.data.model.Reminder;
import com.synthlabs.twohearts.data.repo.ReminderRepository;

/** Handles the Snooze / Mark done buttons on a reminder notification. */
public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String ACTION_SNOOZE = "com.synthlabs.twohearts.SNOOZE";
    private static final String ACTION_DONE = "com.synthlabs.twohearts.DONE";
    private static final String EXTRA_ID = "reminder_id";
    private static final String EXTRA_NOTIFICATION = "notification_id";

    public static PendingIntent snoozeIntent(Context context, long reminderId, int notificationId) {
        return build(context, ACTION_SNOOZE, reminderId, notificationId);
    }

    public static PendingIntent doneIntent(Context context, long reminderId, int notificationId) {
        return build(context, ACTION_DONE, reminderId, notificationId);
    }

    private static PendingIntent build(Context context, String action,
                                       long reminderId, int notificationId) {
        Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_ID, reminderId);
        intent.putExtra(EXTRA_NOTIFICATION, notificationId);
        int requestCode = (int) (reminderId * 10 + (ACTION_SNOOZE.equals(action) ? 1 : 2));
        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(EXTRA_ID, 0);
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION, 0);
        ReminderRepository repo = new ReminderRepository();
        Reminder reminder = repo.get(id);

        if (ACTION_SNOOZE.equals(intent.getAction()) && reminder != null) {
            reminder.triggerAt = System.currentTimeMillis() + AppConfig.SNOOZE_DURATION_MS;
            reminder.enabled = true;
            repo.save(reminder);
            ReminderScheduler.schedule(context, reminder);
        } else if (ACTION_DONE.equals(intent.getAction()) && reminder != null) {
            if (ReminderRepository.REPEAT_NONE.equals(reminder.repeatType)) {
                repo.setEnabled(reminder.id, false);
                ReminderScheduler.cancel(context, reminder.id);
            }
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.cancel(notificationId);
        }
    }
}
