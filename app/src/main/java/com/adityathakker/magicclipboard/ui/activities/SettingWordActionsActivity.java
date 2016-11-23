package com.adityathakker.magicclipboard.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.adapters.SettingWordActionsRA;
import com.adityathakker.magicclipboard.ui.custom.RecyclerEmptyView;
import com.adityathakker.magicclipboard.utils.CommonTasks;
import com.adityathakker.magicclipboard.utils.SimpleDividerItemDecoration;

import java.util.List;

public class SettingWordActionsActivity extends AppCompatActivity {

    private static final String TAG = SettingWordActionsActivity.class.getCanonicalName();

    private RecyclerEmptyView recyclerView;
    private TextView emptyTextView;
    private List<Object> actionsList;
    private SettingWordActionsRA settingWordActionsRA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_word_actions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.app.ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        actionsList = CommonTasks.getAllActionsWordList(this);

        recyclerView = (RecyclerEmptyView) findViewById(R.id.content_actions_choose_word_recyclerview);
        emptyTextView = (TextView) findViewById(R.id.content_actions_choose_word_textview_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setEmptyView(emptyTextView);

        settingWordActionsRA = new SettingWordActionsRA(this, actionsList);
        recyclerView.setAdapter(settingWordActionsRA);
    }


}
