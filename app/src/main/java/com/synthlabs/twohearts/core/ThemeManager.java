package com.synthlabs.twohearts.core;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

/** Theme (light/dark/system) applied from a single place. */
public final class ThemeManager {

    public static final int MODE_SYSTEM = 0;
    public static final int MODE_LIGHT = 1;
    public static final int MODE_DARK = 2;

    private ThemeManager() { }

    public static void applySavedTheme(Context context) {
        apply(Prefs.getInt(context, Prefs.KEY_THEME, MODE_SYSTEM));
    }

    public static void setMode(Context context, int mode) {
        Prefs.setInt(context, Prefs.KEY_THEME, mode);
        apply(mode);
    }

    private static void apply(int mode) {
        switch (mode) {
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
