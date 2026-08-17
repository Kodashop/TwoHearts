package com.synthlabs.twohearts.core;

/**
 * OWNER CONFIGURATION — feature defaults in one place.
 *
 * These are the values TwoHearts falls back to before the owner changes
 * anything in Settings. Editing a number here changes the default for a
 * fresh install (and for features that read the default every time).
 */
public final class AppConfig {

    private AppConfig() { }

    /* ---------------- Period Tracker defaults ---------------- */
    public static final int DEFAULT_CYCLE_LENGTH_DAYS = 28;
    public static final int DEFAULT_PERIOD_LENGTH_DAYS = 5;
    /** How many days before the estimated period the reminder fires. */
    public static final int PERIOD_REMINDER_DAYS_BEFORE = 2;
    public static final int PERIOD_REMINDER_HOUR = 9;
    public static final boolean DEFAULT_SHOW_FERTILE_WINDOW = false;
    public static final boolean DEFAULT_PARTNER_VISIBILITY = false;

    /* ---------------- Reminder defaults ---------------- */
    public static final int DEFAULT_REMINDER_HOUR = 9;
    public static final int DEFAULT_REMINDER_MINUTE = 0;
    public static final long SNOOZE_DURATION_MS = 60L * 60L * 1000L;
    /** Important dates notify at this hour on the day itself. */
    public static final int IMPORTANT_DATE_HOUR = 8;

    /* ---------------- App Lock defaults ---------------- */
    public static final long DEFAULT_LOCK_TIMEOUT_MS = 60L * 1000L;
    public static final int MIN_PIN_LENGTH = 4;
    public static final int MAX_PIN_LENGTH = 8;

    /* ---------------- Relationship milestones (days) ---------------- */
    public static final int[] MILESTONE_DAYS = {
            30, 100, 180, 200, 300, 365, 500, 730, 1000, 1095, 1460, 1825, 2000, 3000
    };

    /* ---------------- Content ---------------- */
    /** Folder inside app assets holding editable game content. */
    public static final String GAMES_ASSET_DIR = "games";
    /** Folder inside app assets holding other editable text content. */
    public static final String CONTENT_ASSET_DIR = "content";
}
