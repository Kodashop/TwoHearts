package com.synthlabs.twohearts.core;

import android.content.Context;

/**
 * Tracks whether the app currently needs the lock screen.
 * Kept out of the UI so any Activity can ask the same question.
 */
public final class AppLockState {

    private static AppLockState instance;

    private final Context context;
    private boolean unlocked;
    private long backgroundedAt;

    private AppLockState(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized AppLockState get(Context context) {
        if (instance == null) {
            instance = new AppLockState(context);
        }
        return instance;
    }

    public boolean isLockEnabled() {
        return Prefs.getBool(context, Prefs.KEY_LOCK_ENABLED, false);
    }

    public void onAppStarted() {
        unlocked = !isLockEnabled();
    }

    public void markUnlocked() {
        unlocked = true;
    }

    public void lockNow() {
        unlocked = false;
    }

    public void onActivityPaused() {
        backgroundedAt = System.currentTimeMillis();
    }

    /** Called when an Activity resumes: re-locks after the chosen timeout. */
    public boolean shouldPromptLock() {
        if (!isLockEnabled()) {
            return false;
        }
        if (!unlocked) {
            return true;
        }
        long timeout = Prefs.getLong(context, Prefs.KEY_LOCK_TIMEOUT_MS,
                AppConfig.DEFAULT_LOCK_TIMEOUT_MS);
        if (backgroundedAt == 0) {
            return false;
        }
        boolean expired = System.currentTimeMillis() - backgroundedAt >= timeout;
        if (expired) {
            unlocked = false;
        }
        return expired;
    }
}
