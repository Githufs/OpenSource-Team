package com.example.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SCORE = "score";
    private static final String DATABASE_NAME = "quiz_ranking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RANKING = "ranking";
    private static final String COLUMN_ID = "_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_RANKING + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_RANKING;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public void saveRanking(String username, int score) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_SCORE, score);

        db.insert(TABLE_RANKING, null, values);
        db.close();
    }

    public Cursor getRanking() {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {COLUMN_USERNAME, COLUMN_SCORE};
        String orderBy = COLUMN_SCORE + " DESC";
        Cursor cursor = db.query(TABLE_RANKING, columns, null, null, null, null, orderBy);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }
}
