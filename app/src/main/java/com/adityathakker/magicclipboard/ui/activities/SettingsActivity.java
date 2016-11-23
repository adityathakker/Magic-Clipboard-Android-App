package com.adityathakker.magicclipboard.ui.activities;


import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.services.ClipboardMonitorService;
import com.adityathakker.magicclipboard.ui.custom.AppCompatPreferenceActivity;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || SettingsFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        private Context context;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = getActivity().getApplicationContext();
            addPreferencesFromResource(R.xml.prefs);

            SwitchPreference enablePopupFeature = (SwitchPreference) getPreferenceScreen().getPreference(0);
            Log.d(TAG, "onCreate: Is UI Popup Box Feature Enabled => " + enablePopupFeature.isChecked());
            if(isMyServiceRunning(ClipboardMonitorService.class)){
                enablePopupFeature.setChecked(true);
                Log.d(TAG, "onCreate: Enabling Service Feature UI");
            }else{
                enablePopupFeature.setChecked(false);
                Log.d(TAG, "onCreate: Disabling Service Feature UI");
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_popup_box_enable_disable")) {
                SwitchPreference enablePopupFeature = (SwitchPreference)findPreference(key);
                if (enablePopupFeature.isChecked()) {
                    if(!isMyServiceRunning(ClipboardMonitorService.class)){
                        Log.d(TAG, "onSharedPreferenceChanged: Service Started");
                        context.startService(new Intent(context, ClipboardMonitorService.class));
                    }else{
                        Log.d(TAG, "onSharedPreferenceChanged: Already Service Running");
                    }
                } else {
                    if(isMyServiceRunning(ClipboardMonitorService.class)){
                        Log.d(TAG, "onSharedPreferenceChanged: Service Stopped");
                        context.stopService(new Intent(context, ClipboardMonitorService.class));
                    }else{
                        Log.d(TAG, "onSharedPreferenceChanged: Already Service Stopped");
                    }
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

    }

}
