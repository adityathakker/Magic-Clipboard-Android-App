package com.adityathakker.copyactions.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.ui.custom.RecyclerEmptyView;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;
import com.adityathakker.copyactions.utils.TimeAgo;

import java.util.Date;
import java.util.List;

/**
 * Created by adityajthakker on 15/6/16.
 */

public class HistoryRecyclerAdapter extends RecyclerEmptyView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {
    public static final String TAG = HistoryRecyclerAdapter.class.getSimpleName();

    private Context context;
    private OnItemClickListener clickListener;
    private OnInfoClickListener infoClickListener;
    private List<CopyRecord> copyRecordList;
    private Date currentTime;
    public HistoryRecyclerAdapter(Context context, List<CopyRecord> copyRecordList) {
        this.context = context;
        this.copyRecordList = copyRecordList;
        currentTime = new Date();
    }

    public List<CopyRecord> getCopyRecordList() {
        return copyRecordList;
    }

    public void setCopyRecordList(List<CopyRecord> copyRecordList) {
        this.copyRecordList = copyRecordList;
    }

    public void addCopyRecordToList(CopyRecord copyRecord, int index) {
        copyRecordList.add(index, copyRecord);
    }


    public void removeCopyRecordFromList(int index) {
        copyRecordList.remove(index);
        HistoryFragment.recyclerView.dataChanged();
    }

    public void replaceCopyRecordAt(CopyRecord copyRecord, int index) {
        copyRecordList.set(index, copyRecord);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recyclerview_row, parent, false);
        HistoryViewHolder viewHolder = new HistoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        CopyRecord tempCopyRecord = copyRecordList.get(position);
        holder.timeStamp.setText(TimeAgo.toDuration(currentTime.getTime() - tempCopyRecord.getTimestamp().getTime()));
        holder.string.setText(tempCopyRecord.getString());
        Log.v(TAG, "onBindViewHolder: CopyRecord => " + tempCopyRecord.toString());
    }

    @Override
    public int getItemCount() {
        return copyRecordList.size();
    }

    //Setting up the click listener
    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setOnInfoClickListener(final OnInfoClickListener itemClickListener) {
        this.infoClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //Click Listener Interface
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface OnInfoClickListener {
        public void onInfoClick(View view, int position);
    }

    class HistoryViewHolder extends RecyclerEmptyView.ViewHolder implements View.OnClickListener {
        TextView timeStamp, string;
        ImageView info;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            timeStamp = (TextView) itemView.findViewById(R.id.history_recyclerview_row_timestamp);
            string = (TextView) itemView.findViewById(R.id.history_recyclerview_row_string);
            info = (ImageView) itemView.findViewById(R.id.history_recyclerview_row_info);
            info.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.history_recyclerview_row_info) {
                infoClickListener.onInfoClick(v, getAdapterPosition());
            } else {
                clickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
