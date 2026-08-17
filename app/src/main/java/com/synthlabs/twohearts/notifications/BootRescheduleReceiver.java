package com.synthlabs.twohearts.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;

/**
 * Alarms are cleared by a reboot, a timezone change or an app update, so every
 * enabled reminder is rebuilt from the database when those events arrive.
 */
public class BootRescheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TwoHeartsDatabase.init(context);
        NotificationChannels.ensureChannels(context);
        ReminderScheduler.rescheduleAll(context);
    }
}
