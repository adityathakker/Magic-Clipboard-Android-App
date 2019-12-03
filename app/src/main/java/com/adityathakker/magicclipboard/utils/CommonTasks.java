package com.adityathakker.magicclipboard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.models.InstalledApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adityajthakker on 6/7/16.
 */
public class CommonTasks {
    public static List<Object> getEnabledActionsWordList(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<Object> actionsList = new ArrayList<>();
        if (sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true)) {
            actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH);
        }

        if (sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true)) {
            actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE);
        }

        if (sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true)) {
            actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS);
        }

        if (sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true)) {
            actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE);
        }

        if (sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV, true)) {
            actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV);
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<InstalledApp> installedAppList = databaseHelper.getEnabledLocalAppsForSent();
        if(installedAppList != null){
            for (InstalledApp installedApp : installedAppList) {
                actionsList.add(installedApp);
            }
        }

        actionsList.add(Constants.Others.EDIT_ACTIONS_WORD);

        return actionsList;
    }

    public static void bubbleSort(int arr[]){
        int n = arr.length;
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (arr[j] > arr[j+1])
                {
                    // swap temp and arr[i]
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
    }

    public static List<Object> getAllActionsWordList(Context context) {
        List<Object> actionsList = new ArrayList<>();
        actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH);
        actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE);
        actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS);
        actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE);
        actionsList.add(Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV);

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<InstalledApp> installedAppList = databaseHelper.getAllLocalAppsForSent();
        for (InstalledApp installedApp : installedAppList) {
            actionsList.add(installedApp);
        }
        return actionsList;
    }
}
