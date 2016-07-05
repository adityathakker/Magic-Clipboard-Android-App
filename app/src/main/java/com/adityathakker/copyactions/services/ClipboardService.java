package com.adityathakker.copyactions.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.ui.activities.WordPopupActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

public class ClipboardService extends Service {
    private static final String TAG = ClipboardService.class.getSimpleName();
    private ClipboardManager clipboardManager;
    private Context context;
    private String previousClip;
    private Date previousClipsTime;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        previousClipsTime = new Date();
        previousClip = "";
        Log.d(TAG, "onStartCommand: Clipboard Service Started At Time => " + new Timestamp(previousClipsTime.getTime()));
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                String newClip = clipData.getItemAt(0).coerceToText(context).toString().trim();
                Date currentTime = new Date();
                long differenceInSecs = (currentTime.getTime() - previousClipsTime.getTime()) / 1000;
                Log.d(TAG, "onPrimaryClipChanged: New Clip Added On Time => " + new Timestamp(currentTime.getTime()) + " and has a difference of " + differenceInSecs + " secs");
                if (newClip.equals(previousClip) && differenceInSecs <= 2) {
                    return;
                } else {
                    if (!newClip.isEmpty()) {
                        Log.d(TAG, "onPrimaryClipChanged: New Clip => \"" + newClip + "\"");

                        databaseHelper = new DatabaseHelper(context);
                        databaseHelper.insertNewCopyRecord(newClip);
                        Log.d(TAG, "onPrimaryClipChanged: Copy Record Inserted");

                        sendBroadcast(new Intent(AppConst.RequestCodes.NEW_RECORD_ADDED));
                        Log.d(TAG, "onPrimaryClipChanged: Sending Broadcast For Record Inserted Event");

                        String loweredNewClip = newClip.toLowerCase();
                        try {
                            URL url = new URL(loweredNewClip);
                            //It is a valid URL

                        } catch (MalformedURLException e) {
                            if (Pattern.matches(AppConst.RegEx.WORD_REGEX, loweredNewClip)) {
                                //It is a Word
                                Log.d(TAG, "onPrimaryClipChanged: Showing Dialog For Word");
                                Intent intent = new Intent(context, WordPopupActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                intent.putExtra("copiedString", newClip);
                                context.startActivity(intent);

                            } else {
                                //It is a Sentence
                            }
                        }



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
