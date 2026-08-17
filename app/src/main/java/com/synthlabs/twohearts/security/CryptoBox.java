package com.synthlabs.twohearts.security;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * AES-GCM encryption backed by the Android Keystore.
 *
 * The key never leaves the device's hardware-backed keystore, so vault text is
 * unreadable even if the SQLite file is copied off the phone.
 */
public final class CryptoBox {

    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "twohearts_vault_key";
    private static final String TRANSFORM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;

    private CryptoBox() { }

    private static SecretKey key() throws Exception {
        KeyStore store = KeyStore.getInstance(KEYSTORE);
        store.load(null);
        if (store.containsAlias(KEY_ALIAS)) {
            return ((KeyStore.SecretKeyEntry) store.getEntry(KEY_ALIAS, null)).getSecretKey();
        }
        KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);
        generator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build());
        return generator.generateKey();
    }

    /** Returns base64(iv + ciphertext), or null when the input is null. */
    public static String encrypt(Context context, String plain) {
        if (plain == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, key());
            byte[] iv = cipher.getIV();
            byte[] data = cipher.doFinal(plain.getBytes("UTF-8"));
            byte[] combined = new byte[iv.length + data.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(data, 0, combined, iv.length, data.length);
            return Base64.encodeToString(combined, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    /** Reverses {@link #encrypt}; returns an empty string if the data is unreadable. */
    public static String decrypt(Context context, String cipherText) {
        if (cipherText == null) {
            return "";
        }
        try {
            byte[] combined = Base64.decode(cipherText, Base64.NO_WRAP);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_BITS, iv));
            byte[] plain = cipher.doFinal(combined, IV_LENGTH, combined.length - IV_LENGTH);
            return new String(plain, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
