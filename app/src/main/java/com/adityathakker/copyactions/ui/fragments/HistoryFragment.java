package com.adityathakker.copyactions.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.adapters.HistoryRecyclerAdapter;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.ui.activities.DisplayCopyActivity;
import com.adityathakker.copyactions.ui.custom.RecyclerEmptyView;
import com.adityathakker.copyactions.utils.SimpleDividerItemDecoration;

import java.util.List;


public class HistoryFragment extends Fragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    public static RecyclerEmptyView recyclerView;
    public static HistoryRecyclerAdapter historyRecyclerAdapter;
    private Context context;
    private DatabaseHelper databaseHelper;
    private ClipboardManager clipboardManager;
    private TextView emptyView;
    private List<CopyRecord> tempCopyRecordList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();

        View layoutView = inflater.inflate(R.layout.fragment_history, container, false);

        emptyView = (TextView) layoutView.findViewById(R.id.fragment_history_textview_empty);

        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        recyclerView = (RecyclerEmptyView) layoutView.findViewById(R.id.fragment_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        databaseHelper = new DatabaseHelper(context);
        tempCopyRecordList = databaseHelper.getAllCopyRecords();
        historyRecyclerAdapter = new HistoryRecyclerAdapter(context, tempCopyRecordList);

        historyRecyclerAdapter.setOnItemClickListener(new HistoryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String copyString = tempCopyRecordList.get(position).getString();
                ClipData clipToCopy = ClipData.newPlainText("copyString", copyString);
                clipboardManager.setPrimaryClip(clipToCopy);
                Log.d(TAG, "onItemClick: Copied => " + copyString);
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        historyRecyclerAdapter.setOnInfoClickListener(new HistoryRecyclerAdapter.OnInfoClickListener() {
            @Override
            public void onInfoClick(View view, int position) {
                Log.d(TAG, "onInfoClick: Info Clicked");
                CopyRecord tempCopyRecord = tempCopyRecordList.get(position);
                Intent intent = new Intent(context, DisplayCopyActivity.class);
                intent.putExtra("id", Long.toString(tempCopyRecord.getId()));
                intent.putExtra("string", tempCopyRecord.getString());
                intent.putExtra("isFav", Boolean.toString(tempCopyRecord.getFav()));
                intent.putExtra("timeStamp", new java.sql.Timestamp(tempCopyRecord.getTimestamp().getTime()).toString());
                intent.putExtra("position", Integer.toString(position));
                context.startActivity(intent);
            }
        });

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        emptyView.setText("No Records In History");
        recyclerView.setEmptyView(emptyView);

        recyclerView.setAdapter(historyRecyclerAdapter);
        recyclerView.dataChanged();


        databaseHelper.close();
        return layoutView;
    }

    @Override
    public void onResume() {
        super.onResume();
        
        /*final List<CopyRecord> tempCopyRecordList = databaseHelper.getAllCopyRecords();
        historyRecyclerAdapter.setCopyRecordList(tempCopyRecordList);
        historyRecyclerAdapter.notifyDataSetChanged();
        recyclerView.dataChanged();*/

    }
}
