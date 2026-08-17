package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

/** History of everything the app has notified about (the in-app inbox). */
public class NotificationRepository {

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public long log(String title, String body, String channel, long reminderId) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("body", body);
        v.put("channel", channel);
        v.put("reminder_id", reminderId);
        v.put("created_at", System.currentTimeMillis());
        v.put("read", 0);
        return db().insert("notification_log", null, v);
    }

    public List<NotificationItem> list() {
        Cursor c = db().query("notification_log", null, null, null, null, null,
                "created_at DESC", "200");
        List<NotificationItem> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                NotificationItem n = new NotificationItem();
                n.id = c.getLong(c.getColumnIndexOrThrow("id"));
                n.title = c.getString(c.getColumnIndexOrThrow("title"));
                n.body = c.getString(c.getColumnIndexOrThrow("body"));
                n.channel = c.getString(c.getColumnIndexOrThrow("channel"));
                n.reminderId = c.getLong(c.getColumnIndexOrThrow("reminder_id"));
                n.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
                n.read = c.getInt(c.getColumnIndexOrThrow("read")) == 1;
                out.add(n);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public int unreadCount() {
        Cursor c = db().rawQuery("SELECT COUNT(*) FROM notification_log WHERE read = 0", null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    public void markAllRead() {
        ContentValues v = new ContentValues();
        v.put("read", 1);
        db().update("notification_log", v, "read = 0", null);
    }

    public void clearAll() {
        db().delete("notification_log", null, null);
    }
}
