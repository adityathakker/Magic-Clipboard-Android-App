package com.adityathakker.copyactions.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.copyactions.AppConst;
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
    private DataUpdateReceiver dataUpdateReceiver;
    private Spinner spinnerContent, spinnerDate;

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
        tempCopyRecordList = databaseHelper.getAllCopyRecords(null, null, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
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

        spinnerDate = (Spinner) layoutView.findViewById(R.id.fragment_history_spinner_date);
        spinnerContent = (Spinner) layoutView.findViewById(R.id.fragment_history_spinner_content);
        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(context,
                R.array.spinner_date, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterContent = ArrayAdapter.createFromResource(context,
                R.array.spinner_content, android.R.layout.simple_spinner_item);

        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapterDate);
        spinnerDate.setSelection(1);
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerContentPosition = spinnerContent.getSelectedItemPosition();
                Log.d(TAG, "onItemSelected: Spinner Content Position: " + spinnerContentPosition);
                if (position == 0) {
                    Log.d(TAG, "Spinner Date onItemSelected: Ascending");
                    filterByDate(spinnerContentPosition, "ASC");
                } else {
                    Log.d(TAG, "Spinner Date onItemSelected: Descending");
                    filterByDate(spinnerContentPosition, "DESC");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Spinner Date onNothingSelected: Nothing Changed");
            }
        });

        adapterContent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContent.setAdapter(adapterContent);
        spinnerContent.setSelection(0);
        spinnerContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerDatePosition = spinnerDate.getSelectedItemPosition();
                Log.d(TAG, "onItemSelected: Spinner Date Position: " + spinnerDatePosition);
                if (position == 0) {
                    Log.d(TAG, "Spinner Content onItemSelected: All Content");
                    filterByContent(spinnerDatePosition, null, null);

                } else if (position == 1) {
                    Log.d(TAG, "Spinner Content onItemSelected: Fav Only");
                    filterByContent(spinnerDatePosition, AppConst.DB.HISTORY_IS_FAV + "=?", new String[]{"1"});

                } else {
                    Log.d(TAG, "Spinner Content onItemSelected: Non Fav Only");
                    filterByContent(spinnerDatePosition, AppConst.DB.HISTORY_IS_FAV + "=?", new String[]{"0"});
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Spinner Content onNothingSelected: Nothing Changed");
            }
        });

        databaseHelper.close();
        return layoutView;
    }

    private void filterByContent(int spinnerDatePosition, String whereClause, String whereArg[]) {
        if (spinnerDatePosition == 0) {
            //Asc
            tempCopyRecordList = databaseHelper.getAllCopyRecords(whereClause, whereArg, AppConst.DB.HISTORY_TIMESTAMP);
        } else {
            //Desc
            tempCopyRecordList = databaseHelper.getAllCopyRecords(whereClause, whereArg, AppConst.DB.HISTORY_TIMESTAMP + " DESC");
        }
        historyRecyclerAdapter.setCopyRecordList(tempCopyRecordList);
        historyRecyclerAdapter.notifyDataSetChanged();
        recyclerView.dataChanged();
    }

    private void filterByDate(int spinnerContentPosition, String orderBy) {
        switch (spinnerContentPosition) {
            case 0:
                tempCopyRecordList = databaseHelper.getAllCopyRecords(null, null, AppConst.DB.HISTORY_TIMESTAMP + " " + orderBy);
                break;
            case 1:
                tempCopyRecordList = databaseHelper.getAllCopyRecords(AppConst.DB.HISTORY_IS_FAV + "=?", new String[]{"1"}, AppConst.DB.HISTORY_TIMESTAMP + " " + orderBy);
                break;
            case 2:
                tempCopyRecordList = databaseHelper.getAllCopyRecords(AppConst.DB.HISTORY_IS_FAV + "=?", new String[]{"0"}, AppConst.DB.HISTORY_TIMESTAMP + " " + orderBy);
                break;
        }
        historyRecyclerAdapter.setCopyRecordList(tempCopyRecordList);
        historyRecyclerAdapter.notifyDataSetChanged();
        recyclerView.dataChanged();
    }

    private void addCopyRecordAndNotify(CopyRecord copyRecord, int index) {
        historyRecyclerAdapter.addCopyRecordToList(copyRecord, index);
        historyRecyclerAdapter.notifyItemInserted(index);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(AppConst.RequestCodes.NEW_RECORD_ADDED);
        context.registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) context.unregisterReceiver(dataUpdateReceiver);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppConst.RequestCodes.NEW_RECORD_ADDED)) {
                CopyRecord copyRecord = databaseHelper.getLatestCopyRecord();
                int spinnerContentPosition = spinnerContent.getSelectedItemPosition();
                int spinnerDatePosition = spinnerDate.getSelectedItemPosition();
                if (spinnerDatePosition == 0) {
                    if (spinnerContentPosition == 0) {
                        int lastIndex = historyRecyclerAdapter.getCopyRecordList().size();
                        addCopyRecordAndNotify(copyRecord, lastIndex);
                    } else if (spinnerContentPosition == 2) {
                        int lastIndex = historyRecyclerAdapter.getCopyRecordList().size();
                        addCopyRecordAndNotify(copyRecord, lastIndex);
                    }
                } else {
                    if (spinnerContentPosition == 0) {
                        addCopyRecordAndNotify(copyRecord, 0);
                    } else if (spinnerContentPosition == 2) {
                        addCopyRecordAndNotify(copyRecord, 0);
                    }
                }
            }
        }
    }
}
