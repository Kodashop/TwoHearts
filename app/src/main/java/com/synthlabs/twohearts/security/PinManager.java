package com.synthlabs.twohearts.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * App-lock PIN storage. The PIN itself is never stored — only a salted SHA-256
 * hash, kept inside EncryptedSharedPreferences.
 */
public final class PinManager {

    private static final String FILE = "twohearts_secure";
    private static final String KEY_HASH = "pin_hash";
    private static final String KEY_SALT = "pin_salt";
    private static final String KEY_HINT = "pin_hint";

    private PinManager() { }

    private static SharedPreferences prefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context.getApplicationContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context.getApplicationContext(),
                    FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (Exception e) {
            // Fallback keeps the app usable on devices with a broken keystore.
            return context.getApplicationContext()
                    .getSharedPreferences(FILE + "_fallback", Context.MODE_PRIVATE);
        }
    }

    public static boolean hasPin(Context context) {
        return prefs(context).getString(KEY_HASH, null) != null;
    }

    public static void setPin(Context context, String pin) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP);
        prefs(context).edit()
                .putString(KEY_SALT, saltB64)
                .putString(KEY_HASH, hash(pin, saltB64))
                .apply();
    }

    public static boolean verify(Context context, String pin) {
        SharedPreferences p = prefs(context);
        String stored = p.getString(KEY_HASH, null);
        String salt = p.getString(KEY_SALT, null);
        if (stored == null || salt == null) {
            return false;
        }
        return stored.equals(hash(pin, salt));
    }

    public static void clear(Context context) {
        prefs(context).edit().remove(KEY_HASH).remove(KEY_SALT).remove(KEY_HINT).apply();
    }

    public static void setHint(Context context, String hint) {
        prefs(context).edit().putString(KEY_HINT, hint).apply();
    }

    public static String getHint(Context context) {
        return prefs(context).getString(KEY_HINT, null);
    }

    private static String hash(String pin, String saltB64) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.decode(saltB64, Base64.NO_WRAP));
            byte[] out = digest.digest(pin.getBytes("UTF-8"));
            return Base64.encodeToString(out, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("PIN hashing unavailable", e);
        }
    }
}
