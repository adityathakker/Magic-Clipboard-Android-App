package com.adityathakker.copyactions.ui.fragments;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.services.ClipboardService;
import com.adityathakker.copyactions.utils.TimeAgo;

import java.util.Date;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class HomeFragment extends Fragment {
    public static TextView totalCopyRecords, favCopyRecords, latestCopyRecordString, latestCopyRecordTimeStamp;
    private Context context;
    private DatabaseHelper databaseHelper;
    private DataUpdateReceiver dataUpdateReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        databaseHelper = new DatabaseHelper(context);
        View layoutView = inflater.inflate(R.layout.fragment_home, container, false);

        Switch onOffSwitch = (Switch) layoutView.findViewById(R.id.fragment_home_switch_on_off);
        if (isMyServiceRunning(ClipboardService.class)) {
            onOffSwitch.setChecked(true);
        } else {
            onOffSwitch.setChecked(false);
        }

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    context.startService(new Intent(context, ClipboardService.class));
                } else {
                    context.stopService(new Intent(context, ClipboardService.class));
                }
            }
        });

        totalCopyRecords = (TextView) layoutView.findViewById(R.id.fragment_home_total_records);
        favCopyRecords = (TextView) layoutView.findViewById(R.id.fragment_home_fav_records);
        latestCopyRecordString = (TextView) layoutView.findViewById(R.id.fragment_home_latest_string);
        latestCopyRecordTimeStamp = (TextView) layoutView.findViewById(R.id.fragment_home_latest_timestamp);

        totalCopyRecords.setText(databaseHelper.getTotalCopyRecords() + "");
        favCopyRecords.setText(databaseHelper.getTotalFavCopyRecords() + "");

        CopyRecord latestCopyRecord = databaseHelper.getLatestCopyRecord();
        if (latestCopyRecord != null) {
            latestCopyRecordString.setText(latestCopyRecord.getString());
            latestCopyRecordTimeStamp.setVisibility(View.VISIBLE);
            latestCopyRecordTimeStamp.setText(TimeAgo.toDuration(new Date().getTime() - latestCopyRecord.getTimestamp().getTime()));
        } else {
            latestCopyRecordString.setText("No records yet!");
            latestCopyRecordTimeStamp.setVisibility(View.GONE);
        }

        databaseHelper.close();
        return layoutView;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
            totalCopyRecords.setText((Integer.parseInt(totalCopyRecords.getText().toString()) + 1) + "");
        }
    }
}
