package com.synthlabs.twohearts.core;

import android.app.Application;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.notifications.NotificationChannels;
import com.synthlabs.twohearts.notifications.ReminderScheduler;

/**
 * Application entry point.
 *
 * Responsibilities (kept deliberately small):
 *  - open the local SQLite database once
 *  - register notification channels
 *  - re-apply the saved theme
 *  - make sure every enabled reminder is registered with AlarmManager
 */
public class TwoHeartsApp extends Application {

    private static TwoHeartsApp instance;

    public static TwoHeartsApp get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        TwoHeartsDatabase.init(this);
        NotificationChannels.ensureChannels(this);
        ThemeManager.applySavedTheme(this);
        // Safety net: reminders are also rescheduled on boot, but a cold start
        // after a force-stop is the other moment alarms can be missing.
        ReminderScheduler.rescheduleAll(this);
        AppLockState.get(this).onAppStarted();
    }
}
