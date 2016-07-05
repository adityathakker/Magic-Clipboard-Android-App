package com.adityathakker.copyactions.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.adapters.MeaningRecyclerAdapter;
import com.adityathakker.copyactions.adapters.WordPopupActionsRecyclerAdapter;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.LocalApp;
import com.adityathakker.copyactions.ui.custom.RecyclerEmptyView;

import java.util.ArrayList;
import java.util.List;

public class WordPopupActivity extends AppCompatActivity {
    private static final String TAG = WordPopupActivity.class.getSimpleName();
    /*private TextView share, search, map, translate, openInBrowser, shorten;
    private LinearLayout firstRowOptions, secondRowOptions;*/
    private RecyclerEmptyView recyclerEmptyView;
    private RelativeLayout meaningLayout;
    private String copiedString;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_word_popup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        copiedString = getIntent().getStringExtra("copiedString");

        databaseHelper = new DatabaseHelper(this);

        recyclerEmptyView = (RecyclerEmptyView) findViewById(R.id.activity_word_popup_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerEmptyView.setLayoutManager(linearLayoutManager);

        List<Object> actionsList = new ArrayList<>();
        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true)) {
            actionsList.add(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SEARCH);
        }

        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true)) {
            actionsList.add(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SHARE);
        }

        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true)) {
            actionsList.add(AppConst.SharedPrefs.BUILT_IN_ACTIONS_MAPS);
        }

        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true)) {
            actionsList.add(AppConst.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE);
        }

        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true)) {
            actionsList.add(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SPEAK);
        }

        List<LocalApp> localAppList = databaseHelper.getAllLocalAppsForSentEnabled();
        for (LocalApp localApp : localAppList) {
            actionsList.add(localApp);
        }

        WordPopupActionsRecyclerAdapter wordPopupActionsRecyclerAdapter = new WordPopupActionsRecyclerAdapter(this, actionsList, copiedString);
        recyclerEmptyView.setAdapter(wordPopupActionsRecyclerAdapter);


        meaningLayout = (RelativeLayout) findViewById(R.id.activity_popup_meaning_layout);

        List<String> meaningsList = databaseHelper.getMeanings(copiedString.toLowerCase());
        if (meaningsList != null && meaningsList.size() > 0) {
            TextView meaningSupport = (TextView) findViewById(R.id.activity_popup_support_meaning);
            meaningSupport.setText("Meaning Of The Word \"" + copiedString + "\"");
            meaningLayout.setVisibility(View.VISIBLE);
            MeaningRecyclerAdapter meaningRecyclerAdapter = new MeaningRecyclerAdapter(this, meaningsList);


            RecyclerEmptyView recyclerEmptyView = (RecyclerEmptyView) findViewById(R.id.activity_popup_meaning_recyclerview);
            recyclerEmptyView.setLayoutManager(new LinearLayoutManager(this));
            recyclerEmptyView.setAdapter(meaningRecyclerAdapter);
        } else {
            meaningLayout.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: No Meaning Available");
            Toast.makeText(WordPopupActivity.this, "No Meaning Defined For " + copiedString, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
