package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.TimelineEvent;

import java.util.ArrayList;
import java.util.List;

/** Relationship timeline entries (newest first). */
public class TimelineRepository {

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public List<TimelineEvent> list() {
        Cursor c = db().query("timeline_events", null, null, null, null, null, "date DESC, id DESC");
        List<TimelineEvent> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                TimelineEvent e = new TimelineEvent();
                e.id = c.getLong(c.getColumnIndexOrThrow("id"));
                e.title = c.getString(c.getColumnIndexOrThrow("title"));
                e.note = c.getString(c.getColumnIndexOrThrow("note"));
                e.date = c.getLong(c.getColumnIndexOrThrow("date"));
                e.icon = c.getString(c.getColumnIndexOrThrow("icon"));
                out.add(e);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public long save(TimelineEvent e) {
        ContentValues v = new ContentValues();
        v.put("title", e.title);
        v.put("note", e.note);
        v.put("date", e.date);
        v.put("icon", e.icon);
        if (e.id > 0) {
            db().update("timeline_events", v, "id = ?", new String[]{String.valueOf(e.id)});
            return e.id;
        }
        v.put("created_at", System.currentTimeMillis());
        e.id = db().insert("timeline_events", null, v);
        return e.id;
    }

    public void delete(long id) {
        db().delete("timeline_events", "id = ?", new String[]{String.valueOf(id)});
    }
}
