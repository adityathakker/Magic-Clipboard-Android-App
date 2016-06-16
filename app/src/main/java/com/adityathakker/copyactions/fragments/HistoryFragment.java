package com.adityathakker.copyactions.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.adapters.HistoryRecyclerAdapter;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.utils.SimpleDividerItemDecoration;

import java.util.List;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class HistoryFragment extends Fragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private ClipboardManager clipboardManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View layoutView = inflater.inflate(R.layout.fragment_history, container, false);

        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        recyclerView = (RecyclerView) layoutView.findViewById(R.id.fragment_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        databaseHelper = new DatabaseHelper(context);
        final List<CopyRecord> tempCopyRecordList = databaseHelper.getAllCopyRecords();
        HistoryRecyclerAdapter historyRecyclerAdapter = new HistoryRecyclerAdapter(context, tempCopyRecordList);
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

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setAdapter(historyRecyclerAdapter);


        databaseHelper.close();
        return layoutView;
    }
}
