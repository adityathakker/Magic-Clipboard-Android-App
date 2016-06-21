package com.adityathakker.copyactions.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.models.CopyRecord;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;
import com.adityathakker.copyactions.utils.TimeAgo;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DisplayCopyActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DisplayCopyActivity.class.getSimpleName();
    private TextView share, delete, favorite, timeStamp, copyText;
    private CopyRecord copyRecord;
    private DatabaseHelper databaseHelper;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_copy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Copied Text");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        long id = Long.parseLong(intent.getStringExtra("id"));
        String string = intent.getStringExtra("string");
        String timeStampString = intent.getStringExtra("timeStamp");
        String isFav = intent.getStringExtra("isFav");
        position = Integer.parseInt(intent.getStringExtra("position"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            copyRecord = new CopyRecord(id, string, simpleDateFormat.parse(timeStampString), Boolean.parseBoolean(isFav));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onCreate: CopyRecord For Display => " + copyRecord.toString());

        databaseHelper = new DatabaseHelper(this);

        share = (TextView) findViewById(R.id.content_display_copy_button_share);
        share.setOnClickListener(this);
        delete = (TextView) findViewById(R.id.content_display_copy_button_delete);
        delete.setOnClickListener(this);
        favorite = (TextView) findViewById(R.id.content_display_copy_button_fav);
        favorite.setOnClickListener(this);
        timeStamp = (TextView) findViewById(R.id.content_display_copy_timestamp);

        java.util.Date currentTime = new java.util.Date();
        timeStamp.setText(TimeAgo.toDuration(currentTime.getTime() - copyRecord.getTimestamp().getTime()));

        copyText = (TextView) findViewById(R.id.content_display_copy_string);
        copyText.setText(copyRecord.getString());

        if (copyRecord.getFav()) {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp, 0, 0, 0);
        } else {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp, 0, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_display_copy_button_delete:
                databaseHelper.removedCopyRecord(copyRecord.getId());
                Toast.makeText(DisplayCopyActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                HistoryFragment.historyRecyclerAdapter.removeCopyRecordFromList(position);
                HistoryFragment.historyRecyclerAdapter.notifyItemRemoved(position);
                HistoryFragment.recyclerView.dataChanged();
                finish();
                break;
            case R.id.content_display_copy_button_share:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, copyRecord.getString());
                startActivity(Intent.createChooser(shareIntent, "Share: " + copyRecord.getString()));
                break;
            case R.id.content_display_copy_button_fav:
                if (copyRecord.getFav()) {
                    copyRecord.setFav(false);
                    databaseHelper.removeFromFavorite(copyRecord);
                    favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp, 0, 0, 0);
                    Toast.makeText(DisplayCopyActivity.this, "Removed From Favorites", Toast.LENGTH_SHORT).show();
                    HistoryFragment.historyRecyclerAdapter.replaceCopyRecordAt(copyRecord, position);
                    HistoryFragment.historyRecyclerAdapter.notifyItemChanged(position);
                } else {
                    copyRecord.setFav(true);
                    databaseHelper.markAsFavorite(copyRecord);
                    favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp, 0, 0, 0);
                    Toast.makeText(DisplayCopyActivity.this, "Added To Favorites", Toast.LENGTH_SHORT).show();
                    HistoryFragment.historyRecyclerAdapter.replaceCopyRecordAt(copyRecord, position);
                    HistoryFragment.historyRecyclerAdapter.notifyItemChanged(position);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
        finish();
    }
}
