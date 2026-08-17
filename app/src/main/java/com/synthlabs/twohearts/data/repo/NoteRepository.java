package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.Note;

import java.util.ArrayList;
import java.util.List;

/** Love notes: pinned notes float to the top, newest first after that. */
public class NoteRepository {

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public List<Note> list(String query) {
        String where = null;
        String[] args = null;
        if (query != null && !query.trim().isEmpty()) {
            where = "title LIKE ? OR body LIKE ?";
            String like = "%" + query.trim() + "%";
            args = new String[]{like, like};
        }
        Cursor c = db().query("notes", null, where, args, null, null,
                "pinned DESC, updated_at DESC");
        List<Note> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                out.add(map(c));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public Note get(long id) {
        Cursor c = db().query("notes", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? map(c) : null;
        } finally {
            c.close();
        }
    }

    public int count() {
        Cursor c = db().rawQuery("SELECT COUNT(*) FROM notes", null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    public long save(Note n) {
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("title", n.title);
        v.put("body", n.body);
        v.put("pinned", n.pinned ? 1 : 0);
        v.put("updated_at", now);
        if (n.id > 0) {
            db().update("notes", v, "id = ?", new String[]{String.valueOf(n.id)});
            return n.id;
        }
        v.put("created_at", now);
        n.id = db().insert("notes", null, v);
        return n.id;
    }

    public void setPinned(long id, boolean pinned) {
        ContentValues v = new ContentValues();
        v.put("pinned", pinned ? 1 : 0);
        db().update("notes", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        db().delete("notes", "id = ?", new String[]{String.valueOf(id)});
    }

    private Note map(Cursor c) {
        Note n = new Note();
        n.id = c.getLong(c.getColumnIndexOrThrow("id"));
        n.title = c.getString(c.getColumnIndexOrThrow("title"));
        n.body = c.getString(c.getColumnIndexOrThrow("body"));
        n.pinned = c.getInt(c.getColumnIndexOrThrow("pinned")) == 1;
        n.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        n.updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"));
        return n;
    }
}
