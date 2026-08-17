package com.synthlabs.twohearts.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Reminder;
import com.synthlabs.twohearts.data.repo.ReminderRepository;

/** Fires when an alarm goes off: posts the notification and rolls repeats forward. */
public class ReminderAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(ReminderScheduler.EXTRA_REMINDER_ID, 0);
        String title = intent.getStringExtra(ReminderScheduler.EXTRA_TITLE);
        String body = intent.getStringExtra(ReminderScheduler.EXTRA_BODY);
        String channel = intent.getStringExtra(ReminderScheduler.EXTRA_CHANNEL);

        Reminder reminder = id > 0 ? new ReminderRepository().get(id) : null;
        if (reminder != null) {
            title = reminder.title;
            body = reminder.note;
            channel = reminder.channel;
        }
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name);
        }
        if (TextUtils.isEmpty(body)) {
            body = context.getString(R.string.reminder_default_body);
        }
        if (TextUtils.isEmpty(channel)) {
            channel = NotificationChannels.REMINDERS;
        }

        Notifier.show(context, channel, (int) (id == 0 ? System.currentTimeMillis() % 100000 : id),
                title, body, id);

        if (reminder != null) {
            ReminderScheduler.advanceAfterFire(context, reminder);
        }
    }
}
