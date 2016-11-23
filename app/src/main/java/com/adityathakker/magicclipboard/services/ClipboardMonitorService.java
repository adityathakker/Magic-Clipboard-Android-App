package com.adityathakker.magicclipboard.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.ui.activities.SentencePopupActivity;
import com.adityathakker.magicclipboard.ui.activities.WordPopupActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

public class ClipboardMonitorService extends Service implements ClipboardManager.OnPrimaryClipChangedListener{
    private static final String TAG = ClipboardMonitorService.class.getSimpleName();
    private ClipboardManager clipboardManager;
    private Context context;
    private String previousClip;
    private Date previousClipsTime;
    private SharedPreferences sharedPreferences;

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
        clipboardManager.removePrimaryClipChangedListener(this);
        clipboardManager.addPrimaryClipChangedListener(this);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Magic Clipboard")
                        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .setContentText("Ready to help you....");
        startForeground(1, builder.build());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clipboardManager.removePrimaryClipChangedListener(this);
        Log.d(TAG, "onDestroy: Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

                String loweredNewClip = newClip.toLowerCase();
                try {
                    URL urlObj = new URL(loweredNewClip);
                    Log.d(TAG, "onPrimaryClipChanged: It is a URL");
                            /*String url = "http://www.google.com";
                            PackageManager packageManager = context.getPackageManager();
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setData(Uri.parse(url));
                            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sendIntent, 0);
                            InstalledApp localApps = null;
                            ActivityInfo activityInfo = null;
                            for (int j = 0; j < resolveInfoList.size(); j++) {
                                ResolveInfo resolveInfo = resolveInfoList.get(j);
                                String packageName = resolveInfo.activityInfo.packageName;
                                String activityName = resolveInfo.activityInfo.name;
                                try {
                                    activityInfo = packageManager.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
                                    localApps = new InstalledApp(packageName, activityInfo.loadLabel(packageManager).toString(), activityName, activityInfo.loadIcon(packageManager), true);
                                    Log.d(TAG, "onPrimaryClipChanged: Apps" + localApps.toString());
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }*/

                } catch (MalformedURLException e) {
                    if (Pattern.matches(Constants.RegEx.WORD_REGEX, loweredNewClip)) {
                        Log.d(TAG, "onPrimaryClipChanged: It is a word");
                        if(sharedPreferences.getBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_ENABLED,true)){
                            Intent intent = new Intent(context, WordPopupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.putExtra("copiedString", newClip);
                            context.startActivity(intent);
                        }else{
                            Log.d(TAG, "onPrimaryClipChanged: Popup Box is Disabled");
                        }
                    } else {
                        Log.d(TAG, "onPrimaryClipChanged: It is a sentence");
                        if(sharedPreferences.getBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_ENABLED,true)){
                            Intent intent = new Intent(context, SentencePopupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            intent.putExtra("copiedString", newClip);
                            context.startActivity(intent);
                        }else{
                            Log.d(TAG, "onPrimaryClipChanged: Popup Box is Disabled");
                        }
                    }
                }



                //This should always be here
                previousClip = newClip;
                previousClipsTime = currentTime;
            }
        }
    }
}
