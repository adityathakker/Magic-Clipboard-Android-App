package com.adityathakker.copyactions.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.models.CopyRecord;

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
        long id = sqLiteDatabase.insert(AppConst.DB.HISTORY_TABLE_NAME, null, contentValues);
        if (id == -1) {
            return false;
        }
        return true;
    }

    public List<CopyRecord> getAllCopyRecords() {
        List<CopyRecord> copyRecordList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor resultSet = sqLiteDatabase.query(AppConst.DB.HISTORY_TABLE_NAME, null, null, null, null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
        if (resultSet.moveToFirst()) {
            do {
                CopyRecord tempCopyRecord = new CopyRecord(resultSet.getInt(0),
                        resultSet.getString(1),
                        resultSet.getString(2));
                copyRecordList.add(tempCopyRecord);
                Log.d(TAG, "getAllCopyRecords: Adding Record => " + tempCopyRecord.getString());
            } while (resultSet.moveToNext());
        }
        if (resultSet != null && !resultSet.isClosed()) {
            resultSet.close();
        }
        return copyRecordList;
    }
}
