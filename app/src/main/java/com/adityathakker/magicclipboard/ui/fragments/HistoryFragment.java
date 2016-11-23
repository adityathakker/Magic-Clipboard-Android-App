package com.adityathakker.magicclipboard.ui.fragments;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.adapters.ClipboardHistoryRA;
import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.events.ClipboardLogAdditionEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogDeletionEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogFavoriteChangeEvent;
import com.adityathakker.magicclipboard.models.ClipboardLog;
import com.adityathakker.magicclipboard.ui.activities.DisplayClipboardLogActivity;
import com.adityathakker.magicclipboard.ui.custom.RecyclerEmptyView;
import com.adityathakker.magicclipboard.utils.BusProvider;
import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.utils.SimpleDividerItemDecoration;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;


public class HistoryFragment extends Fragment {
    private static final String TAG = HistoryFragment.class.getSimpleName();
    private RecyclerEmptyView recyclerView;
    private ClipboardHistoryRA clipboardHistoryRA;
    private Context context;
    private DatabaseHelper databaseHelper;
    private ClipboardManager clipboardManager;
    private TextView emptyView;
    private List<ClipboardLog> tempClipboardLogList;
    private Spinner spinnerContent, spinnerDate;
    private Bus bus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        bus = BusProvider.getInstance();
        bus.register(this);

        View layoutView = inflater.inflate(R.layout.fragment_history, container, false);

        setupRecyclerView(layoutView);
        setupSpinners(layoutView);

