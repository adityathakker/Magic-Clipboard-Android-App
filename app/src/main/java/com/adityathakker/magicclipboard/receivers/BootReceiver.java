package com.adityathakker.magicclipboard.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adityathakker.magicclipboard.services.ClipboardMonitorService;

/**
 * Created by adityajthakker on 12/6/16.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            ComponentName service = context.startService(
                    new Intent(context, ClipboardMonitorService.class));
            if (service == null) {
                Log.e(TAG, "onReceive: Service Can't Be Started");
            } else {
                Log.d(TAG, "onReceive: ClipboardMonitorService Started");
            }
        } else {
            Log.e(TAG, "onReceive: Received unexpected intent " + intent.toString());
        }
    }
}
