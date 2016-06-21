package com.adityathakker.copyactions.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.adityathakker.copyactions.ui.activities.PopupActivity;

import java.sql.Timestamp;
import java.util.Date;

public class ClipboardService extends Service {
    private static final String TAG = ClipboardService.class.getSimpleName();
    private ClipboardManager clipboardManager;
    private Context context;
    private String previousClip;
    private Date previousClipsTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        previousClipsTime = new Date();
        previousClip = "";
        Log.d(TAG, "onStartCommand: Clipboard Service Started At Time => " + new Timestamp(previousClipsTime.getTime()));
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                String newClip = clipData.getItemAt(0).coerceToText(context).toString();
                Date currentTime = new Date();
                long differenceInSecs = (currentTime.getTime() - previousClipsTime.getTime()) / 1000;
                Log.d(TAG, "onPrimaryClipChanged: New Clip Added On Time => " + new Timestamp(currentTime.getTime()) + " and has a difference of " + differenceInSecs + " secs");
                if (newClip.equals(previousClip) && differenceInSecs <= 2) {
                    return;
                } else {
                    if (!newClip.isEmpty()) {


                        Intent intent = new Intent(context, PopupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.putExtra("copiedString", newClip);
                        context.startActivity(intent);
                        Log.d(TAG, "onPrimaryClipChanged: New Clip => \"" + newClip + "\"");

                        //This should always be here
                        previousClip = newClip;
                        previousClipsTime = currentTime;
                    }
                }
            }
        });
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
