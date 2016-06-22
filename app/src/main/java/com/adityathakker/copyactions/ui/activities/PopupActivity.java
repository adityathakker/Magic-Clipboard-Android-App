package com.adityathakker.copyactions.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.adapters.MeaningRecyclerAdapter;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.ui.custom.RecyclerEmptyView;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;

import java.util.List;
import java.util.regex.Pattern;

public class PopupActivity extends AppCompatActivity {
    private static final String TAG = PopupActivity.class.getSimpleName();
    private TextView share, search, map, translate;
    private LinearLayout firstRowOptions, secondRowOptions;
    private RelativeLayout meaningLayout;
    private String copiedString;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(true);

        copiedString = getIntent().getStringExtra("copiedString");

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.insertNewCopyRecord(copiedString);
        databaseHelper.close();

        CopyRecord latestCopyRecord = databaseHelper.getLatestCopyRecord();
        if (HistoryFragment.historyRecyclerAdapter != null && HistoryFragment.recyclerView != null) {
            HistoryFragment.historyRecyclerAdapter.addCopyRecordToList(latestCopyRecord, 0);
            HistoryFragment.historyRecyclerAdapter.notifyItemInserted(0);
            HistoryFragment.recyclerView.getLayoutManager().scrollToPosition(0);
        }

        share = (TextView) findViewById(R.id.activity_popup_share);
        search = (TextView) findViewById(R.id.activity_popup_search);
        map = (TextView) findViewById(R.id.activity_popup_map);
        translate = (TextView) findViewById(R.id.activity_popup_translate);
        firstRowOptions = (LinearLayout) findViewById(R.id.activity_popup_first_row);
        secondRowOptions = (LinearLayout) findViewById(R.id.activity_popup_second_row);
        meaningLayout = (RelativeLayout) findViewById(R.id.activity_popup_meaning_layout);

        final String copiedStringLowered = copiedString.toLowerCase();
        if (Pattern.matches(AppConst.RegEx.URL_REGEX, copiedStringLowered)) {
            Log.d(TAG, "onCreate: Copied String is a Link");

        } else if (Pattern.matches(AppConst.RegEx.WORD_REGEX, copiedStringLowered)) {
            Log.d(TAG, "onCreate: Copied String is a Word");
            firstRowOptions.setVisibility(View.VISIBLE);
            secondRowOptions.setVisibility(View.GONE);
            share.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            map.setVisibility(View.VISIBLE);
            translate.setVisibility(View.VISIBLE);
            TextView supportMeaning = (TextView) findViewById(R.id.activity_popup_support_meaning);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, copiedString);
                    startActivity(Intent.createChooser(shareIntent, "Share: " + copiedString));
                }
            });

            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + copiedString);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        String url = "https://www.google.co.in/maps/search/" + copiedString;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                }
            });

            translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://translate.google.com/m/translate#auto/en/" + copiedString;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                    searchIntent.putExtra(SearchManager.QUERY, copiedString);
                    startActivity(searchIntent);
                }
            });

            List<String> meaningsList = databaseHelper.getMeanings(copiedStringLowered);
            if (meaningsList != null && meaningsList.size() > 0) {
                supportMeaning.setVisibility(View.VISIBLE);
                meaningLayout.setVisibility(View.VISIBLE);
                MeaningRecyclerAdapter meaningRecyclerAdapter = new MeaningRecyclerAdapter(this, meaningsList);


                RecyclerEmptyView recyclerEmptyView = (RecyclerEmptyView) findViewById(R.id.activity_popup_meaning_recyclerview);
                recyclerEmptyView.setLayoutManager(new LinearLayoutManager(this));
                recyclerEmptyView.setAdapter(meaningRecyclerAdapter);
            } else {
                supportMeaning.setVisibility(View.GONE);
                meaningLayout.setVisibility(View.GONE);
            }


        } else {
            Log.d(TAG, "onCreate: Copied String is a Sentence");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
