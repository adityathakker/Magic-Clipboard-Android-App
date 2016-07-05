package com.adityathakker.copyactions.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.adapters.InstalledAppsActionsWordRecyclerAdapter;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.LocalApp;
import com.adityathakker.copyactions.ui.custom.RecyclerEmptyView;
import com.adityathakker.copyactions.utils.SimpleDividerItemDecoration;

import java.util.List;

public class InstalledAppsActionsWordActivity extends AppCompatActivity {

    private static final String TAG = InstalledAppsActionsWordActivity.class.getCanonicalName();

    private RecyclerEmptyView recyclerEmptyView;
    private TextView emptyTextView;
    private DatabaseHelper databaseHelper;
    private List<LocalApp> localAppsList;
    private InstalledAppsActionsWordRecyclerAdapter installedAppsActionsWordRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_choose_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = new DatabaseHelper(this);

        localAppsList = databaseHelper.getAllLocalAppsForSent();

        recyclerEmptyView = (RecyclerEmptyView) findViewById(R.id.content_actions_choose_word_recyclerview);
        emptyTextView = (TextView) findViewById(R.id.content_actions_choose_word_textview_empty);
        recyclerEmptyView.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmptyView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerEmptyView.setEmptyView(emptyTextView);

        installedAppsActionsWordRecyclerAdapter = new InstalledAppsActionsWordRecyclerAdapter(this, localAppsList);
        recyclerEmptyView.setAdapter(installedAppsActionsWordRecyclerAdapter);
    }


}
