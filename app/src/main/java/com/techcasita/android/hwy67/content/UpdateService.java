package com.techcasita.android.hwy67.content;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.techcasita.android.hwy67.R;
import com.techcasita.android.hwy67.remote.ContentReader;
import com.techcasita.android.hwy67.remote.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * UpdateService, fetching information from a remote server, using the ContentReader (Retrofit).
 * Before and after, a local broadcast is sent, to allow for UI updates, e.g. progress-bars
 */
@SuppressWarnings("deprecation")
public class UpdateService extends IntentService {
    public static final String BROADCAST_ACTION_STATE_CHANGE = "com.techcasita.android.hwy67.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING = "com.techcasita.android.hwy67.intent.extra.REFRESHING";
    private static final String LOG_TAG = UpdateService.class.getName();


    public UpdateService() {
        super(UpdateService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(LOG_TAG, "Device is not online, omit refresh.");
            return;
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
        Log.w(LOG_TAG, "Data requested");

        final ArrayList<ContentProviderOperation> cpo = new ArrayList<>();

        final List<Story> list = ContentReader.fetchArticles(getString(R.string.content_url));
        if (list != null && !list.isEmpty()) {
            // Delete all items
            final Uri dirUri = Contract.Items.buildDirUri();
            cpo.add(ContentProviderOperation.newDelete(dirUri).build());

            for (final Story a : list) {
                final ContentValues values = Contract.getContentValues(a);
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }
        }
        try {
            getContentResolver().applyBatch(Contract.CONTENT_AUTHORITY, cpo);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
            Log.w(LOG_TAG, "Data refreshed");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            e.printStackTrace();
        }
    }
}
