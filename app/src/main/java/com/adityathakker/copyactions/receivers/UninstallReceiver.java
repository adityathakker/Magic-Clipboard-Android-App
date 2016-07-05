package com.adityathakker.copyactions.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adityathakker.copyactions.database.DatabaseHelper;

/**
 * Created by adityajthakker on 4/7/16.
 */
public class UninstallReceiver extends BroadcastReceiver {
    private static final String TAG = UninstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.removeLocalAppWithPackage(packageName);
        databaseHelper.close();
        Log.d(TAG, "onReceive: Uninstalling App Done : " + packageName);
    }
}
