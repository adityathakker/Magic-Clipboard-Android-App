package com.adityathakker.copyactions;

/**
 * Created by adityajthakker on 12/6/16.
 */
public abstract class AppConst {
    public static class SharedPrefs {
        public static final String IS_FIRST_TIME = "is_first_time";
    }

    public static class DB {
        //DB Name
        public static final String DB_NAME = "copy_actions.db";
        public static final String DB_PATH = "/data/data/com.adityathakker.copyactions/databases/";
        public static final int DB_VERSION = 1;

        //History Table
        public static final String HISTORY_TABLE_NAME = "history_table";

        public static final String HISTORY_ID = "id";
        public static final String HISTORY_STRING = "string";
        public static final String HISTORY_TIMESTAMP = "timestamp";
        public static final String HISTORY_IS_FAV = "is_fav";

        public static final String CREATE_TABLE_HISTORY = "create table " + HISTORY_TABLE_NAME + "(" +
                HISTORY_ID + " integer primary key autoincrement," +
                HISTORY_STRING + " text," +
                HISTORY_IS_FAV + " integer," +
                HISTORY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        public static final String DROP_TABLE_HISTORY = "drop table if exists " + HISTORY_TABLE_NAME;


    }
}
