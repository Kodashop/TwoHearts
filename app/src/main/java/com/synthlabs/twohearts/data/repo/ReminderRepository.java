package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Reminders. The row is the source of truth; AlarmManager registrations are
 * derived from it by {@link com.synthlabs.twohearts.notifications.ReminderScheduler}.
 */
public class ReminderRepository {

    public static final String REPEAT_NONE = "none";
    public static final String REPEAT_DAILY = "daily";
    public static final String REPEAT_WEEKLY = "weekly";
    public static final String REPEAT_MONTHLY = "monthly";
    public static final String REPEAT_YEARLY = "yearly";

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public List<Reminder> list(boolean onlyEnabled) {
        Cursor c = db().query("reminders", null,
                onlyEnabled ? "enabled = 1" : null, null, null, null, "trigger_at ASC");
        List<Reminder> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                out.add(map(c));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public List<Reminder> upcoming(int limit) {
        Cursor c = db().query("reminders", null, "enabled = 1 AND trigger_at >= ?",
                new String[]{String.valueOf(System.currentTimeMillis())},
                null, null, "trigger_at ASC", String.valueOf(limit));
        List<Reminder> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                out.add(map(c));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public Reminder get(long id) {
        Cursor c = db().query("reminders", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? map(c) : null;
        } finally {
            c.close();
        }
    }

    public Reminder findBySource(String sourceType, long sourceId) {
        Cursor c = db().query("reminders", null, "source_type = ? AND source_id = ?",
                new String[]{sourceType, String.valueOf(sourceId)}, null, null, null, "1");
        try {
            return c.moveToFirst() ? map(c) : null;
        } finally {
            c.close();
        }
    }

    public long save(Reminder r) {
        ContentValues v = new ContentValues();
        v.put("title", r.title);
        v.put("note", r.note);
        v.put("trigger_at", r.triggerAt);
        v.put("repeat_type", r.repeatType == null ? REPEAT_NONE : r.repeatType);
        v.put("enabled", r.enabled ? 1 : 0);
        v.put("channel", r.channel == null ? "reminders" : r.channel);
        v.put("source_type", r.sourceType);
        v.put("source_id", r.sourceId);
        if (r.id > 0) {
            db().update("reminders", v, "id = ?", new String[]{String.valueOf(r.id)});
            return r.id;
        }
        v.put("created_at", System.currentTimeMillis());
        r.id = db().insert("reminders", null, v);
        return r.id;
    }

    public void setEnabled(long id, boolean enabled) {
        ContentValues v = new ContentValues();
        v.put("enabled", enabled ? 1 : 0);
        db().update("reminders", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void setTriggerAt(long id, long triggerAt) {
        ContentValues v = new ContentValues();
        v.put("trigger_at", triggerAt);
        db().update("reminders", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        db().delete("reminders", "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteBySource(String sourceType, long sourceId) {
        db().delete("reminders", "source_type = ? AND source_id = ?",
                new String[]{sourceType, String.valueOf(sourceId)});
    }

    private Reminder map(Cursor c) {
        Reminder r = new Reminder();
        r.id = c.getLong(c.getColumnIndexOrThrow("id"));
        r.title = c.getString(c.getColumnIndexOrThrow("title"));
        r.note = c.getString(c.getColumnIndexOrThrow("note"));
        r.triggerAt = c.getLong(c.getColumnIndexOrThrow("trigger_at"));
        r.repeatType = c.getString(c.getColumnIndexOrThrow("repeat_type"));
        r.enabled = c.getInt(c.getColumnIndexOrThrow("enabled")) == 1;
        r.channel = c.getString(c.getColumnIndexOrThrow("channel"));
        r.sourceType = c.getString(c.getColumnIndexOrThrow("source_type"));
        r.sourceId = c.getLong(c.getColumnIndexOrThrow("source_id"));
        return r;
    }
}
