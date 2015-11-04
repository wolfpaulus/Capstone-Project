package com.techcasita.android.hwy67.content;

import android.content.ContentValues;
import android.net.Uri;

import com.techcasita.android.hwy67.remote.Story;

/**
 * The Contract, for putting a story into sql columns
 */
public class Contract {
    public static final String CONTENT_AUTHORITY = "com.techcasita.android.hwy67";
    public static final Uri BASE_URI = Uri.parse("content://com.techcasita.android.hwy67");

    private Contract() {
    }

    public static ContentValues getContentValues(final Story story) {
        final ContentValues values = new ContentValues();
        values.put(Contract.Items.SID, story.getID());
        values.put(Contract.Items.GUID, story.getGuid());
        values.put(Contract.Items.DATE, story.getDate());
        values.put(Contract.Items.CATEGORIES, story.getCategories());

        values.put(Contract.Items.AUTHOR, story.getAuthor());
        values.put(Contract.Items.TITLE, story.getTitle());
        values.put(Contract.Items.CONTENT, story.getContent());

        values.put(Contract.Items.URL, story.getUrl());
        values.put(Contract.Items.FOREIGN_URL, story.getForeign_url());
        values.put(Contract.Items.IMAGE_URL, story.getImage_url());
        return values;
    }

    interface Columns {
        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "_id";
        String SID = "sid";
        String GUID = "guid";
        String DATE = "date";
        String CATEGORIES = "categories";

        String AUTHOR = "author";
        String TITLE = "title";
        String CONTENT = "content";

        String URL = "url";
        String FOREIGN_URL = "foreign_url";
        String IMAGE_URL = "image_url";
    }

    @SuppressWarnings("unused")
    public static class Items implements Columns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.techcasita.android.hwy67.items";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.techcasita.android.hwy67.items";
        public static final String DEFAULT_SORT = SID + " DESC";

        /**
         * Matches: /items/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("items").build();
        }

        /**
         * Matches: /items/[_id]/
         */
        public static Uri buildItemUri(final long _id) {
            return BASE_URI.buildUpon().appendPath("items").appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(final Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }

        /**
         * Matches: /items/[category]/
         */
        public static Uri buildCatUri(final String category) {
            return BASE_URI.buildUpon().appendPath("items").appendPath(category).build();
        }

        /**
         * Matches: /items/search/[searchterm]/
         */
        public static Uri buildSearchUri(final String searchTerm) {
            return BASE_URI.buildUpon().appendPath("items").appendPath("search").appendPath(searchTerm).build();
        }
    }
}
