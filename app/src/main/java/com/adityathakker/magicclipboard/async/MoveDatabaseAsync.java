package com.adityathakker.magicclipboard.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.interfaces.IMoveDatabaseAsync;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by adityajthakker on 21/6/16.
 */
public class MoveDatabaseAsync extends AsyncTask<Void, Void, Void> {
    private static final String TAG = MoveDatabaseAsync.class.getSimpleName();
    private Context context;
    private IMoveDatabaseAsync copyAsyncCallback;

    public MoveDatabaseAsync(Context context, IMoveDatabaseAsync copyAsyncCallback) {
        this.context = context;
        this.copyAsyncCallback = copyAsyncCallback;
    }


    @Override
    protected Void doInBackground(Void... params) {
        String dbPath = Constants.DB.DB_PATH + Constants.DB.DB_NAME_MEANINGS;

        try {
            SQLiteDatabase checkDB = context.openOrCreateDatabase(Constants.DB.DB_NAME_MEANINGS, Context.MODE_PRIVATE, null);
            if (checkDB != null) {
                checkDB.close();
            }

            InputStream inputStream = context.getApplicationContext().getAssets().open(Constants.DB.DB_NAME_MEANINGS);
            OutputStream fileOutputStream = new FileOutputStream(dbPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            fileOutputStream.close();
            Log.d(TAG, "doInBackground: Dictionary File Has Been Copied");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground: Error Occurred While Copying", e);
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        copyAsyncCallback.onCopyComplete();
    }
}
