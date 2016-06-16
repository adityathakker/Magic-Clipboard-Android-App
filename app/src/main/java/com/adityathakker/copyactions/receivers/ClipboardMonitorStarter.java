package com.adityathakker.copyactions.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adityathakker.copyactions.services.ClipboardService;

/**
 * Created by adityajthakker on 12/6/16.
 */
public class ClipboardMonitorStarter extends BroadcastReceiver {

    private static final String TAG = ClipboardMonitorStarter.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName service = context.startService(
                    new Intent(context, ClipboardService.class));
            if (service == null) {
                Log.e(TAG, "onReceive: Service Can't Be Started");
            } else {
                Log.d(TAG, "onReceive: ClipboardService Started On Boot");
            }
        } else {
            Log.e(TAG, "onReceive: Received unexpected intent " + intent.toString());
        }
    }
}
