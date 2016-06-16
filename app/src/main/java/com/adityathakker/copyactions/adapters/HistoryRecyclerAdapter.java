package com.adityathakker.copyactions.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.utils.TimeAgo;

import java.util.Date;
import java.util.List;

/**
 * Created by adityajthakker on 15/6/16.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {
    public static final String TAG = HistoryRecyclerAdapter.class.getSimpleName();
    private Context context;
    private OnItemClickListener clickListener;
    private List<CopyRecord> copyRecordList;
    private Date currentTime;

    public HistoryRecyclerAdapter(Context context, List<CopyRecord> copyRecordList) {
        this.context = context;
        this.copyRecordList = copyRecordList;
        currentTime = new Date();
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

    //Click Listener Interface
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView timeStamp, string;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            timeStamp = (TextView) itemView.findViewById(R.id.history_recyclerview_row_timestamp);
            string = (TextView) itemView.findViewById(R.id.history_recyclerview_row_string);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
