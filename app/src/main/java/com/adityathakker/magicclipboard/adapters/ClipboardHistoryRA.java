package com.adityathakker.magicclipboard.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityathakker.magicclipboard.models.ClipboardLog;
import com.adityathakker.magicclipboard.ui.custom.RecyclerEmptyView;
import com.adityathakker.magicclipboard.utils.TimeAgo;
import com.adityathakker.magicclipboard.R;

import java.util.Date;
import java.util.List;

/**
 * Created by adityajthakker on 15/6/16.
 */

public class ClipboardHistoryRA extends RecyclerEmptyView.Adapter<ClipboardHistoryRA.ClipboardLogViewHolder> {
    public static final String TAG = ClipboardHistoryRA.class.getSimpleName();
    private OnRowClickListener rowClickListener;
    private OnInfoClickListener infoClickListener;
    private List<ClipboardLog> clipboardLogList;
    private Date currentTime;

    public ClipboardHistoryRA(List<ClipboardLog> clipboardLogList) {
        this.clipboardLogList = clipboardLogList;
        currentTime = new Date();
    }

//    public List<ClipboardLog> getClipboardLogList() {
//        return clipboardLogList;
//    }

    public void setClipboardLogList(List<ClipboardLog> clipboardLogList) {
        this.clipboardLogList = clipboardLogList;
    }

//    public void addCopyRecordToList(ClipboardLog clipboardLog, int index) {
//        clipboardLogList.add(index, clipboardLog);
//    }


    @Override
    public ClipboardLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_clipboard_log_recyclerview_row, parent, false);
        ClipboardLogViewHolder viewHolder = new ClipboardLogViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ClipboardLogViewHolder holder, int position) {
        ClipboardLog tempClipboardLog = clipboardLogList.get(position);
        holder.timeStamp.setText(TimeAgo.toDuration(currentTime.getTime() - tempClipboardLog.getTimestamp().getTime()));
        holder.copiedText.setText(tempClipboardLog.getClip());
        Log.v(TAG, "onBindViewHolder: ClipboardLog => " + tempClipboardLog.toString());
    }

    @Override
    public int getItemCount() {
        return clipboardLogList != null? clipboardLogList.size():0;
    }

    //Setting up the click listener
    public void setOnRowClickListener(final OnRowClickListener rowClickListener) {
        this.rowClickListener = rowClickListener;
    }

    public void setOnInfoClickListener(final OnInfoClickListener itemClickListener) {
        this.infoClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //Click Listener Interface
    public interface OnRowClickListener {
        public void onItemClick(View view, int position);
    }

    public interface OnInfoClickListener {
        public void onInfoClick(View view, int position);
    }

    class ClipboardLogViewHolder extends RecyclerEmptyView.ViewHolder implements View.OnClickListener {
        TextView timeStamp, copiedText;
        ImageView infoIcon;

        ClipboardLogViewHolder(View itemView) {
            super(itemView);
            timeStamp = (TextView) itemView.findViewById(R.id.history_recyclerview_row_timestamp);
            copiedText = (TextView) itemView.findViewById(R.id.history_recyclerview_row_string);
            infoIcon = (ImageView) itemView.findViewById(R.id.history_recyclerview_row_info);

            infoIcon.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.history_recyclerview_row_info) {
                infoClickListener.onInfoClick(v, getAdapterPosition());
            } else {
                rowClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
