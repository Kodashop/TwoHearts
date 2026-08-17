package com.synthlabs.twohearts.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.synthlabs.twohearts.core.AppConfig;

/**
 * LOCAL SQLITE DATABASE — the single source of truth for TwoHearts data.
 *
 * MIGRATION STRATEGY
 * ------------------
 * 1. Bump {@link #VERSION} by one.
 * 2. Add a new {@code case} block in {@link #onUpgrade} that upgrades FROM the
 *    previous version (ALTER TABLE / CREATE TABLE / data backfill).
 * 3. Never delete or recreate the database to "fix" a schema change: the owner's
 *    memories, notes and cycle history must survive every update.
 *
 * Only repositories in {@code data.repo} talk to this class. UI code never
 * touches SQL directly.
 */
public class TwoHeartsDatabase extends SQLiteOpenHelper {

    public static final String NAME = "twohearts.db";
    public static final int VERSION = 1;

    private static TwoHeartsDatabase instance;

    private TwoHeartsDatabase(Context context) {
        super(context.getApplicationContext(), NAME, null, VERSION);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new TwoHeartsDatabase(context);
            instance.getWritableDatabase();
        }
    }

    public static synchronized TwoHeartsDatabase get() {
        if (instance == null) {
            throw new IllegalStateException("TwoHeartsDatabase.init() must run in Application.onCreate");
        }
        return instance;
    }

    public SQLiteDatabase db() {
        return getWritableDatabase();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createV1(db);
        seedV1(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Each case upgrades from that version to the next one, then falls through.
        switch (oldVersion) {
            // case 1: db.execSQL("ALTER TABLE ..."); // -> version 2
            default:
                break;
        }
    }

    private void createV1(SQLiteDatabase db) {
        /* ---- People and relationship ---- */
        db.execSQL("CREATE TABLE profile ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "is_partner INTEGER NOT NULL DEFAULT 0,"
                + "name TEXT NOT NULL,"
                + "nickname TEXT,"
                + "birthday INTEGER,"
                + "photo_uri TEXT,"
                + "created_at INTEGER NOT NULL,"
                + "updated_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE relationship ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "space_name TEXT,"
                + "greeting TEXT,"
                + "start_date INTEGER,"
                + "status TEXT,"
                + "updated_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE important_dates ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "date INTEGER NOT NULL,"
                + "type TEXT,"
                + "recurring INTEGER NOT NULL DEFAULT 1,"
                + "reminder_enabled INTEGER NOT NULL DEFAULT 1,"
                + "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_dates_date ON important_dates(date)");

        /* ---- Content ---- */
        db.execSQL("CREATE TABLE memories ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "story TEXT,"
                + "date INTEGER NOT NULL,"
                + "location TEXT,"
                + "photo_uri TEXT,"
                + "favorite INTEGER NOT NULL DEFAULT 0,"
                + "created_at INTEGER NOT NULL,"
                + "updated_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_memories_date ON memories(date DESC)");

        db.execSQL("CREATE TABLE notes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "body TEXT NOT NULL,"
                + "pinned INTEGER NOT NULL DEFAULT 0,"
                + "created_at INTEGER NOT NULL,"
                + "updated_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_notes_updated ON notes(updated_at DESC)");

        db.execSQL("CREATE TABLE timeline_events ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "note TEXT,"
                + "date INTEGER NOT NULL,"
                + "icon TEXT,"
                + "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_timeline_date ON timeline_events(date DESC)");

        db.execSQL("CREATE TABLE places ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "note TEXT,"
                + "address TEXT,"
                + "status TEXT NOT NULL DEFAULT 'visited'," // visited | wishlist
                + "photo_uri TEXT,"
                + "visited_date INTEGER,"
                + "created_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE mood_entries ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "mood_key TEXT NOT NULL,"
                + "note TEXT,"
                + "owner TEXT NOT NULL DEFAULT 'me',"
                + "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_mood_created ON mood_entries(created_at DESC)");

        /* ---- Reminders / notifications ---- */
        db.execSQL("CREATE TABLE reminders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "note TEXT,"
                + "trigger_at INTEGER NOT NULL,"
                + "repeat_type TEXT NOT NULL DEFAULT 'none'," // none|daily|weekly|monthly|yearly
                + "enabled INTEGER NOT NULL DEFAULT 1,"
                + "channel TEXT NOT NULL DEFAULT 'reminders',"
                + "source_type TEXT," // manual|important_date|period|timeline
                + "source_id INTEGER,"
                + "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_reminders_trigger ON reminders(trigger_at)");

        db.execSQL("CREATE TABLE notification_log ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "body TEXT,"
                + "channel TEXT,"
                + "reminder_id INTEGER,"
                + "created_at INTEGER NOT NULL,"
                + "read INTEGER NOT NULL DEFAULT 0)");

        /* ---- Period tracker (local only) ---- */
        db.execSQL("CREATE TABLE period_cycles ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "start_date INTEGER NOT NULL,"
                + "end_date INTEGER,"
                + "flow TEXT,"          // light|medium|heavy
                + "note TEXT,"
                + "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_cycles_start ON period_cycles(start_date DESC)");

        db.execSQL("CREATE TABLE period_settings ("
                + "id INTEGER PRIMARY KEY,"
                + "cycle_length INTEGER NOT NULL,"
                + "period_length INTEGER NOT NULL,"
                + "show_fertile INTEGER NOT NULL DEFAULT 0,"
                + "partner_visible INTEGER NOT NULL DEFAULT 0,"
                + "hide_details INTEGER NOT NULL DEFAULT 1,"
                + "local_only INTEGER NOT NULL DEFAULT 1,"
                + "reminder_expected INTEGER NOT NULL DEFAULT 1,"
                + "reminder_tracking INTEGER NOT NULL DEFAULT 0,"
                + "setup_done INTEGER NOT NULL DEFAULT 0,"
                + "updated_at INTEGER NOT NULL)");

        /* ---- Vault ---- */
        db.execSQL("CREATE TABLE vault_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "body_cipher TEXT," // encrypted, never plaintext
                + "type TEXT NOT NULL DEFAULT 'note'," // note|photo
                + "media_uri TEXT,"
                + "created_at INTEGER NOT NULL,"
                + "updated_at INTEGER NOT NULL)");

        /* ---- Games ---- */
        db.execSQL("CREATE TABLE game_stats ("
                + "game_id TEXT PRIMARY KEY,"
                + "plays INTEGER NOT NULL DEFAULT 0,"
                + "best_score INTEGER NOT NULL DEFAULT 0,"
                + "last_score INTEGER NOT NULL DEFAULT 0,"
                + "total_score INTEGER NOT NULL DEFAULT 0,"
                + "last_played INTEGER)");

        db.execSQL("CREATE TABLE game_sessions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "game_id TEXT NOT NULL,"
                + "score INTEGER NOT NULL,"
                + "max_score INTEGER NOT NULL,"
                + "detail TEXT,"
                + "played_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_sessions_game ON game_sessions(game_id, played_at DESC)");

        db.execSQL("CREATE TABLE achievements ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "key TEXT NOT NULL UNIQUE,"
                + "title TEXT NOT NULL,"
                + "unlocked_at INTEGER)");
    }

    /** Rows that must exist for the app to have somewhere to write. */
    private void seedV1(SQLiteDatabase db) {
        long now = System.currentTimeMillis();

        ContentValues relationship = new ContentValues();
        relationship.put("id", 1);
        relationship.put("space_name", "Our little space");
        relationship.put("updated_at", now);
        db.insert("relationship", null, relationship);

        ContentValues period = new ContentValues();
        period.put("id", 1);
        period.put("cycle_length", AppConfig.DEFAULT_CYCLE_LENGTH_DAYS);
        period.put("period_length", AppConfig.DEFAULT_PERIOD_LENGTH_DAYS);
        period.put("show_fertile", AppConfig.DEFAULT_SHOW_FERTILE_WINDOW ? 1 : 0);
        period.put("partner_visible", AppConfig.DEFAULT_PARTNER_VISIBILITY ? 1 : 0);
        period.put("updated_at", now);
        db.insert("period_settings", null, period);
    }
}
