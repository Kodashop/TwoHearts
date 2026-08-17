package com.synthlabs.twohearts.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Date helpers shared by every feature. Dates are stored as epoch millis. */
public final class DateUtils {

    private static final SimpleDateFormat LONG_DATE =
            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat MEDIUM_DATE =
            new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat DAY_MONTH =
            new SimpleDateFormat("MMM d", Locale.getDefault());
    private static final SimpleDateFormat TIME =
            new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final SimpleDateFormat MONTH_YEAR =
            new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private DateUtils() { }

    public static String formatLong(long millis) {
        return LONG_DATE.format(new Date(millis));
    }

    public static String formatMedium(long millis) {
        return MEDIUM_DATE.format(new Date(millis));
    }

    public static String formatDayMonth(long millis) {
        return DAY_MONTH.format(new Date(millis));
    }

    public static String formatTime(long millis) {
        return TIME.format(new Date(millis));
    }

    public static String formatMonthYear(long millis) {
        return MONTH_YEAR.format(new Date(millis));
    }

    public static long startOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long today() {
        return startOfDay(System.currentTimeMillis());
    }

    public static long addDays(long millis, int days) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTimeInMillis();
    }

    public static int daysBetween(long fromMillis, long toMillis) {
        long diff = startOfDay(toMillis) - startOfDay(fromMillis);
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }

    public static long atTime(long dayMillis, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startOfDay(dayMillis));
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTimeInMillis();
    }

    /** Next occurrence of a month/day (used for birthdays and anniversaries). */
    public static long nextAnniversary(long originalDate, int hour) {
        Calendar original = Calendar.getInstance();
        original.setTimeInMillis(originalDate);
        Calendar next = Calendar.getInstance();
        next.setTimeInMillis(System.currentTimeMillis());
        next.set(Calendar.MONTH, original.get(Calendar.MONTH));
        next.set(Calendar.DAY_OF_MONTH, original.get(Calendar.DAY_OF_MONTH));
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) {
            next.add(Calendar.YEAR, 1);
        }
        return next.getTimeInMillis();
    }
}
