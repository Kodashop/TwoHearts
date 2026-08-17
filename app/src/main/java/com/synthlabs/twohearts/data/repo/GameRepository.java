package com.synthlabs.twohearts.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.synthlabs.twohearts.data.db.TwoHeartsDatabase;
import com.synthlabs.twohearts.data.model.GameSession;
import com.synthlabs.twohearts.data.model.GameStat;

import java.util.ArrayList;
import java.util.List;

/** Per-game statistics and session history. */
public class GameRepository {

    private SQLiteDatabase db() {
        return TwoHeartsDatabase.get().db();
    }

    public GameStat stats(String gameId) {
        GameStat s = new GameStat();
        s.gameId = gameId;
        Cursor c = db().query("game_stats", null, "game_id = ?",
                new String[]{gameId}, null, null, null);
        try {
            if (c.moveToFirst()) {
                s.plays = c.getInt(c.getColumnIndexOrThrow("plays"));
                s.bestScore = c.getInt(c.getColumnIndexOrThrow("best_score"));
                s.lastScore = c.getInt(c.getColumnIndexOrThrow("last_score"));
                s.totalScore = c.getInt(c.getColumnIndexOrThrow("total_score"));
                s.lastPlayed = c.getLong(c.getColumnIndexOrThrow("last_played"));
            }
        } finally {
            c.close();
        }
        return s;
    }

    /** Records a finished round and rolls the aggregate stats forward. */
    public void recordSession(String gameId, int score, int maxScore, String detail) {
        long now = System.currentTimeMillis();
        ContentValues session = new ContentValues();
        session.put("game_id", gameId);
        session.put("score", score);
        session.put("max_score", maxScore);
        session.put("detail", detail);
        session.put("played_at", now);
        db().insert("game_sessions", null, session);

        GameStat current = stats(gameId);
        ContentValues v = new ContentValues();
        v.put("game_id", gameId);
        v.put("plays", current.plays + 1);
        v.put("best_score", Math.max(current.bestScore, score));
        v.put("last_score", score);
        v.put("total_score", current.totalScore + score);
        v.put("last_played", now);
        db().insertWithOnConflict("game_stats", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<GameSession> history(String gameId, int limit) {
        Cursor c = db().query("game_sessions", null, "game_id = ?", new String[]{gameId},
                null, null, "played_at DESC", String.valueOf(limit));
        List<GameSession> out = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                GameSession s = new GameSession();
                s.id = c.getLong(c.getColumnIndexOrThrow("id"));
                s.gameId = c.getString(c.getColumnIndexOrThrow("game_id"));
                s.score = c.getInt(c.getColumnIndexOrThrow("score"));
                s.maxScore = c.getInt(c.getColumnIndexOrThrow("max_score"));
                s.detail = c.getString(c.getColumnIndexOrThrow("detail"));
                s.playedAt = c.getLong(c.getColumnIndexOrThrow("played_at"));
                out.add(s);
            }
        } finally {
            c.close();
        }
        return out;
    }

    public int totalPlays() {
        Cursor c = db().rawQuery("SELECT COALESCE(SUM(plays), 0) FROM game_stats", null);
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    public void resetAll() {
        db().delete("game_sessions", null, null);
        db().delete("game_stats", null, null);
    }
}
