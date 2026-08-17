package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.ImportantDate;
import com.synthlabs.twohearts.data.model.Profile;
import com.synthlabs.twohearts.data.model.Relationship;

import java.util.ArrayList;
import java.util.List;

/** Profiles (me + partner), the relationship row and important dates. */
public class ProfileRepository {

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    /* ------------------- profiles ------------------- */

    public Profile getProfile(boolean partner) {
        Cursor c = db().query("profile", null, "is_partner = ?",
                new String[]{partner ? "1" : "0"}, null, null, "id ASC", "1");
        try {
            return c.moveToFirst() ? map(c) : null;
        } finally {
            c.close();
        }
    }

    public long saveProfile(Profile profile) {
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("is_partner", profile.isPartner ? 1 : 0);
        v.put("name", profile.name);
        v.put("nickname", profile.nickname);
        v.put("birthday", profile.birthday);
        v.put("photo_uri", profile.photoUri);
        v.put("updated_at", now);
        if (profile.id > 0) {
            db().update("profile", v, "id = ?", new String[]{String.valueOf(profile.id)});
            return profile.id;
        }
        v.put("created_at", now);
        profile.id = db().insert("profile", null, v);
        return profile.id;
    }

    private Profile map(Cursor c) {
        Profile p = new Profile();
        p.id = c.getLong(c.getColumnIndexOrThrow("id"));
        p.isPartner = c.getInt(c.getColumnIndexOrThrow("is_partner")) == 1;
        p.name = c.getString(c.getColumnIndexOrThrow("name"));
        p.nickname = c.getString(c.getColumnIndexOrThrow("nickname"));
        p.birthday = c.getLong(c.getColumnIndexOrThrow("birthday"));
        p.photoUri = c.getString(c.getColumnIndexOrThrow("photo_uri"));
        return p;
    }

    /* ------------------- relationship ------------------- */

    public Relationship getRelationship() {
        Cursor c = db().query("relationship", null, "id = 1", null, null, null, null);
        try {
            Relationship r = new Relationship();
            r.id = 1;
            if (c.moveToFirst()) {
                r.spaceName = c.getString(c.getColumnIndexOrThrow("space_name"));
                r.greeting = c.getString(c.getColumnIndexOrThrow("greeting"));
                r.startDate = c.getLong(c.getColumnIndexOrThrow("start_date"));
                r.status = c.getString(c.getColumnIndexOrThrow("status"));
            }
            return r;
        } finally {
            c.close();
        }
    }

    public void saveRelationship(Relationship r) {
        ContentValues v = new ContentValues();
        v.put("id", 1);
        v.put("space_name", r.spaceName);
        v.put("greeting", r.greeting);
        v.put("start_date", r.startDate);
        v.put("status", r.status);
        v.put("updated_at", System.currentTimeMillis());
        db().insertWithOnConflict("relationship", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /* ------------------- important dates ------------------- */

    public List<ImportantDate> listDates() {
        List<ImportantDate> out = new ArrayList<>();
        Cursor c = db().query("important_dates", null, null, null, null, null, "date ASC");
        try {
            while (c.moveToNext()) {
                ImportantDate d = new ImportantDate();
                d.id = c.getLong(c.getColumnIndexOrThrow("id"));
                d.title = c.getString(c.getColumnIndexOrThrow("title"));
                d.date = c.getLong(c.getColumnIndexOrThrow("date"));
                d.type = c.getString(c.getColumnIndexOrThrow("type"));
                d.recurring = c.getInt(c.getColumnIndexOrThrow("recurring")) == 1;
                d.reminderEnabled = c.getInt(c.getColumnIndexOrThrow("reminder_enabled")) == 1;
                out.add(d);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public long saveDate(ImportantDate d) {
        ContentValues v = new ContentValues();
        v.put("title", d.title);
        v.put("date", d.date);
        v.put("type", d.type);
        v.put("recurring", d.recurring ? 1 : 0);
        v.put("reminder_enabled", d.reminderEnabled ? 1 : 0);
        if (d.id > 0) {
            db().update("important_dates", v, "id = ?", new String[]{String.valueOf(d.id)});
            return d.id;
        }
        v.put("created_at", System.currentTimeMillis());
        d.id = db().insert("important_dates", null, v);
        return d.id;
    }

    public void deleteDate(long id) {
        db().delete("important_dates", "id = ?", new String[]{String.valueOf(id)});
    }
}
