package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.Memory;

import java.util.ArrayList;
import java.util.List;

/** Memories: create, edit, favourite, delete and search. */
public class MemoryRepository {

    public static final int FILTER_ALL = 0;
    public static final int FILTER_FAVORITES = 1;
    public static final int FILTER_WITH_PHOTO = 2;

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public List<Memory> list(int filter, String query) {
        StringBuilder where = new StringBuilder("1 = 1");
        List<String> args = new ArrayList<>();
        if (filter == FILTER_FAVORITES) {
            where.append(" AND favorite = 1");
        } else if (filter == FILTER_WITH_PHOTO) {
            where.append(" AND photo_uri IS NOT NULL AND photo_uri <> ''");
        }
        if (query != null && !query.trim().isEmpty()) {
            where.append(" AND (title LIKE ? OR story LIKE ? OR location LIKE ?)");
            String like = "%" + query.trim() + "%";
            args.add(like);
            args.add(like);
            args.add(like);
        }
        Cursor c = db().query("memories", null, where.toString(),
                args.toArray(new String[0]), null, null, "date DESC, id DESC");
        List<Memory> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                out.add(map(c));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public Memory get(long id) {
        Cursor c = db().query("memories", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? map(c) : null;
        } finally {
            c.close();
        }
    }

    public int count() {
        Cursor c = db().rawQuery("SELECT COUNT(*) FROM memories", null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    public long save(Memory m) {
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("title", m.title);
        v.put("story", m.story);
        v.put("date", m.date);
        v.put("location", m.location);
        v.put("photo_uri", m.photoUri);
        v.put("favorite", m.favorite ? 1 : 0);
        v.put("updated_at", now);
        if (m.id > 0) {
            db().update("memories", v, "id = ?", new String[]{String.valueOf(m.id)});
            return m.id;
        }
        v.put("created_at", now);
        m.id = db().insert("memories", null, v);
        return m.id;
    }

    public void setFavorite(long id, boolean favorite) {
        ContentValues v = new ContentValues();
        v.put("favorite", favorite ? 1 : 0);
        db().update("memories", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        db().delete("memories", "id = ?", new String[]{String.valueOf(id)});
    }

    private Memory map(Cursor c) {
        Memory m = new Memory();
        m.id = c.getLong(c.getColumnIndexOrThrow("id"));
        m.title = c.getString(c.getColumnIndexOrThrow("title"));
        m.story = c.getString(c.getColumnIndexOrThrow("story"));
        m.date = c.getLong(c.getColumnIndexOrThrow("date"));
        m.location = c.getString(c.getColumnIndexOrThrow("location"));
        m.photoUri = c.getString(c.getColumnIndexOrThrow("photo_uri"));
        m.favorite = c.getInt(c.getColumnIndexOrThrow("favorite")) == 1;
        m.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        return m;
    }
}
