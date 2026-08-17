package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.core.AppConfig;
import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.PeriodCycle;
import com.synthlabs.twohearts.data.model.PeriodSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Period tracker storage. This data never leaves the device and is never
 * uploaded anywhere — see {@link PeriodSettings#localOnly}.
 */
public class PeriodRepository {

    public static final String FLOW_LIGHT = "light";
    public static final String FLOW_MEDIUM = "medium";
    public static final String FLOW_HEAVY = "heavy";

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    /* ---------------- settings ---------------- */

    public PeriodSettings getSettings() {
        PeriodSettings s = new PeriodSettings();
        s.cycleLength = AppConfig.DEFAULT_CYCLE_LENGTH_DAYS;
        s.periodLength = AppConfig.DEFAULT_PERIOD_LENGTH_DAYS;
        s.hideDetails = true;
        s.localOnly = true;
        s.reminderExpected = true;
        Cursor c = db().query("period_settings", null, "id = 1", null, null, null, null);
        try {
            if (c.moveToFirst()) {
                s.cycleLength = c.getInt(c.getColumnIndexOrThrow("cycle_length"));
                s.periodLength = c.getInt(c.getColumnIndexOrThrow("period_length"));
                s.showFertile = c.getInt(c.getColumnIndexOrThrow("show_fertile")) == 1;
                s.partnerVisible = c.getInt(c.getColumnIndexOrThrow("partner_visible")) == 1;
                s.hideDetails = c.getInt(c.getColumnIndexOrThrow("hide_details")) == 1;
                s.localOnly = c.getInt(c.getColumnIndexOrThrow("local_only")) == 1;
                s.reminderExpected = c.getInt(c.getColumnIndexOrThrow("reminder_expected")) == 1;
                s.reminderTracking = c.getInt(c.getColumnIndexOrThrow("reminder_tracking")) == 1;
                s.setupDone = c.getInt(c.getColumnIndexOrThrow("setup_done")) == 1;
            }
        } finally {
            c.close();
        }
        return s;
    }

    public void saveSettings(PeriodSettings s) {
        ContentValues v = new ContentValues();
        v.put("id", 1);
        v.put("cycle_length", s.cycleLength);
        v.put("period_length", s.periodLength);
        v.put("show_fertile", s.showFertile ? 1 : 0);
        v.put("partner_visible", s.partnerVisible ? 1 : 0);
        v.put("hide_details", s.hideDetails ? 1 : 0);
        v.put("local_only", s.localOnly ? 1 : 0);
        v.put("reminder_expected", s.reminderExpected ? 1 : 0);
        v.put("reminder_tracking", s.reminderTracking ? 1 : 0);
        v.put("setup_done", s.setupDone ? 1 : 0);
        v.put("updated_at", System.currentTimeMillis());
        db().insertWithOnConflict("period_settings", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /* ---------------- cycles ---------------- */

    public List<PeriodCycle> listCycles() {
        Cursor c = db().query("period_cycles", null, null, null, null, null, "start_date DESC");
        List<PeriodCycle> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                PeriodCycle p = new PeriodCycle();
                p.id = c.getLong(c.getColumnIndexOrThrow("id"));
                p.startDate = c.getLong(c.getColumnIndexOrThrow("start_date"));
                p.endDate = c.getLong(c.getColumnIndexOrThrow("end_date"));
                p.flow = c.getString(c.getColumnIndexOrThrow("flow"));
                p.note = c.getString(c.getColumnIndexOrThrow("note"));
                out.add(p);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public PeriodCycle lastCycle() {
        List<PeriodCycle> all = listCycles();
        return all.isEmpty() ? null : all.get(0);
    }

    public long saveCycle(PeriodCycle p) {
        ContentValues v = new ContentValues();
        v.put("start_date", p.startDate);
        v.put("end_date", p.endDate);
        v.put("flow", p.flow);
        v.put("note", p.note);
        if (p.id > 0) {
            db().update("period_cycles", v, "id = ?", new String[]{String.valueOf(p.id)});
            return p.id;
        }
        v.put("created_at", System.currentTimeMillis());
        p.id = db().insert("period_cycles", null, v);
        return p.id;
    }

    public void deleteCycle(long id) {
        db().delete("period_cycles", "id = ?", new String[]{String.valueOf(id)});
    }

    /** Wipes all tracker data (used by "Delete period data" in settings). */
    public void deleteAllData() {
        db().delete("period_cycles", null, null);
        PeriodSettings fresh = new PeriodSettings();
        fresh.cycleLength = AppConfig.DEFAULT_CYCLE_LENGTH_DAYS;
        fresh.periodLength = AppConfig.DEFAULT_PERIOD_LENGTH_DAYS;
        fresh.hideDetails = true;
        fresh.localOnly = true;
        fresh.reminderExpected = true;
        saveSettings(fresh);
    }
}
