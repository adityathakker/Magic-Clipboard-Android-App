package com.adityathakker.copyactions;

/**
 * Created by adityajthakker on 12/6/16.
 */
public abstract class AppConst {
    public static class SharedPrefs {
        public static final String IS_FIRST_TIME = "is_first_time";
        public static final String POPUP_STYLE_KEY = "popupStyle";
        public static final String DIALOG_POPUP_VALUE = "0";
        public static final String NOTIF_POPUP_VALUE = "1";

        //BuiltIn Actions
        public static final String BUILT_IN_ACTIONS_SEARCH = "pref_popup_box_word_built_in_action_search";
        public static final String BUILT_IN_ACTIONS_SHARE = "pref_popup_box_word_built_in_action_share";
        public static final String BUILT_IN_ACTIONS_MAPS = "pref_popup_box_word_built_in_action_maps";
        public static final String BUILT_IN_ACTIONS_TRANSLATE = "pref_popup_box_word_built_in_action_translate";
        public static final String BUILT_IN_ACTIONS_SPEAK = "pref_popup_box_word_built_in_action_speak";
    }

    public static class RequestCodes {
        public static final String NEW_RECORD_ADDED = "com.adityathakker.copyactions.newCopyRecordAdded";
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


        //Actions_Sent Table
        public static final String ACTIONS_SENT_WORD_TABLE_NAME = "actions_sent_word";

        public static final String ACTIONS_SENT_WORD_ID = "id";
        public static final String ACTIONS_SENT_WORD_PACKAGE = "package_name";
        public static final String ACTIONS_SENT_WORD_ACTIVITY = "activity_name";
        public static final String ACTIONS_SENT_WORD_ENABLED = "enabled";





    }

    public static class RegEx {
        public static final String URL_REGEX = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+(:[0-9]+)?|(?:ww\u200C\u200Bw.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?\u200C\u200B(?:[\\w]*))?)";
        public static final String WORD_REGEX = "^\\b[\\S]+\\b$";
    }

    public static class APIKeys {
        public static final String GOOGLE_URL_SHORTNER_API_KEY = "AIzaSyC1XAPi4haCC8rDCzOqSGyb0QTLiRaydUA";
    }
}
