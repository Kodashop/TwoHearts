package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.MoodEntry;

import java.util.ArrayList;
import java.util.List;

/** Daily mood check-ins and their history. */
public class MoodRepository {

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public long add(String moodKey, String note, String owner) {
        ContentValues v = new ContentValues();
        v.put("mood_key", moodKey);
        v.put("note", note);
        v.put("owner", owner == null ? "me" : owner);
        v.put("created_at", System.currentTimeMillis());
        return db().insert("mood_entries", null, v);
    }

    public List<MoodEntry> recent(int limit) {
        Cursor c = db().query("mood_entries", null, null, null, null, null,
                "created_at DESC", String.valueOf(limit));
        List<MoodEntry> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                MoodEntry m = new MoodEntry();
                m.id = c.getLong(c.getColumnIndexOrThrow("id"));
                m.moodKey = c.getString(c.getColumnIndexOrThrow("mood_key"));
                m.note = c.getString(c.getColumnIndexOrThrow("note"));
                m.owner = c.getString(c.getColumnIndexOrThrow("owner"));
                m.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
                out.add(m);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public MoodEntry latest() {
        List<MoodEntry> list = recent(1);
        return list.isEmpty() ? null : list.get(0);
    }

    public void delete(long id) {
        db().delete("mood_entries", "id = ?", new String[]{String.valueOf(id)});
    }
}
