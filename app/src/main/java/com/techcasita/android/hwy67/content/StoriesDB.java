package com.techcasita.android.hwy67.content;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.techcasita.android.hwy67.remote.Story;

import java.util.ArrayList;
import java.util.List;

import static com.techcasita.android.hwy67.content.StoriesProvider.Tables;

/**
 * StoriesDB, SQL for creating/upda. the table definition.
 */
public class StoriesDB extends SQLiteOpenHelper {
    private static final String LOG_TAG = StoriesDB.class.getName();
    private static final String DATABASE_NAME = "hwy67.db";
    private static final int DATABASE_VERSION = 4;

    public StoriesDB(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static List<Story> getAllStories(final Context context) {
        final List<Story> storyList = new ArrayList<>();
        final SQLiteDatabase database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        if (database == null) {
            Log.d(LOG_TAG, "getAllStories() database is null");
            return storyList;
        }
        final Cursor cursor = database.query(Tables.ITEMS, null, null, null, null, null, "sid DESC", "5");
        if (cursor == null) {
            Log.d(LOG_TAG, "getAllStories() queryResultcursor is null");
            return storyList;
        }
        if (cursor.moveToFirst()) {
            do {
                final String title = cursor.getString(StoryLoader.Query.TITLE);
                final String date = cursor.getString(StoryLoader.Query.DATE);
                final String author = cursor.getString(StoryLoader.Query.AUTHOR);
                final String img_url = cursor.getString(StoryLoader.Query.IMAGE_URL);
                storyList.add(new Story(title, date, author, img_url));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storyList;
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