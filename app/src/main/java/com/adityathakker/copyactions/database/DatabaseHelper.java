package com.adityathakker.copyactions.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.models.CopyRecord;

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
        super(context, AppConst.DB.DB_NAME, null, AppConst.DB.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate was Called: " + db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL(AppConst.DB.DROP_TABLE_HISTORY);
        onCreate(db);*/
        Log.d(TAG, "onUpgrade was Called");

    }

    public void openDatabase() {
        String dbPath = context.getDatabasePath(AppConst.DB.DB_NAME).getPath();
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            return;
        } else {
            sqLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    public void closeDatabase() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }



    public Boolean insertNewCopyRecord(String copiedString) {
        openDatabase();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringDate = simpleDateFormat.format(date);

        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConst.DB.HISTORY_STRING, copiedString);
        contentValues.put(AppConst.DB.HISTORY_TIMESTAMP, stringDate);
        contentValues.put(AppConst.DB.HISTORY_IS_FAV, 0);

        long id = sqLiteDatabase.insert(AppConst.DB.HISTORY_TABLE_NAME, null, contentValues);
        closeDatabase();
        if (id == -1) {
            return false;
        }
        return true;
    }

    public Boolean removedCopyRecord(long id) {
        openDatabase();
        long row = sqLiteDatabase.delete(AppConst.DB.HISTORY_TABLE_NAME, "id=?", new String[]{Long.toString(id)});
        close();
        if (row == -1) {
            return false;
        }
        return true;
    }

    public Boolean markAsFavorite(CopyRecord copyRecord) {
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConst.DB.HISTORY_IS_FAV, 1);

        long id = sqLiteDatabase.update(AppConst.DB.HISTORY_TABLE_NAME, contentValues, "id=?", new String[]{Long.toString(copyRecord.getId())});
        closeDatabase();
        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean removeFromFavorite(CopyRecord copyRecord) {
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConst.DB.HISTORY_IS_FAV, 0);
        long id = sqLiteDatabase.update(AppConst.DB.HISTORY_TABLE_NAME, contentValues, "id=?", new String[]{Long.toString(copyRecord.getId())});
        closeDatabase();
        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<CopyRecord> getAllCopyRecords() {
        List<CopyRecord> copyRecordList = new ArrayList<>();
        openDatabase();
        CopyRecord tempCopyRecord = null;
        Cursor resultSet = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, null, null, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
        if (resultSet.moveToFirst()) {
            do {
                try {
                    long id = resultSet.getInt(0);
                    String string = resultSet.getString(1);
                    Date timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString(3));
                    Boolean isFav = resultSet.getInt(2) == 1;
                    tempCopyRecord = new CopyRecord(id, string, timeStamp, isFav);
                    copyRecordList.add(tempCopyRecord);
                    Log.d(TAG, "getAllCopyRecords: Adding Record => " + tempCopyRecord.getString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (resultSet.moveToNext());
        }
        if (resultSet != null && !resultSet.isClosed()) {
            resultSet.close();
        }
        closeDatabase();
        return copyRecordList;
    }

    public Boolean isMarkedFav(long id) {
        openDatabase();
        Cursor results = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, new String[]{AppConst.DB.HISTORY_IS_FAV}, "id=?", new String[]{Long.toString(id)}, null, null, null);
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

    public CopyRecord getLatestCopyRecord() {
        openDatabase();
        Cursor result = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, null, null, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC", "1");
        if (result.moveToFirst()) {
            try {
                CopyRecord copyRecord = new CopyRecord(result.getInt(0), result.getString(1), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result.getString(3)), Boolean.parseBoolean(result.getString(2)));
                closeDatabase();
                return copyRecord;
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

    public List<CopyRecord> getAllFavorites() {
        openDatabase();
        List<CopyRecord> tempCopyRecordList = new ArrayList<>();
        Cursor resultSet = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, AppConst.DB.HISTORY_IS_FAV + "=?", new String[]{"1"}, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
        CopyRecord copyRecord = null;
        try {
            if (resultSet.moveToFirst()) {
                do {
                    copyRecord = new CopyRecord(resultSet.getInt(0), resultSet.getString(1), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString(3)), Boolean.parseBoolean(resultSet.getString(2)));
                    tempCopyRecordList.add(copyRecord);
                } while (resultSet.moveToNext());
            } else {
                return null;
            }
            return tempCopyRecordList;
        } catch (ParseException e) {
            e.printStackTrace();
            closeDatabase();
            return null;
        }
    }
}
