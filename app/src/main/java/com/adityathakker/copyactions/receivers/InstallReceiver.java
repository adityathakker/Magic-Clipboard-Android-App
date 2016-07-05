package com.adityathakker.copyactions.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.LocalApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityajthakker on 4/7/16.
 */
public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = InstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();

        List<LocalApp> localAppList = getLocalAppsList(context, packageName);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.storeAllLocalAppsForSent(localAppList);
        databaseHelper.close();
        Log.d(TAG, "onReceive: Installing Package Done");
    }

    private List<LocalApp> getLocalAppsList(Context context, String packageName) {
        List<LocalApp> localAppsList = new ArrayList<>();

        // Intents with SEND action
        PackageManager packageManager = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.setPackage(packageName);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sendIntent, 0);

        LocalApp localApps = null;
        for (int j = 0; j < resolveInfoList.size(); j++) {
            ResolveInfo resolveInfo = resolveInfoList.get(j);
            String packageNameTemp = resolveInfo.activityInfo.packageName;
            String localName = resolveInfo.loadLabel(packageManager).toString();
            String activityName = resolveInfo.activityInfo.name;
            Drawable icon = resolveInfo.activityInfo.loadIcon(packageManager);
            localApps = new LocalApp(packageNameTemp, localName, activityName, icon, true);
            localAppsList.add(localApps);
            Log.d(TAG, "In Install Receiver Loop: Activity Name => " + resolveInfo.activityInfo.toString());
        }

        return localAppsList;
    }
}
