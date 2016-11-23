package com.adityathakker.magicclipboard.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.events.ActionUsedEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogAdditionEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogDeletionEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogFavoriteChangeEvent;
import com.adityathakker.magicclipboard.models.ClipboardLog;
import com.adityathakker.magicclipboard.utils.BusProvider;
import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.utils.TimeAgo;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    public  TextView totalCopyRecords, favCopyRecords, latestCopyRecordString, latestCopyRecordTimeStamp, totalActions, usedActions, latestCopyRecordSupportText;
    private Context context;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private  View divider4;
    private Bus bus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        databaseHelper = new DatabaseHelper(context);
        bus = BusProvider.getInstance();
        bus.register(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        View layoutView = inflater.inflate(R.layout.fragment_home, container, false);

        totalCopyRecords = (TextView) layoutView.findViewById(R.id.fragment_home_total_records);
        favCopyRecords = (TextView) layoutView.findViewById(R.id.fragment_home_fav_records);
        latestCopyRecordString = (TextView) layoutView.findViewById(R.id.fragment_home_latest_string);
        latestCopyRecordTimeStamp = (TextView) layoutView.findViewById(R.id.fragment_home_latest_timestamp);
        totalActions = (TextView) layoutView.findViewById(R.id.fragment_home_total_actions);
        usedActions = (TextView) layoutView.findViewById(R.id.fragment_home_used_actions);
        divider4 = layoutView.findViewById(R.id.fragment_home_divider4);
        latestCopyRecordSupportText = (TextView) layoutView.findViewById(R.id.fragment_home_textView_latest_support);

        totalCopyRecords.setText(databaseHelper.getClipboardLogsSize() + "");
        favCopyRecords.setText(databaseHelper.getFavoritesSize() + "");
        totalActions.setText(sharedPreferences.getInt(Constants.SharedPrefs.TOTAL_AVAILABLE_ACTIONS,0) + "");
        usedActions.setText(sharedPreferences.getInt(Constants.SharedPrefs.TOTAL_USED_ACTIONS,0) + "");

        ClipboardLog latestClipboardLog = databaseHelper.getLatestClipboardLog();
        if (latestClipboardLog != null) {
            changeVisibilityLatestCopyRecordView(View.VISIBLE);
            latestCopyRecordString.setText(latestClipboardLog.getClip());
            latestCopyRecordTimeStamp.setText(TimeAgo.toDuration(new Date().getTime() - latestClipboardLog.getTimestamp().getTime()));
        } else {
            changeVisibilityLatestCopyRecordView(View.GONE);
        }

        databaseHelper.close();
        return layoutView;
    }

    public void changeVisibilityLatestCopyRecordView(int visibility){
        latestCopyRecordSupportText.setVisibility(visibility);
        divider4.setVisibility(visibility);
        latestCopyRecordTimeStamp.setVisibility(visibility);
        latestCopyRecordString.setVisibility(visibility);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }


    @Subscribe
    public void additionEvent(ClipboardLogAdditionEvent event) {
        if(event != null){
            Log.d(TAG, "Addition Event Occurred");
            ClipboardLog clipboardLog = databaseHelper.getLatestClipboardLog();
            changeVisibilityLatestCopyRecordView(View.VISIBLE);
            latestCopyRecordString.setText(clipboardLog.getClip());
            latestCopyRecordTimeStamp.setText(TimeAgo.toDuration(new Date().getTime() - clipboardLog.getTimestamp().getTime()));

            totalCopyRecords.setText((Integer.parseInt(totalCopyRecords.getText().toString()) + 1) + "");
        }
    }

    @Subscribe
    public void deletionEvent(ClipboardLogDeletionEvent event) {
        Log.d(TAG, "Deletion Event Occurred");
        if(event != null){
            ClipboardLog clipboardLog = databaseHelper.getLatestClipboardLog();
            changeVisibilityLatestCopyRecordView(View.VISIBLE);
            latestCopyRecordString.setText(clipboardLog.getClip());
            latestCopyRecordTimeStamp.setText(TimeAgo.toDuration(new Date().getTime() - clipboardLog.getTimestamp().getTime()));

            favCopyRecords.setText(databaseHelper.getFavoritesSize() + "");
            totalCopyRecords.setText((Integer.parseInt(totalCopyRecords.getText().toString()) - 1) + "");
        }
    }

    @Subscribe
    public void favoriteChangeEvent(ClipboardLogFavoriteChangeEvent event){
        Log.d(TAG, "Favorite Change Event Occurred");
        if(event != null){
            if(event.getFav()){
                favCopyRecords.setText((Integer.parseInt(favCopyRecords.getText().toString()) + 1) + "");
            }else{
                favCopyRecords.setText((Integer.parseInt(favCopyRecords.getText().toString()) - 1) + "");
            }
        }
    }

    @Subscribe
    public void actionUsedEvent(ActionUsedEvent event){
        Log.d(TAG, "Action Used Event Occurred");
        usedActions.setText((Integer.parseInt(usedActions.getText().toString()) + 1) + "");

    }

}
