package com.adityathakker.magicclipboard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.models.InstalledApp;
import com.adityathakker.magicclipboard.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityajthakker on 4/7/16.
 */
public class AppInstallReceiver extends BroadcastReceiver {
    private static final String TAG = AppInstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        List<InstalledApp> installedAppList = getLocalAppsList(context, packageName);

        //Updating the Actions Count
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.SharedPrefs.TOTAL_AVAILABLE_ACTIONS,sharedPreferences.getInt(Constants.SharedPrefs.TOTAL_AVAILABLE_ACTIONS,0) + installedAppList.size());
        editor.commit();


        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.addAllLocalAppsForSent(installedAppList);
        databaseHelper.close();
        Log.d(TAG, "onReceive: Installing Package Done");
    }

    private List<InstalledApp> getLocalAppsList(Context context, String packageName) {
        List<InstalledApp> installedAppsList = new ArrayList<>();

        // Intents with SEND action
        PackageManager packageManager = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sendIntent, 0);

        InstalledApp installedApps = null;
        for (int j = 0; j < resolveInfoList.size(); j++) {
            ResolveInfo resolveInfo = resolveInfoList.get(j);
            String packageNameTemp = resolveInfo.activityInfo.packageName;
            String localName = resolveInfo.loadLabel(packageManager).toString();
            String activityName = resolveInfo.activityInfo.name;
            Drawable icon = resolveInfo.activityInfo.loadIcon(packageManager);
            installedApps = new InstalledApp(packageNameTemp, localName, activityName, icon, true);
            installedAppsList.add(installedApps);
            Log.d(TAG, "In Install Receiver Loop: Activity Name => " + resolveInfo.activityInfo.toString());
        }

        return installedAppsList;
    }
}
