package com.adityathakker.magicclipboard.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.models.InstalledApp;
import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.R;

import java.util.List;

/**
 * Created by adityajthakker on 1/7/16.
 */

public class SettingWordActionsRA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = SettingWordActionsRA.class.getSimpleName();
    private Context context;
    private DatabaseHelper databaseHelper;
    private List<Object> objectList;
    private int INSTALLED_ACTION = 1;
    private int BUILT_IN_ACTION = 2;
    private SharedPreferences sharedPreferences;

    public SettingWordActionsRA(Context context, List<Object> objectList) {
        this.context = context;
        this.objectList = objectList;
        databaseHelper = new DatabaseHelper(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void replaceLocalAppAt(InstalledApp installedApp, int index) {
        objectList.set(index, installedApp);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == BUILT_IN_ACTION){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actions_recyclerview_row, parent, false);
            BuiltInActionsViewHolder viewHolder = new BuiltInActionsViewHolder(view);
            return viewHolder;
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actions_recyclerview_row, parent, false);
            InstalledActionsViewHolder viewHolder = new InstalledActionsViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (objectList.get(position) instanceof InstalledApp) {
            return INSTALLED_ACTION;
        } else if (objectList.get(position) instanceof String) {
            return BUILT_IN_ACTION;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == INSTALLED_ACTION){
            InstalledActionsViewHolder installedActionsViewHolder = (InstalledActionsViewHolder) holder;
            configureInstalledAction(position, installedActionsViewHolder);
        }else{
            BuiltInActionsViewHolder builtInActionsViewHolder = (BuiltInActionsViewHolder) holder;
            configureBuiltInAction(position, builtInActionsViewHolder);
        }

    }

    private void configureBuiltInAction(int position, BuiltInActionsViewHolder builtInActionsViewHolder) {
        String actionName = (String) objectList.get(position);
        builtInActionsViewHolder.icon.setAlpha(0.5f);
        switch (actionName){
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH:
                builtInActionsViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_black_48dp));
                builtInActionsViewHolder.name.setText("Search");
                builtInActionsViewHolder.switchCompat.setChecked(sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true));
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE:
                builtInActionsViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_share_black_48dp));
                builtInActionsViewHolder.name.setText("Share");
                builtInActionsViewHolder.switchCompat.setChecked(sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true));
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS:
                builtInActionsViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_near_me_black_48dp));
                builtInActionsViewHolder.name.setText("Maps");
                builtInActionsViewHolder.switchCompat.setChecked(sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true));
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE:
                builtInActionsViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_translate_black_48dp));
                builtInActionsViewHolder.name.setText("Translate");
                builtInActionsViewHolder.switchCompat.setChecked(sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true));
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK:
                builtInActionsViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_volume_up_black_48dp));
                builtInActionsViewHolder.name.setText("Speak");
                builtInActionsViewHolder.switchCompat.setChecked(sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true));
                break;

            case Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV:
                builtInActionsViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favorite_black_48dp));
                builtInActionsViewHolder.name.setText("Favorite");
                builtInActionsViewHolder.switchCompat.setChecked(sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true));
                break;
        }
    }

    private void configureInstalledAction(int position, InstalledActionsViewHolder installedActionsViewHolder) {
        InstalledApp installedApp = (InstalledApp) objectList.get(position);
        installedActionsViewHolder.icon.setImageDrawable(installedApp.getIcon());
        installedActionsViewHolder.name.setText(installedApp.getLocalName());
        installedActionsViewHolder.switchCompat.setChecked(installedApp.getEnabled());
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    private class InstalledActionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView name;
        SwitchCompat switchCompat;

        InstalledActionsViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.actions_recyclerview_row_name);
            icon = (ImageView) itemView.findViewById(R.id.actions_recyclerview_row_icon);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.actions_recyclerview_row_switch);
            switchCompat.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            InstalledApp installedApp = (InstalledApp) objectList.get(getAdapterPosition());
            installedApp.setEnabled(!installedApp.getEnabled());
            databaseHelper.updateLocalApp(installedApp);
            replaceLocalAppAt(installedApp, getAdapterPosition());
//            notifyItemChanged(getAdapterPosition());
            Log.d(TAG, "onItemClick: Installed Action Updated");
        }
    }

    private class BuiltInActionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView name;
        SwitchCompat switchCompat;

        BuiltInActionsViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.actions_recyclerview_row_name);
            icon = (ImageView) itemView.findViewById(R.id.actions_recyclerview_row_icon);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.actions_recyclerview_row_switch);
            switchCompat.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onItemClick: Built-In Action Updated");
            String actionName = (String) objectList.get(getAdapterPosition());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (actionName){
                case Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH:
                    editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH,!sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true));
                    editor.apply();
//                    notifyItemChanged(getAdapterPosition());
                    break;
                case Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE:
                    editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE,!sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true));
                    editor.apply();
//                    notifyItemChanged(getAdapterPosition());
                    break;
                case Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS:
                    editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS,!sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true));
                    editor.apply();
//                    notifyItemChanged(getAdapterPosition());
                    break;
                case Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE:
                    editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE,!sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true));
                    editor.apply();
//                    notifyItemChanged(getAdapterPosition());
                    break;
                case Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK:
                    editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK,!sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true));
                    editor.apply();
//                    notifyItemChanged(getAdapterPosition());
                    break;
                case Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV:
                    editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV,!sharedPreferences.getBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV, true));
                    editor.apply();
//                    notifyItemChanged(getAdapterPosition());
                    break;
            }
        }
    }
}
