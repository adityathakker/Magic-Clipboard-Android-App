package com.adityathakker.copyactions.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.LocalApp;

import java.util.List;

/**
 * Created by adityajthakker on 1/7/16.
 */

public class InstalledAppsActionsWordRecyclerAdapter extends RecyclerView.Adapter<InstalledAppsActionsWordRecyclerAdapter.ActionsRecyclerViewHolder> {
    public static final String TAG = InstalledAppsActionsWordRecyclerAdapter.class.getSimpleName();
    private Context context;
    private DatabaseHelper databaseHelper;
    private List<LocalApp> localAppsList;

    public InstalledAppsActionsWordRecyclerAdapter(Context context, List<LocalApp> localAppsList) {
        this.context = context;
        this.localAppsList = localAppsList;
        databaseHelper = new DatabaseHelper(context);
    }

    public void setLocalAppsList(List<LocalApp> localAppsList) {
        this.localAppsList = localAppsList;
    }

    public void replaceLocalAppAt(LocalApp localApp, int index) {
        localAppsList.set(index, localApp);
    }

    @Override
    public ActionsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actions_recyclerview_row, parent, false);
        ActionsRecyclerViewHolder viewHolder = new ActionsRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ActionsRecyclerViewHolder holder, int position) {
        LocalApp localApp = localAppsList.get(position);
        holder.icon.setImageDrawable(localApp.getIcon());
        holder.name.setText(localApp.getLocalName());
        holder.switchCompat.setChecked(localApp.getEnabled());
    }

    @Override
    public int getItemCount() {
        return localAppsList.size();
    }

    class ActionsRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView name;
        SwitchCompat switchCompat;

        public ActionsRecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.actions_recyclerview_row_name);
            icon = (ImageView) itemView.findViewById(R.id.actions_recyclerview_row_icon);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.actions_recyclerview_row_switch);
            switchCompat.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onItemClick: Local App Updated");
            LocalApp localApp = localAppsList.get(getAdapterPosition());
            localApp.setEnabled(!localApp.getEnabled());
            databaseHelper.updateLocalApp(localApp);
            replaceLocalAppAt(localApp, getAdapterPosition());
            notifyItemChanged(getAdapterPosition());
        }
    }
}
