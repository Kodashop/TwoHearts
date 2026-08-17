package com.synthlabs.twohearts.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.synthlabs.twohearts.core.AppConfig;
import com.synthlabs.twohearts.core.DateUtils;
import com.synthlabs.twohearts.data.model.ImportantDate;
import com.synthlabs.twohearts.data.model.PeriodCycle;
import com.synthlabs.twohearts.data.model.PeriodSettings;
import com.synthlabs.twohearts.data.model.Reminder;
import com.synthlabs.twohearts.data.repo.PeriodRepository;
import com.synthlabs.twohearts.data.repo.ProfileRepository;
import com.synthlabs.twohearts.data.repo.ReminderRepository;
import com.synthlabs.twohearts.domain.PeriodEngine;

import java.util.List;

/**
 * Turns reminder rows into AlarmManager alarms.
 *
 * Everything is derived from the database, so {@link #rescheduleAll} can safely
 * run after a reboot, a time change or an app update.
 */
public final class ReminderScheduler {

    public static final String EXTRA_REMINDER_ID = "reminder_id";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_BODY = "body";
    public static final String EXTRA_CHANNEL = "channel";

    private ReminderScheduler() { }

    public static void schedule(Context context, Reminder reminder) {
        if (!reminder.enabled || reminder.triggerAt <= 0) {
            cancel(context, reminder.id);
            return;
        }
        AlarmManager manager = context.getSystemService(AlarmManager.class);
        if (manager == null) {
            return;
        }
        long triggerAt = reminder.triggerAt;
        if (triggerAt <= System.currentTimeMillis()) {
            triggerAt = nextOccurrence(reminder);
            if (triggerAt <= System.currentTimeMillis()) {
                return; // one-off reminder already in the past
            }
        }
        PendingIntent intent = pendingIntent(context, reminder);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
                manager.set(AlarmManager.RTC_WAKEUP, triggerAt, intent);
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, intent);
            }
        } catch (SecurityException e) {
            manager.set(AlarmManager.RTC_WAKEUP, triggerAt, intent);
        }
    }

    public static void cancel(Context context, long reminderId) {
        AlarmManager manager = context.getSystemService(AlarmManager.class);
        if (manager == null) {
            return;
        }
        Intent intent = new Intent(context, ReminderAlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, (int) reminderId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        manager.cancel(pending);
    }

    /** Rebuilds every alarm: manual reminders, important dates and period reminders. */
    public static void rescheduleAll(Context context) {
        ReminderRepository reminders = new ReminderRepository();
        syncImportantDates(context, reminders);
        syncPeriodReminder(context, reminders);
        for (Reminder reminder : reminders.list(true)) {
            schedule(context, reminder);
        }
    }

    /** Advances a repeating reminder after it fires; disables one-off reminders. */
    public static void advanceAfterFire(Context context, Reminder reminder) {
        ReminderRepository repo = new ReminderRepository();
        if (ReminderRepository.REPEAT_NONE.equals(reminder.repeatType)) {
            repo.setEnabled(reminder.id, false);
            return;
        }
        long next = nextOccurrence(reminder);
        repo.setTriggerAt(reminder.id, next);
        reminder.triggerAt = next;
        schedule(context, reminder);
    }

    private static long nextOccurrence(Reminder reminder) {
        long next = reminder.triggerAt;
        long now = System.currentTimeMillis();
        int step;
        switch (reminder.repeatType == null ? "none" : reminder.repeatType) {
            case ReminderRepository.REPEAT_DAILY:
                step = 1;
                break;
            case ReminderRepository.REPEAT_WEEKLY:
                step = 7;
                break;
            case ReminderRepository.REPEAT_MONTHLY:
                step = 30;
                break;
            case ReminderRepository.REPEAT_YEARLY:
                step = 365;
                break;
            default:
                return reminder.triggerAt;
        }
        while (next <= now) {
            next = DateUtils.addDays(next, step);
        }
        return next;
    }

    private static PendingIntent pendingIntent(Context context, Reminder reminder) {
        Intent intent = new Intent(context, ReminderAlarmReceiver.class);
        intent.putExtra(EXTRA_REMINDER_ID, reminder.id);
        intent.putExtra(EXTRA_TITLE, reminder.title);
        intent.putExtra(EXTRA_BODY, reminder.note);
        intent.putExtra(EXTRA_CHANNEL, reminder.channel);
        return PendingIntent.getBroadcast(context, (int) reminder.id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** Keeps one reminder row per important date that has reminders switched on. */
    private static void syncImportantDates(Context context, ReminderRepository repo) {
        List<ImportantDate> dates = new ProfileRepository().listDates();
        for (ImportantDate date : dates) {
            Reminder existing = repo.findBySource("important_date", date.id);
            if (!date.reminderEnabled) {
                if (existing != null) {
                    cancel(context, existing.id);
                    repo.delete(existing.id);
                }
                continue;
            }
            Reminder reminder = existing == null ? new Reminder() : existing;
            reminder.title = date.title;
            reminder.note = null;
            reminder.channel = NotificationChannels.DATES;
            reminder.enabled = true;
            reminder.sourceType = "important_date";
            reminder.sourceId = date.id;
            reminder.repeatType = date.recurring
                    ? ReminderRepository.REPEAT_YEARLY : ReminderRepository.REPEAT_NONE;
            reminder.triggerAt = date.recurring
                    ? DateUtils.nextAnniversary(date.date, AppConfig.IMPORTANT_DATE_HOUR)
                    : DateUtils.atTime(date.date, AppConfig.IMPORTANT_DATE_HOUR, 0);
            repo.save(reminder);
        }
    }

    /** Recomputes the "period expected soon" reminder from the latest prediction. */
    private static void syncPeriodReminder(Context context, ReminderRepository repo) {
        PeriodRepository periodRepo = new PeriodRepository();
        PeriodSettings settings = periodRepo.getSettings();
        Reminder existing = repo.findBySource("period", 1);
        List<PeriodCycle> cycles = periodRepo.listCycles();
        PeriodEngine.Prediction prediction = PeriodEngine.predict(cycles, settings);

        if (!settings.reminderExpected || !prediction.hasData) {
            if (existing != null) {
                cancel(context, existing.id);
                repo.delete(existing.id);
            }
            return;
        }
        Reminder reminder = existing == null ? new Reminder() : existing;
        reminder.title = context.getString(com.synthlabs.twohearts.R.string.period_reminder_title);
        reminder.note = context.getString(com.synthlabs.twohearts.R.string.period_reminder_body);
        reminder.channel = NotificationChannels.PERIOD;
        reminder.enabled = true;
        reminder.repeatType = ReminderRepository.REPEAT_NONE;
        reminder.sourceType = "period";
        reminder.sourceId = 1;
        reminder.triggerAt = DateUtils.atTime(
                DateUtils.addDays(prediction.nextStart, -AppConfig.PERIOD_REMINDER_DAYS_BEFORE),
                AppConfig.PERIOD_REMINDER_HOUR, 0);
        repo.save(reminder);
    }
}
