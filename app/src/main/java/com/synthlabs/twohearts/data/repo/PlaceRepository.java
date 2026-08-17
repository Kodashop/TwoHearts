package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.Place;

import java.util.ArrayList;
import java.util.List;

/** Places we've been and places on the wishlist. */
public class PlaceRepository {

    public static final String STATUS_VISITED = "visited";
    public static final String STATUS_WISHLIST = "wishlist";

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public List<Place> list(String status) {
        Cursor c = db().query("places", null,
                status == null ? null : "status = ?",
                status == null ? null : new String[]{status},
                null, null, "created_at DESC");
        List<Place> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Place p = new Place();
                p.id = c.getLong(c.getColumnIndexOrThrow("id"));
                p.name = c.getString(c.getColumnIndexOrThrow("name"));
                p.note = c.getString(c.getColumnIndexOrThrow("note"));
                p.address = c.getString(c.getColumnIndexOrThrow("address"));
                p.status = c.getString(c.getColumnIndexOrThrow("status"));
                p.photoUri = c.getString(c.getColumnIndexOrThrow("photo_uri"));
                p.visitedDate = c.getLong(c.getColumnIndexOrThrow("visited_date"));
                out.add(p);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public long save(Place p) {
        ContentValues v = new ContentValues();
        v.put("name", p.name);
        v.put("note", p.note);
        v.put("address", p.address);
        v.put("status", p.status == null ? STATUS_VISITED : p.status);
        v.put("photo_uri", p.photoUri);
        v.put("visited_date", p.visitedDate);
        if (p.id > 0) {
            db().update("places", v, "id = ?", new String[]{String.valueOf(p.id)});
            return p.id;
        }
        v.put("created_at", System.currentTimeMillis());
        p.id = db().insert("places", null, v);
        return p.id;
    }

    public void delete(long id) {
        db().delete("places", "id = ?", new String[]{String.valueOf(id)});
    }
}
