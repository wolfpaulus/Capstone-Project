package com.techcasita.android.hwy67;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.Log;

import com.techcasita.android.hwy67.content.Contract;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class UriMatcherTest {
    private static final String LOG_TAG = UriMatcherTest.class.getName();
    private static final int ITEMS = 0;
    private static final int ITEMS__ID = 1;
    private static final int ITEMS__CAT = 2;
    private static final int ITEMS__Q = 3;

    @Test
    public void testMaching() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "items", ITEMS);
        matcher.addURI(authority, "items/#", ITEMS__ID);
        matcher.addURI(authority, "items/cat/*", ITEMS__CAT);
        matcher.addURI(authority, "items/search/*", ITEMS__Q);
        int i = -1;
        i = matcher.match(Uri.parse("content://com.techcasita.android.hwy67/items"));
        i = matcher.match(Uri.parse("content://com.techcasita.android.hwy67/items/1000"));
        i = matcher.match(Uri.parse("content://com.techcasita.android.hwy67/cat/Lakeside"));
        i = matcher.match(Uri.parse("content://com.techcasita.android.hwy67/search/beer"));

        assertTrue(ITEMS == matcher.match(Uri.parse("content://com.techcasita.android.hwy67/items/1000")));
        assertTrue(ITEMS__CAT == matcher.match(Uri.parse("content://com.techcasita.android.hwy67/Lakeside")));


    }
}
