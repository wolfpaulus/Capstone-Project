package com.techcasita.android.hwy67.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.techcasita.android.hwy67.R;

import java.util.HashSet;
import java.util.Set;


public class RegistrationIntentService extends IntentService {
    private static final String TAG = RegistrationIntentService.class.getName();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // Initially this call goes out to the network to retrieve the token, subsequent calls are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "R.string.gcm_defaultSenderId: " + getString(R.string.gcm_defaultSenderId));
                Log.i(TAG, "GCM Registration Token: " + token);
                GcmPubSub.getInstance(this).subscribe(token, "/topics/Demo", null);

                if (!sharedPreferences.getBoolean(getString(R.string.preference_key_insync), true)) {
                    final String[] all = getResources().getStringArray(R.array.feed_values);
                    final Set<String> topics = sharedPreferences.getStringSet(getString(R.string.preference_key_sources), new HashSet<String>());
                    for (final String s : all) {
                        if (topics.contains(s)) {
                            GcmPubSub.getInstance(this).subscribe(token, "/topics/" + s.replace(' ', '_'), null);
                        } else {
                            GcmPubSub.getInstance(this).unsubscribe(token, "/topics/" + s.replace(' ', '_'));
                        }
                    }
                }
                sharedPreferences.edit().putBoolean(getString(R.string.preference_key_insync), true).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(getString(R.string.preference_key_insync), false).apply();
        }
    }
}