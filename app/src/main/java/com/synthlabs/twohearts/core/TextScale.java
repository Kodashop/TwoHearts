package com.synthlabs.twohearts.core;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * System-wide text size setting (Small / Default / Large / Extra Large).
 *
 * Implemented by multiplying the configuration fontScale, so every "sp"
 * dimension in the app scales at once. Screens get this for free by
 * extending {@link com.synthlabs.twohearts.ui.common.BaseActivity}.
 */
public final class TextScale {

    public static final int SMALL = 0;
    public static final int DEFAULT = 1;
    public static final int LARGE = 2;
    public static final int XLARGE = 3;

    private static final float[] FACTORS = {0.9f, 1.0f, 1.15f, 1.3f};

    private TextScale() { }

    public static int getLevel(Context context) {
        int level = Prefs.getInt(context, Prefs.KEY_TEXT_SCALE, DEFAULT);
        return (level < 0 || level >= FACTORS.length) ? DEFAULT : level;
    }

    public static void setLevel(Context context, int level) {
        Prefs.setInt(context, Prefs.KEY_TEXT_SCALE, level);
    }

    public static float factor(Context context) {
        return FACTORS[getLevel(context)];
    }

    /** Wraps a base context so all sp values honour the app text-size setting. */
    public static Context wrap(Context base) {
        Configuration config = new Configuration(base.getResources().getConfiguration());
        Resources system = Resources.getSystem();
        float osScale = system.getConfiguration().fontScale;
        config.fontScale = osScale * factor(base);
        return base.createConfigurationContext(config);
    }
}
