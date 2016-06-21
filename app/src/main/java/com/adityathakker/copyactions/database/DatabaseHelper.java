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

    public DatabaseHelper(Context context) {
        super(context, AppConst.DB.DB_NAME, null, AppConst.DB.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppConst.DB.CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AppConst.DB.DROP_TABLE_HISTORY);
        onCreate(db);
    }

    public Boolean insertNewCopyRecord(String copiedString) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringDate = sdf.format(date);
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConst.DB.HISTORY_STRING, copiedString);
        contentValues.put(AppConst.DB.HISTORY_TIMESTAMP, stringDate);
        contentValues.put(AppConst.DB.HISTORY_IS_FAV, 0);
        long id = sqLiteDatabase.insert(AppConst.DB.HISTORY_TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        if (id == -1) {
            return false;
        }
        return true;
    }

    public Boolean removedCopyRecord(long id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long row = sqLiteDatabase.delete(AppConst.DB.HISTORY_TABLE_NAME, "id=?", new String[]{Long.toString(id)});
        sqLiteDatabase.close();
        if (row == -1) {
            return false;
        }
        return true;
    }

    public Boolean markAsFavorite(CopyRecord copyRecord) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConst.DB.HISTORY_IS_FAV, 1);
        long id = sqLiteDatabase.update(AppConst.DB.HISTORY_TABLE_NAME, contentValues, "id=?", new String[]{Long.toString(copyRecord.getId())});
        sqLiteDatabase.close();
        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean removeFromFavorite(CopyRecord copyRecord) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppConst.DB.HISTORY_IS_FAV, 0);
        long id = sqLiteDatabase.update(AppConst.DB.HISTORY_TABLE_NAME, contentValues, "id=?", new String[]{Long.toString(copyRecord.getId())});
        sqLiteDatabase.close();
        if (id == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<CopyRecord> getAllCopyRecords() {
        List<CopyRecord> copyRecordList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor resultSet = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, null, null, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
        if (resultSet.moveToFirst()) {
            do {
                try {
                    long id = resultSet.getInt(0);
                    String string = resultSet.getString(1);
                    Date timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString(3));
                    Boolean isFav = resultSet.getInt(2) == 1;
                    CopyRecord tempCopyRecord = new CopyRecord(id, string, timeStamp, isFav);
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
        sqLiteDatabase.close();
        return copyRecordList;
    }

    public Boolean isMarkedFav(long id) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor results = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, new String[]{AppConst.DB.HISTORY_IS_FAV}, "id=?", new String[]{Long.toString(id)}, null, null, null);
        int isFav = 0;
        if (results.moveToFirst()) {
            isFav = Integer.parseInt(results.getString(2));
        }
        sqLiteDatabase.close();
        if (isFav == 0) {
            return false;
        } else {
            return true;
        }
    }

    public CopyRecord getLatestCopyRecord() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor result = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, null, null, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC", "1");
        if (result.moveToFirst()) {
            try {
                CopyRecord copyRecord = new CopyRecord(result.getInt(0), result.getString(1), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result.getString(3)), Boolean.parseBoolean(result.getString(2)));
                sqLiteDatabase.close();
                return copyRecord;
            } catch (ParseException e) {
                e.printStackTrace();
                sqLiteDatabase.close();
                return null;
            }
        } else {
            sqLiteDatabase.close();
            return null;
        }
    }

    public List<CopyRecord> getAllFavorites() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<CopyRecord> tempCopyRecordList = new ArrayList<>();
        Cursor resultSet = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, AppConst.DB.HISTORY_IS_FAV + "=?", new String[]{"1"}, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
        try {
            if (resultSet.moveToFirst()) {
                do {
                    CopyRecord copyRecord = new CopyRecord(resultSet.getInt(0), resultSet.getString(1), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString(3)), Boolean.parseBoolean(resultSet.getString(2)));
                    tempCopyRecordList.add(copyRecord);
                } while (resultSet.moveToNext());
            } else {
                return null;
            }
            return tempCopyRecordList;
        } catch (ParseException e) {
            e.printStackTrace();
            sqLiteDatabase.close();
            return null;
        }
    }
}
