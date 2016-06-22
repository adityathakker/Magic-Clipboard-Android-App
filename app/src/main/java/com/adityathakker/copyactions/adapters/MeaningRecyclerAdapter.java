package com.adityathakker.copyactions.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adityathakker.copyactions.R;

import java.util.List;

/**
 * Created by adityajthakker on 22/6/16.
 */

public class MeaningRecyclerAdapter extends RecyclerView.Adapter<MeaningRecyclerAdapter.MeaningRecyclerViewHolder> {
    public static final String TAG = MeaningRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<String> meaningsList;

    public MeaningRecyclerAdapter(Context context, List<String> meaningsList) {
        this.context = context;
        this.meaningsList = meaningsList;
    }

    @Override
    public MeaningRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meaning_recyclerview_row, parent, false);
        MeaningRecyclerViewHolder viewHolder = new MeaningRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeaningRecyclerViewHolder holder, int position) {
        holder.meaning.setText((position + 1) + ". " + meaningsList.get(position));
    }

    @Override
    public int getItemCount() {
        return meaningsList.size();
    }

    class MeaningRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView meaning;

        public MeaningRecyclerViewHolder(View itemView) {
            super(itemView);
            meaning = (TextView) itemView.findViewById(R.id.meaning_recyclerview_row_textview);
        }


    }

}
