package com.adityathakker.magicclipboard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.models.InstalledApp;

import java.util.List;

/**
 * Created by adityajthakker on 4/7/16.
 */
public class AppUninstallReceiver extends BroadcastReceiver {
    private static final String TAG = AppUninstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.removeLocalApp(packageName);
        List<InstalledApp> installedAppList = databaseHelper.getAllLocalAppsForSent();

        //Updating the Actions Count
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.SharedPrefs.TOTAL_AVAILABLE_ACTIONS, installedAppList.size() + Constants.Others.BUILT_IN_ACTIONS_COUNT);
        editor.commit();

        Log.d(TAG, "onReceive: Uninstalling App Done : " + packageName);
    }
}
