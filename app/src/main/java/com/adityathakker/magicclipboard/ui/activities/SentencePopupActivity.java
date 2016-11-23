package com.adityathakker.magicclipboard.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.utils.BusProvider;
import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.adapters.PopupWordActionsRA;
import com.adityathakker.magicclipboard.ui.custom.RecyclerEmptyView;
import com.adityathakker.magicclipboard.utils.CommonTasks;

import java.util.List;

/**
 * Created by adityajthakker on 5/7/16.
 */
public class SentencePopupActivity extends AppCompatActivity {
    private static final String TAG = SentencePopupActivity.class.getSimpleName();
    private RecyclerEmptyView recyclerEmptyView;
    private RelativeLayout displayCopiedLayout;
    private TextView displayCopiedTextView;
    private String copiedString;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sent_popup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        copiedString = getIntent().getStringExtra("copiedString");
        databaseHelper = new DatabaseHelper(this);

        if(sharedPreferences.getBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_SAVING_ENABLED,true)){
            databaseHelper.addClipboardLog(copiedString);
            Intent intent = new Intent(Constants.Codes.CLIPBOARD_RECORD_ADDED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            Log.d(TAG, "onCreate: Saved in History");
        }else{
            Log.d(TAG, "onCreate: Saving is Disabled");
        }

        recyclerEmptyView = (RecyclerEmptyView) findViewById(R.id.activity_sent_popup_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerEmptyView.setLayoutManager(linearLayoutManager);

        List<Object> actionsList = CommonTasks.getEnabledActionsWordList(this);

        PopupWordActionsRA popupWordActionsRA = new PopupWordActionsRA(this, actionsList, copiedString, BusProvider.getInstance());
        recyclerEmptyView.setAdapter(popupWordActionsRA);

        displayCopiedLayout = (RelativeLayout) findViewById(R.id.activity_sent_popup_display_copied_layout);
        if(sharedPreferences.getBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_DISPLAY_COPIED_ENABLED,true)){
            displayCopiedLayout.setVisibility(View.VISIBLE);
            displayCopiedTextView = (TextView) findViewById(R.id.activity_sent_popup_textview_display_copied);
            displayCopiedTextView.setText(copiedString);
        }else{
            Log.d(TAG, "onCreate: Further Selection Box is Disabled");
            displayCopiedLayout.setVisibility(View.GONE);
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
