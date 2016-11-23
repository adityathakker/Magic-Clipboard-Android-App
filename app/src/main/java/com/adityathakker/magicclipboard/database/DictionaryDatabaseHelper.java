package com.adityathakker.magicclipboard.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.utils.Constants;

/**
 * Created by Aditya Thakker (Github: @adityathakker) on 5/11/16.
 */

public class DictionaryDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DictionaryDatabaseHelper.class.getSimpleName();
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DictionaryDatabaseHelper(Context context) {
        super(context, Constants.DB.DB_NAME_MEANINGS, null, Constants.DB.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate was Called: " + db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL(Constants.DB.DROP_TABLE_HISTORY);
        onCreate(db);*/
        Log.d(TAG, "onUpgrade was Called");

    }

    private void openDatabase() {
        String dbPath = context.getDatabasePath(Constants.DB.DB_NAME_MEANINGS).getPath();
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            return;
        } else {
            sqLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    private void closeDatabase() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    public String getMeaning(String word){
        StringBuilder meaningStringBuilder = new StringBuilder();
        meaningStringBuilder.setLength(0);
        meaningStringBuilder.append("<html>");
        meaningStringBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
        meaningStringBuilder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        meaningStringBuilder.append("<head>");
        meaningStringBuilder.append("<style> body{margin: 0px;} hr{display:none; border-color: "  + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorAccent))) + ";border-style: solid; border-width: 1px;}");
        meaningStringBuilder.append(".container{margin: 10px;font-family: arial,sans-serif;} ul{margin:7px 0px;} .wd{font-size: xx-large;font-weight: normal;color: #222;}");
        meaningStringBuilder.append(".prn{font-size: large;line-height: normal;font-weight: normal;color: " + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorPrimary))) + ";}");
        meaningStringBuilder.append(".pos{padding-top: 10px;font-size: small;font-style: italic;}.gloss ul li{font-size: small;}");
        meaningStringBuilder.append(".e_t{font-weight: bold;text-indent: 15px;font-size: small;font-style: italic;}.ex{font-size: small;color: " + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorPrimary))) + ";}");
        meaningStringBuilder.append(".ex ul li{list-style: square inside;} .mix{font-style: italic;font-weight: bold;text-indent: 15px;font-size: small;}");
        meaningStringBuilder.append(".other_forms span{color: " + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorPrimary))) + ";font-style: normal;font-weight: normal;}");
        meaningStringBuilder.append(".syn span{color: " + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorPrimary))) + ";font-style: normal;font-weight: normal;} .antonyms span{color: " + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.colorPrimary))) + ";font-style: normal;font-weight: normal;}");
        meaningStringBuilder.append("</style>");
        meaningStringBuilder.append("</head>");
        meaningStringBuilder.append("<body>");
        meaningStringBuilder.append("<div class=\"container\">");

        char firstLetter = word.charAt(0);
        String tableName = firstLetter + "_words";
        Cursor cursor;
        openDatabase();
        int word_id = -1;
        String[] columns = new String[]{Constants.DB.MEANING_ID, Constants.DB.MEANING_WORD};
        String[] columnsMeaningTable = new String[]{Constants.DB.MEANING_ID, Constants.DB.MEANING_MEANING};
        if ((firstLetter < 'a' || firstLetter > 'z') && (firstLetter < 'A' || firstLetter > 'Z')) {
            cursor = sqLiteDatabase.query(Constants.DB.EXTRAS_TABLE_NAME, columns, Constants.DB.MEANING_WORD + "=\"" + word + '\"', null, null, null, null);
        } else {
            cursor = sqLiteDatabase.query(tableName, columns, Constants.DB.MEANING_WORD + "=\"" + word + '\"', null, null, null, null);
        }

        while (cursor.moveToNext()) {
            word_id = cursor.getInt(cursor.getColumnIndex(Constants.DB.MEANING_ID));
        }
        if(word_id == -1){
            return null;
        }
        Cursor cursorMeaning = sqLiteDatabase.query(Constants.DB.MEANINGS_TABLE_NAME, columnsMeaningTable, Constants.DB.MEANING_ID + "=\"" + word_id + '\"', null, null, null, null);
        while (cursorMeaning.moveToNext() && word_id != -1) {
            meaningStringBuilder.append(cursorMeaning.getString(cursorMeaning.getColumnIndex(Constants.DB.MEANING_MEANING)));
        }
        meaningStringBuilder.append("</div>");
        meaningStringBuilder.append("</body>");
        meaningStringBuilder.append("</html>");
        closeDatabase();
        Log.d(TAG, "getMeaning: " + meaningStringBuilder.toString());
        return meaningStringBuilder.toString();
    }
}
