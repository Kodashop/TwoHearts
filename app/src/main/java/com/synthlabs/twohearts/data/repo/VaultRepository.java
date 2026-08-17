package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.VaultItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Private vault storage. Bodies are stored encrypted; only
 * {@link com.synthlabs.twohearts.security.CryptoBox} can read them back.
 */
public class VaultRepository {

    public static final String TYPE_NOTE = "note";
    public static final String TYPE_PHOTO = "photo";

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public List<VaultItem> list() {
        Cursor c = db().query("vault_items", null, null, null, null, null, "created_at DESC");
        List<VaultItem> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                VaultItem v = new VaultItem();
                v.id = c.getLong(c.getColumnIndexOrThrow("id"));
                v.title = c.getString(c.getColumnIndexOrThrow("title"));
                v.bodyCipher = c.getString(c.getColumnIndexOrThrow("body_cipher"));
                v.type = c.getString(c.getColumnIndexOrThrow("type"));
                v.mediaUri = c.getString(c.getColumnIndexOrThrow("media_uri"));
                v.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
                out.add(v);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public long save(VaultItem item) {
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("title", item.title);
        v.put("body_cipher", item.bodyCipher);
        v.put("type", item.type == null ? TYPE_NOTE : item.type);
        v.put("media_uri", item.mediaUri);
        v.put("updated_at", now);
        if (item.id > 0) {
            db().update("vault_items", v, "id = ?", new String[]{String.valueOf(item.id)});
            return item.id;
        }
        v.put("created_at", now);
        item.id = db().insert("vault_items", null, v);
        return item.id;
    }

    public void delete(long id) {
        db().delete("vault_items", "id = ?", new String[]{String.valueOf(id)});
    }

    public int count() {
        Cursor c = db().rawQuery("SELECT COUNT(*) FROM vault_items", null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }
}
