package com.adityathakker.copyactions.ui.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.services.ClipboardService;

/**
 * Created by adityajthakker on 15/6/16.
 */
public class HomeFragment extends Fragment {
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
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
}