        return layoutView;
    }

    private void setupRecyclerView(View layoutView) {
        emptyView = (TextView) layoutView.findViewById(R.id.fragment_history_textview_empty);

        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        recyclerView = (RecyclerEmptyView) layoutView.findViewById(R.id.fragment_history_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        databaseHelper = new DatabaseHelper(context);
//        tempClipboardLogList = databaseHelper.getClipboardLogs(null, null, Constants.DB.HISTORY_TIMESTAMP + " DESC");
        clipboardHistoryRA = new ClipboardHistoryRA(null);

        clipboardHistoryRA.setOnRowClickListener(new ClipboardHistoryRA.OnRowClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String copyString = tempClipboardLogList.get(position).getClip();
                ClipData clipToCopy = ClipData.newPlainText("copyString", copyString);
                clipboardManager.setPrimaryClip(clipToCopy);
                Log.d(TAG, "onItemClick: Copied => " + copyString);
                Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        clipboardHistoryRA.setOnInfoClickListener(new ClipboardHistoryRA.OnInfoClickListener() {
            @Override
            public void onInfoClick(View view, int position) {
                Log.d(TAG, "onInfoClick: Info Clicked");
                ClipboardLog tempClipboardLog = tempClipboardLogList.get(position);
                Intent intent = new Intent(context, DisplayClipboardLogActivity.class);
                intent.putExtra("id", Long.toString(tempClipboardLog.getId()));
                intent.putExtra("string", tempClipboardLog.getClip());
                intent.putExtra("isFav", Boolean.toString(tempClipboardLog.getFav()));
                intent.putExtra("timeStamp", new java.sql.Timestamp(tempClipboardLog.getTimestamp().getTime()).toString());
                intent.putExtra("position", Integer.toString(position));
                context.startActivity(intent);
            }
        });

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        emptyView.setText("No Records In History");
        recyclerView.setEmptyView(emptyView);

        recyclerView.setAdapter(clipboardHistoryRA);
        recyclerView.dataChanged();
    }

    private void setupSpinners(View layoutView) {
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
                    filterByContent(spinnerDatePosition, Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"1"});

                } else {
                    Log.d(TAG, "Spinner Content onItemSelected: Non Fav Only");
                    filterByContent(spinnerDatePosition, Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"0"});
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Spinner Content onNothingSelected: Nothing Changed");
            }
        });
    }

    private void filterByContent(int spinnerDatePosition, String whereClause, String whereArg[]) {
        if (spinnerDatePosition == 0) {
            //Asc
            tempClipboardLogList = databaseHelper.getClipboardLogs(whereClause, whereArg, Constants.DB.HISTORY_TIMESTAMP);
        } else {
            //Desc
            tempClipboardLogList = databaseHelper.getClipboardLogs(whereClause, whereArg, Constants.DB.HISTORY_TIMESTAMP + " DESC");
        }
        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
        clipboardHistoryRA.notifyDataSetChanged();
        recyclerView.dataChanged();
    }

    private void filterByDate(int spinnerContentPosition, String orderBy) {
        switch (spinnerContentPosition) {
            case 0:
                tempClipboardLogList = databaseHelper.getClipboardLogs(null, null, Constants.DB.HISTORY_TIMESTAMP + " " + orderBy);
                break;
            case 1:
                tempClipboardLogList = databaseHelper.getClipboardLogs(Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"1"}, Constants.DB.HISTORY_TIMESTAMP + " " + orderBy);
                break;
            case 2:
                tempClipboardLogList = databaseHelper.getClipboardLogs(Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"0"}, Constants.DB.HISTORY_TIMESTAMP + " " + orderBy);
                break;
        }
        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
        clipboardHistoryRA.notifyDataSetChanged();
        recyclerView.dataChanged();
    }

    private void addCopyRecordAndNotify(ClipboardLog clipboardLog, int index) {
        tempClipboardLogList.add(index, clipboardLog);
        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
        clipboardHistoryRA.notifyItemInserted(index);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Subscribe
    public void additionEvent(ClipboardLogAdditionEvent event) {
        Log.d(TAG, "Addition Event Occurred");
        if(event != null){
            int spinnerContentPosition = spinnerContent.getSelectedItemPosition();
            int spinnerDatePosition = spinnerDate.getSelectedItemPosition();
            if (spinnerDatePosition == 0 && (spinnerContentPosition == 0 || spinnerContentPosition == 2)) {
                //asc + all
                addCopyRecordAndNotify(event.getClipboardLog(), tempClipboardLogList.size());
            } else if(spinnerDatePosition == 1 && (spinnerContentPosition == 0 || spinnerContentPosition == 2)){
                //desc + all
                addCopyRecordAndNotify(event.getClipboardLog(), 0);
            }
        }
    }

    @Subscribe
    public void deletionEvent(ClipboardLogDeletionEvent event) {
        Log.d(TAG, "Deletion Event Occurred");
        if(event != null){
            tempClipboardLogList.remove(event.getPosition());
            clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
            clipboardHistoryRA.notifyItemRemoved(event.getPosition());
        }
    }

    @Subscribe
    public void favoriteChangeEvent(ClipboardLogFavoriteChangeEvent event){
        Log.d(TAG, "Favorite Change Event Occurred");
        if(event != null){
//            if(event.getSource().equals(Constants.Codes.SOURCE_WORD_POPUP)){
//                int spinnerContentPosition = spinnerContent.getSelectedItemPosition();
//                int spinnerDatePosition = spinnerDate.getSelectedItemPosition();
//                Log.d(TAG, "favoriteChangeEvent: Date: " + spinnerDatePosition + " Content: " + spinnerContentPosition);
//                if (spinnerDatePosition == 0 ) {
//                    if(spinnerContentPosition == 0){
//                        //asc + all
//                        int lastIndex = clipboardHistoryRA.getClipboardLogList().size() - 1;
//                        ClipboardLog temp = tempClipboardLogList.get(lastIndex);
//                        temp.setFav(event.getFav());
//                        tempClipboardLogList.remove(lastIndex);
//                        tempClipboardLogList.add(lastIndex, temp);
//                        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                        clipboardHistoryRA.notifyItemInserted(lastIndex);
//                    }else if(spinnerContentPosition == 1){
//                        //asc + fav
//                        int lastIndex = clipboardHistoryRA.getClipboardLogList().size() - 1;
//                        ClipboardLog temp = databaseHelper.getLatestClipboardLog();
//                        temp.setFav(event.getFav());
//                        tempClipboardLogList.add(lastIndex + 1, temp);
//                        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                        clipboardHistoryRA.notifyItemInserted(lastIndex);
//                    }else if(spinnerContentPosition == 2){
//                        //asc + non-fav
//                        int lastIndex = clipboardHistoryRA.getClipboardLogList().size() - 1;
//                        tempClipboardLogList.remove(lastIndex);
//                        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                        clipboardHistoryRA.notifyItemRemoved(lastIndex);
//                    }
//
//                } else if (spinnerDatePosition == 1) {
//                    if(spinnerContentPosition == 0 ){
//                        //desc + all
//                        ClipboardLog temp = tempClipboardLogList.get(0);
//                        temp.setFav(event.getFav());
//                        tempClipboardLogList.remove(0);
//                        tempClipboardLogList.add(0, temp);
//                        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                        clipboardHistoryRA.notifyItemInserted(0);
//                    }else if(spinnerContentPosition == 1){
//                        //desc + fav
//                        ClipboardLog temp = databaseHelper.getLatestClipboardLog();
//                        temp.setFav(event.getFav());
//                        tempClipboardLogList.add(0, temp);
//                        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                        clipboardHistoryRA.notifyItemInserted(0);
//                    }else if(spinnerContentPosition == 2){
//                        //desc + non-fav
//                        tempClipboardLogList.remove(0);
//                        clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                        clipboardHistoryRA.notifyItemRemoved(0);
//                    }
//                }
//            }else if(event.getSource().equals(Constants.Codes.SOURCE_DISPLAY_LOG_ACTIVTY)){
//                int spinnerContentPosition = spinnerContent.getSelectedItemPosition();
//                int spinnerDatePosition = spinnerDate.getSelectedItemPosition();
//                Log.d(TAG, "favoriteChangeEvent: Date: " + spinnerDatePosition + " Content: " + spinnerContentPosition);
//                if(event.getFav()){
//                    if (spinnerDatePosition == 0 ) {
//                        if(spinnerContentPosition == 0){
//                            //asc + all
//                            ClipboardLog temp = tempClipboardLogList.get(event.getPosition());
//                            tempClipboardLogList.remove(event.getPosition());
//                            temp.setFav(event.getFav());
//                            tempClipboardLogList.add(event.getPosition(), temp);
//                            clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                            clipboardHistoryRA.notifyItemChanged(event.getPosition());
//                        }else if(spinnerContentPosition == 2){
//                            //asc + non-fav
//                            int lastIndex = clipboardHistoryRA.getClipboardLogList().size() - 1;
//                            tempClipboardLogList.remove(lastIndex);
//                            clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                            clipboardHistoryRA.notifyItemRemoved(lastIndex);
//                        }
//
//                    } else if (spinnerDatePosition == 1) {
//                        if(spinnerContentPosition == 0 ){
//                            //desc + all
//                            ClipboardLog temp = tempClipboardLogList.get(0);
//                            temp.setFav(event.getFav());
//                            tempClipboardLogList.remove(0);
//                            tempClipboardLogList.add(0, temp);
//                            clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                            clipboardHistoryRA.notifyItemInserted(0);
//                        }else if(spinnerContentPosition == 1){
//                            //desc + fav
//                            ClipboardLog temp = databaseHelper.getLatestClipboardLog();
//                            temp.setFav(event.getFav());
//                            tempClipboardLogList.add(0, temp);
//                            clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                            clipboardHistoryRA.notifyItemInserted(0);
//                        }else if(spinnerContentPosition == 2){
//                            //desc + non-fav
//                            tempClipboardLogList.remove(0);
//                            clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                            clipboardHistoryRA.notifyItemRemoved(0);
//                        }
//                    }
//                }else{
//
//                }
//                ClipboardLog temp = tempClipboardLogList.get(event.getPosition());
//                tempClipboardLogList.remove(event.getPosition());
//                temp.setFav(event.getFav());
//                tempClipboardLogList.add(event.getPosition(), temp);
//                clipboardHistoryRA.setClipboardLogList(tempClipboardLogList);
//                clipboardHistoryRA.notifyItemChanged(event.getPosition());
//            }
        updateHistoryContent();
        }
    }

    public void updateHistoryContent(){
        int spinnerDatePosition = spinnerDate.getSelectedItemPosition();
        int spinnerContentPosition = spinnerContent.getSelectedItemPosition();
        if (spinnerContentPosition == 0) {
            filterByContent(spinnerDatePosition, null, null);
        } else if (spinnerContentPosition == 1) {
            filterByContent(spinnerDatePosition, Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"1"});
        } else {
            filterByContent(spinnerDatePosition, Constants.DB.HISTORY_IS_FAV + "=?", new String[]{"0"});
        }
    }
}
