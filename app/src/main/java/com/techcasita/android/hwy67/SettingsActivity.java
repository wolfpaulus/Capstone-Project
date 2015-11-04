package com.techcasita.android.hwy67;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.techcasita.android.hwy67.gcm.RegistrationIntentService;
import com.techcasita.android.hwy67.remote.ContentReader;

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SearchActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_settings));
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference button = findPreference(getString(R.string.preference_key_demo));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ContentReader.pushDemo(
                            getString(R.string.content_url),
                            Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
                    return true;
                }
            });
            if (MainActivity.hasPlayServices) {
                getActivity().startService(new Intent(getActivity(), RegistrationIntentService.class));
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Unregister a this instance as a {@link SharedPreferences.OnSharedPreferenceChangeListener}
         *
         * @see #onResume for registering
         */
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        //
        // Implement SharedPreferences.OnSharedPreferenceChangeListener
        //
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
            if (key.equals(getString(R.string.preference_key_sources))) {
                Log.d(LOG_TAG, "Subscriptions changed");
                sharedPreferences.edit().putBoolean(getString(R.string.preference_key_insync), false).apply();
                getActivity().startService(new Intent(getActivity(), RegistrationIntentService.class));
            }
        }
    }
}


