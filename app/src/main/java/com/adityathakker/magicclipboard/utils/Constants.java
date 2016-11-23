package com.adityathakker.magicclipboard.utils;

import android.app.Application;

/**
 * Created by adityajthakker on 12/6/16.
 */
public abstract class Constants extends Application{

    public static class Others{
        public static final int BUILT_IN_ACTIONS_COUNT = 5;
        public static final String EDIT_ACTIONS_WORD = "edit_actions_word";

    }
    public static class SharedPrefs {

        public static final String IS_FIRST_TIME = "is_first_time";
        //BuiltIn Actions
        public static final String BUILT_IN_ACTIONS_SEARCH = "pref_popup_box_word_built_in_action_search";
        public static final String BUILT_IN_ACTIONS_SHARE = "pref_popup_box_word_built_in_action_share";
        public static final String BUILT_IN_ACTIONS_MAPS = "pref_popup_box_word_built_in_action_maps";
        public static final String BUILT_IN_ACTIONS_TRANSLATE = "pref_popup_box_word_built_in_action_translate";
        public static final String BUILT_IN_ACTIONS_SPEAK = "pref_popup_box_word_built_in_action_speak";
        public static final String BUILT_IN_ACTIONS_FAV = "pref_popup_box_word_built_in_action_favorite";
        //Prefs
        public static final String PREF_POPUP_BOX_WORD_SAVING_ENABLED= "pref_popup_box_word_saving_enable_disable";
        public static final String PREF_POPUP_BOX_WORD_ENABLED= "pref_popup_box_word_enable_disable";
        public static final String PREF_POPUP_BOX_WORD_MEANING_ENABLED = "pref_popup_box_word_meaning_enable_disable";
        public static final String PREF_POPUP_BOX_WORD_DISPLAY_COPIED_ENABLED = "pref_popup_box_word_display_copied_enable_disable";
        public static final String TOTAL_AVAILABLE_ACTIONS = "total_available_actions";
        public static final String TOTAL_USED_ACTIONS = "total_used_actions";

    }

    public static class Codes {
        public static final String CLIPBOARD_RECORD_ADDED = "com.adityathakker.magicclipboard.requestCodes.clipboardRecordAdded";
        public static final String ADDED_TO_FAV = "com.adityathakker.magicclipboard.requestCodes.addedToFav";
        public static final String REMOVED_FROM_FAV = "com.adityathakker.magicclipboard.requestCodes.deletedFromFav";
        public static final String ACTION_USED = "com.adityathakker.magicclipboard.requestCodes.actionUsed";
        public static final String CLIPBOARD_RECORD_DELETED = "com.adityathakker.magicclipboard.requestCodes.clipboardRecordDeleted";
        public static final String SOURCE_WORD_POPUP = "com.adityathakker.magicclipboard.requestCodes.sourceWordPopup";
        public static final String SOURCE_DISPLAY_LOG_ACTIVTY = "com.adityathakker.magicclipboard.requestCodes.sourceDisplayLogActivity";

    }

    public static class DB {
        //DB Name
        public static final String DB_NAME_MEANINGS = "magic_clipboard_dict.db";
        public static final String DB_NAME = "others.db";
        public static final String DB_PATH = "/data/data/com.adityathakker.magicclipboard/databases/";
        public static final int DB_VERSION = 1;

        //History Table
        public static final String CREATE_HISTORY_TABLE = "CREATE TABLE history_table (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " string TEXT," +
                " is_fav INTEGER," +
                " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";
        public static final String HISTORY_TABLE_NAME = "history_table";
        public static final String HISTORY_ID = "id";
        public static final String HISTORY_STRING = "string";
        public static final String HISTORY_TIMESTAMP = "timestamp";
        public static final String HISTORY_IS_FAV = "is_fav";


        //Actions_Sent Table
        public static final String CREATE_ACTIONS_SENT_TABLE = "CREATE TABLE actions_sent_word (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "package_name TEXT NOT NULL," +
                "activity_name TEXT NOT NULL," +
                "enabled INTEGER NOT NULL" +
                ");";
        public static final String ACTIONS_SENT_WORD_TABLE_NAME = "actions_sent_word";
        public static final String ACTIONS_SENT_WORD_ID = "id";
        public static final String ACTIONS_SENT_WORD_PACKAGE = "package_name";
        public static final String ACTIONS_SENT_WORD_ACTIVITY = "activity_name";
        public static final String ACTIONS_SENT_WORD_ENABLED = "enabled";

        //Actions Links Table
        public static final String CREATE_ACTION_LINKS_TABLE = "CREATE TABLE actions_link (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "package_name TEXT NOT NULL," +
                "activity_name TEXT NOT NULL," +
                "enabled INTEGER NOT NULL" +
                ");";


        //Meanings Related Tables
        public static final String MEANING_ID = "_id";
        public static final String MEANING_WORD = "word";
        public static final String MEANING_MEANING = "meaning";
        public static final String EXTRAS_TABLE_NAME = "extras";
        public static final String MEANINGS_TABLE_NAME = "meanings";
    }

    public static class RegEx {
        public static final String WORD_REGEX = "^\\b[\\S]+\\b$";
    }

}
