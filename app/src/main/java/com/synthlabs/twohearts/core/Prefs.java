package com.synthlabs.twohearts.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Plain (non-secret) preferences. Anything sensitive (PIN hash, vault key)
 * goes through {@link com.synthlabs.twohearts.security.SecureStore} instead.
 *
 * All keys are declared here so no feature invents its own string literal.
 */
public final class Prefs {

    private static final String FILE = "twohearts_prefs";

    public static final String KEY_SETUP_DONE = "setup_done";
    public static final String KEY_PERMISSIONS_SHOWN = "permissions_shown";
    public static final String KEY_TEXT_SCALE = "text_scale";       // 0..3
    public static final String KEY_THEME = "theme_mode";            // 0 system, 1 light, 2 dark
    public static final String KEY_LOCK_ENABLED = "lock_enabled";
    public static final String KEY_LOCK_BIOMETRIC = "lock_biometric";
    public static final String KEY_LOCK_TIMEOUT_MS = "lock_timeout_ms";
    public static final String KEY_NOTIF_REMINDERS = "notif_reminders";
    public static final String KEY_NOTIF_DATES = "notif_dates";
    public static final String KEY_NOTIF_PERIOD = "notif_period";
    public static final String KEY_NOTIF_DAILY = "notif_daily";
    public static final String KEY_RECENT_SEARCHES = "recent_searches";

    private Prefs() { }

    public static SharedPreferences get(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean getBool(Context c, String key, boolean def) {
        return get(c).getBoolean(key, def);
    }

    public static void setBool(Context c, String key, boolean value) {
        get(c).edit().putBoolean(key, value).apply();
    }

    public static int getInt(Context c, String key, int def) {
        return get(c).getInt(key, def);
    }

    public static void setInt(Context c, String key, int value) {
        get(c).edit().putInt(key, value).apply();
    }

    public static long getLong(Context c, String key, long def) {
        return get(c).getLong(key, def);
    }

    public static void setLong(Context c, String key, long value) {
        get(c).edit().putLong(key, value).apply();
    }

    public static String getString(Context c, String key, String def) {
        return get(c).getString(key, def);
    }

    public static void setString(Context c, String key, String value) {
        get(c).edit().putString(key, value).apply();
    }
}
