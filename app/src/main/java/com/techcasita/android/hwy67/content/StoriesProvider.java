package com.techcasita.android.hwy67.content;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * StoriesProvider, the ContentProvider for stories, also the place where URIs are matched.
 */
public class StoriesProvider extends ContentProvider {
    private static final String LOG_TAG = StoriesProvider.class.getName();
    private static final int ITEMS = 0;
    private static final int ITEMS__ID = 1;
    private static final int ITEMS__CAT = 2;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteOpenHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "items", ITEMS);
        matcher.addURI(authority, "items/#", ITEMS__ID);
        matcher.addURI(authority, "items/*", ITEMS__CAT);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new StoriesDB(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull final Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
            case ITEMS__CAT:
            case ITEMS__ID:
                return Contract.Items.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull final Uri uri,
                        final String[] projection,
                        final String selection,
                        final String[] selectionArgs,
                        final String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS: {
                final long _id = db.insertOrThrow(Tables.ITEMS, null, values);
                if (getContext() != null) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return Contract.Items.buildItemUri(_id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull final Uri uri,
                      final ContentValues values,
                      final String selection,
                      final String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return builder.where(selection, selectionArgs).update(db, values);
    }

    @Override
    public int delete(@NonNull final Uri uri, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return builder.where(selection, selectionArgs).delete(db);
    }

    private SelectionBuilder buildSelection(final Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(final Uri uri, final int match, final SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case ITEMS: {
                return builder.table(Tables.ITEMS);
            }
            case ITEMS__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.ITEMS).where(Contract.Items._ID + "=?", _id);
            }
            case ITEMS__CAT: {
                final String cat = paths.get(1);
                String sql;
                if (cat.startsWith("q=")) {
                    final String searchTerm = paths.get(1).substring(2);
                    sql = String.format("%s LIKE '%%%s%%'", Contract.Items.CATEGORIES, searchTerm)
                            + " OR " + String.format("%s LIKE '%%%s%%'", Contract.Items.AUTHOR, searchTerm)
                            + " OR " + String.format("%s LIKE '%%%s%%'", Contract.Items.TITLE, searchTerm)
                            + " OR " + String.format("%s LIKE '%%%s%%'", Contract.Items.CONTENT, searchTerm);
                } else {
                    sql = String.format("%s LIKE '%%%s%%'", Contract.Items.CATEGORIES, cat);
                }
                Log.d(LOG_TAG, "sql = " + sql);
                return builder.table(Tables.ITEMS).where(sql);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull final ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    interface Tables {
        String ITEMS = "items";
    }
}
