package com.techcasita.android.hwy67.content;


import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * The StoryLoader is a Helper for loading a list of stories or a single one.
 */
public class StoryLoader extends CursorLoader {
    private StoryLoader(final Context context, final Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, Contract.Items.DEFAULT_SORT);
    }

    public static StoryLoader newAllStoriesInstance(final Context context) {
        return new StoryLoader(context, Contract.Items.buildDirUri());
    }

    public static StoryLoader newAllStoriesInstance(final Context context, final String category) {
        return new StoryLoader(context, Contract.Items.buildCatUri(category));
    }

    public static StoryLoader newInstanceForItemId(final Context context, final long itemId) {
        return new StoryLoader(context, Contract.Items.buildItemUri(itemId));
    }

    @SuppressWarnings("unused")
    public interface Query {
        String[] PROJECTION = {
                Contract.Items._ID,
                Contract.Items.SID,
                Contract.Items.GUID,
                Contract.Items.DATE,
                Contract.Items.CATEGORIES,

                Contract.Items.AUTHOR,
                Contract.Items.TITLE,
                Contract.Items.CONTENT,

                Contract.Items.URL,
                Contract.Items.FOREIGN_URL,
                Contract.Items.IMAGE_URL
        };

        int _ID = 0;
        int SID = 1;
        int GUID = 2;
        int DATE = 3;
        int CATEGORIES = 4;

        int AUTHOR = 5;
        int TITLE = 6;
        int CONTENT = 7;

        int URL = 8;
        int FOREIGN_URL = 9;
        int IMAGE_URL = 10;
    }
}