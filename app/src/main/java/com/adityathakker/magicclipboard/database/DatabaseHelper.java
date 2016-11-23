package com.adityathakker.magicclipboard.database;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adityathakker.magicclipboard.models.ClipboardLog;
import com.adityathakker.magicclipboard.models.InstalledApp;
import com.adityathakker.magicclipboard.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseHelper(Context context) {
        super(context, Constants.DB.DB_NAME, null, Constants.DB.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate was Called: " + db.getPath());
        db.execSQL(Constants.DB.CREATE_ACTION_LINKS_TABLE);
        db.execSQL(Constants.DB.CREATE_ACTIONS_SENT_TABLE);
        db.execSQL(Constants.DB.CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade was Called");

    }

    private void openDatabase() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            return;
        } else {
            sqLiteDatabase = getWritableDatabase();
        }
    }

    private void closeDatabase() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    public Boolean addClipboardLog(String clip) {
        openDatabase();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringDate = simpleDateFormat.format(date);

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB.HISTORY_STRING, clip);
        contentValues.put(Constants.DB.HISTORY_TIMESTAMP, stringDate);
        contentValues.put(Constants.DB.HISTORY_IS_FAV, 0);

        long id = sqLiteDatabase.insert(Constants.DB.HISTORY_TABLE_NAME, null, contentValues);
        closeDatabase();
        if (id == -1) {
            return false;
        }
        return true;
    }

    public Boolean removedClipboardLog(long id) {
        openDatabase();
        long row = sqLiteDatabase.delete(Constants.DB.HISTORY_TABLE_NAME, "id=?", new String[]{Long.toString(id)});
        close();
        if (row == -1) {
            return false;
        }
        return true;
    }

    public Boolean addToFavorite(ClipboardLog clipboardLog) {
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB.HISTORY_IS_FAV, 1);

        long id = sqLiteDatabase.update(Constants.DB.HISTORY_TABLE_NAME, contentValues, "id=?", new String[]{Long.toString(clipboardLog.getId())});
        closeDatabase();
        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean removeFromFavorite(ClipboardLog clipboardLog) {
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB.HISTORY_IS_FAV, 0);
        long id = sqLiteDatabase.update(Constants.DB.HISTORY_TABLE_NAME, contentValues, "id=?", new String[]{Long.toString(clipboardLog.getId())});
        closeDatabase();
        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<ClipboardLog> getClipboardLogs(String where, String whereArgs[], String orderBy) {
        List<ClipboardLog> clipboardLogList = new ArrayList<>();
        openDatabase();
        ClipboardLog tempClipboardLog = null;
        Cursor resultSet = sqLiteDatabase.query(Constants.DB.HISTORY_TABLE_NAME, null, where, whereArgs, null, null, orderBy);
        if (resultSet.moveToFirst()) {
            do {
                try {
                    long id = resultSet.getInt(0);
                    String clip = resultSet.getString(1);
                    Date timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString(3));
                    Boolean isFav = resultSet.getInt(2) == 1;
                    tempClipboardLog = new ClipboardLog(id, clip, timeStamp, isFav);
                    clipboardLogList.add(tempClipboardLog);
                    Log.d(TAG, "getClipboardLogs: Adding Record => " + tempClipboardLog.getClip());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (resultSet.moveToNext());
        }
        if (resultSet != null && !resultSet.isClosed()) {
            resultSet.close();
        }
        closeDatabase();
        return clipboardLogList;
    }

    public int getClipboardLogsSize() {
        openDatabase();
        Cursor resultSet = sqLiteDatabase.query(Constants.DB.HISTORY_TABLE_NAME, null, null, null, null, null, null);
        int total = resultSet.getCount();
        closeDatabase();
        return total;
    }

    public int getFavoritesSize() {
        openDatabase();
        Cursor resultSet = sqLiteDatabase.query(Constants.DB.HISTORY_TABLE_NAME, null, Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"1"}, null, null, null);
        int total = resultSet.getCount();
        closeDatabase();
        return total;
    }

    public Boolean isAddedToFav(long id) {
        openDatabase();
        Cursor results = sqLiteDatabase.query(Constants.DB.HISTORY_TABLE_NAME, new String[]{Constants.DB.HISTORY_IS_FAV}, "id=?", new String[]{Long.toString(id)}, null, null, null);
        int isFav = 0;
        if (results.moveToFirst()) {
            isFav = Integer.parseInt(results.getString(2));
        }
        closeDatabase();
        if (isFav == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void addAllLocalAppsForSent(List<InstalledApp> installedAppList) {
        //used for both sentence and word apps
        openDatabase();
        ContentValues contentValues = null;
        for (InstalledApp eachInstalledApp : installedAppList) {
            contentValues = new ContentValues();
            contentValues.put(Constants.DB.ACTIONS_SENT_WORD_PACKAGE, eachInstalledApp.getPackageName());
            contentValues.put(Constants.DB.ACTIONS_SENT_WORD_ACTIVITY, eachInstalledApp.getActivityName());
            contentValues.put(Constants.DB.ACTIONS_SENT_WORD_ENABLED, eachInstalledApp.getEnabled() ? 1 : 0);
            sqLiteDatabase.insert(Constants.DB.ACTIONS_SENT_WORD_TABLE_NAME, null, contentValues);
        }
        closeDatabase();
    }

    public void removeLocalApp(String packageName) {
        openDatabase();
        sqLiteDatabase.delete(Constants.DB.ACTIONS_SENT_WORD_TABLE_NAME, Constants.DB.ACTIONS_SENT_WORD_PACKAGE + "=?", new String[]{packageName});
        closeDatabase();
    }

    public List<InstalledApp> getAllLocalAppsForSent() {
        openDatabase();
        Cursor resultSet = sqLiteDatabase.query(Constants.DB.ACTIONS_SENT_WORD_TABLE_NAME, null, null, null, null, null, null);
        if (resultSet.moveToFirst()) {
            PackageManager packageManager = context.getPackageManager();
            List<InstalledApp> installedAppList = new ArrayList<>();
            InstalledApp installedApp = null;
            do {
                String packageName = resultSet.getString(1);
                String activityName = resultSet.getString(2);
                try {
                    ActivityInfo activityInfo = packageManager.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
                    installedApp = new InstalledApp(packageName, activityInfo.loadLabel(packageManager).toString(), resultSet.getString(2), activityInfo.loadIcon(packageManager), resultSet.getString(3).equals("1"));
                    installedAppList.add(installedApp);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d(TAG, "getAllLocalAppsForSent: App Not Installed: " + packageName);
                }
            } while (resultSet.moveToNext());
            closeDatabase();
            return installedAppList;
        } else {
            closeDatabase();
            return null;
        }
    }

    public List<InstalledApp> getEnabledLocalAppsForSent() {
        openDatabase();
        Cursor resultSet = sqLiteDatabase.query(Constants.DB.ACTIONS_SENT_WORD_TABLE_NAME, null, Constants.DB.ACTIONS_SENT_WORD_ENABLED + "=?", new String[]{"1"}, null, null, null);
        if (resultSet.moveToFirst()) {
            PackageManager packageManager = context.getPackageManager();
            List<InstalledApp> installedAppList = new ArrayList<>();
            InstalledApp installedApp = null;
            do {
                String packageName = resultSet.getString(1);
                String activityName = resultSet.getString(2);
                try {
                    ActivityInfo activityInfo = packageManager.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
                    installedApp = new InstalledApp(packageName, activityInfo.loadLabel(packageManager).toString(), resultSet.getString(2), activityInfo.loadIcon(packageManager), resultSet.getString(3).equals("1"));
                    installedAppList.add(installedApp);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d(TAG, "getAllLocalAppsForSent: App Not Installed: " + packageName);
                }
            } while (resultSet.moveToNext());
            closeDatabase();
            return installedAppList;
        } else {
            closeDatabase();
            return null;
        }
    }

    public void updateLocalApp(InstalledApp installedApp) {
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB.ACTIONS_SENT_WORD_ENABLED, installedApp.getEnabled() ? 1 : 0);
        long id = sqLiteDatabase.update(Constants.DB.ACTIONS_SENT_WORD_TABLE_NAME, contentValues, Constants.DB.ACTIONS_SENT_WORD_ACTIVITY + "=?", new String[]{installedApp.getActivityName()});
        if (id == -1) {
            Log.d(TAG, "updateLocalApp: Something Went Wrong");
        } else {
            Log.d(TAG, "updateLocalApp: Done");
        }
        closeDatabase();
    }

    public ClipboardLog getLatestClipboardLog() {
        openDatabase();
        Cursor result = sqLiteDatabase.query(Constants.DB.HISTORY_TABLE_NAME, null, null, null, null, null, Constants.DB.HISTORY_TIMESTAMP + " DESC", "1");
        if (result.moveToFirst()) {
            try {
                ClipboardLog clipboardLog = new ClipboardLog(result.getInt(0), result.getString(1), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result.getString(3)), Boolean.parseBoolean(result.getString(2)));
                closeDatabase();
                return clipboardLog;
            } catch (ParseException e) {
                e.printStackTrace();
                closeDatabase();
                return null;
            }
        } else {
            closeDatabase();
            return null;
        }
    }
}
