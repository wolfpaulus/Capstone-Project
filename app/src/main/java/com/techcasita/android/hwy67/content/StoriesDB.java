package com.techcasita.android.hwy67.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.techcasita.android.hwy67.content.StoriesProvider.Tables;

/**
 * StoriesDB, SQL for creating/upda. the table definition.
 */
public class StoriesDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hwy67.db";
    private static final int DATABASE_VERSION = 4;

    public StoriesDB(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ITEMS + " ("
                + Contract.Items._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Items.SID + " INTEGER,"
                + Contract.Items.GUID + " TEXT,"
                + Contract.Items.DATE + " TEXT,"
                + Contract.Items.CATEGORIES + " TEXT NOT NULL,"

                + Contract.Items.AUTHOR + " TEXT NOT NULL,"
                + Contract.Items.TITLE + " TEXT NOT NULL,"
                + Contract.Items.CONTENT + " TEXT NOT NULL,"

                + Contract.Items.URL + " TEXT,"
                + Contract.Items.FOREIGN_URL + " TEXT,"
                + Contract.Items.IMAGE_URL + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ITEMS);
        onCreate(db);
    }
}