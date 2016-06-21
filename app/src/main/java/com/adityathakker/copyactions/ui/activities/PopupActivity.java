package com.adityathakker.copyactions.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;

public class PopupActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView share, search, map, translate, speak;
    private String copiedString;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        setFinishOnTouchOutside(true);
        databaseHelper = new DatabaseHelper(this);
        copiedString = getIntent().getStringExtra("copiedString");
        databaseHelper.insertNewCopyRecord(copiedString);
        databaseHelper.close();

        CopyRecord latestCopyRecord = databaseHelper.getLatestCopyRecord();
        if (HistoryFragment.historyRecyclerAdapter != null) {
            HistoryFragment.historyRecyclerAdapter.addCopyRecordToList(latestCopyRecord, 0);
            HistoryFragment.historyRecyclerAdapter.notifyItemInserted(0);
        }


        share = (TextView) findViewById(R.id.activity_popup_share);
        share.setOnClickListener(this);
        search = (TextView) findViewById(R.id.activity_popup_search);
        search.setOnClickListener(this);
        map = (TextView) findViewById(R.id.activity_popup_map);
        map.setOnClickListener(this);
        translate = (TextView) findViewById(R.id.activity_popup_translate);
        translate.setOnClickListener(this);
        speak = (TextView) findViewById(R.id.activity_popup_speak);
        speak.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_popup_share:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, copiedString);
                startActivity(Intent.createChooser(shareIntent, "Share: " + copiedString));
                break;
            case R.id.activity_popup_search:
                Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, copiedString);
                startActivity(searchIntent);
                break;
            case R.id.activity_popup_map:
                break;
            case R.id.activity_popup_translate:
                break;
            case R.id.activity_popup_speak:
                break;
        }
    }
}
