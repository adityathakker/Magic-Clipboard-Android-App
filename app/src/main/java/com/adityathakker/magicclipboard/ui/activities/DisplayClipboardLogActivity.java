package com.adityathakker.magicclipboard.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.events.ClipboardLogDeletionEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogFavoriteChangeEvent;
import com.adityathakker.magicclipboard.models.ClipboardLog;
import com.adityathakker.magicclipboard.utils.BusProvider;
import com.adityathakker.magicclipboard.utils.Constants;
import com.adityathakker.magicclipboard.utils.TimeAgo;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DisplayClipboardLogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DisplayClipboardLogActivity.class.getSimpleName();
    private TextView share, delete, favorite, timeStamp, copyText;
    private ClipboardLog clipboardLog;
    private DatabaseHelper databaseHelper;
    private int position;
    private Bus bus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_clipboard_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Copied Text");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bus = BusProvider.getInstance();

        Intent intent = getIntent();
        long id = Long.parseLong(intent.getStringExtra("id"));
        String string = intent.getStringExtra("string");
        String timeStampString = intent.getStringExtra("timeStamp");
        String isFav = intent.getStringExtra("isFav");
        position = Integer.parseInt(intent.getStringExtra("position"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            clipboardLog = new ClipboardLog(id, string, simpleDateFormat.parse(timeStampString), Boolean.parseBoolean(isFav));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onCreate: ClipboardLog For Display => " + clipboardLog.toString());

        databaseHelper = new DatabaseHelper(this);

        share = (TextView) findViewById(R.id.content_display_copy_button_share);
        delete = (TextView) findViewById(R.id.content_display_copy_button_delete);
        favorite = (TextView) findViewById(R.id.content_display_copy_button_fav);
        timeStamp = (TextView) findViewById(R.id.content_display_copy_timestamp);
        delete.setOnClickListener(this);
        share.setOnClickListener(this);
        favorite.setOnClickListener(this);

        java.util.Date currentTime = new java.util.Date();
        timeStamp.setText(TimeAgo.toDuration(currentTime.getTime() - clipboardLog.getTimestamp().getTime()));

        copyText = (TextView) findViewById(R.id.content_display_copy_string);
        copyText.setText(clipboardLog.getClip());

        if (clipboardLog.getFav()) {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp, 0, 0, 0);
        } else {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp, 0, 0, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_display_copy_button_delete:
                //Clicked on Delete button
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Delete Record");
                alertDialogBuilder.setMessage("Do you want to delete the copy record?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.removedClipboardLog(clipboardLog.getId());
                        Toast.makeText(DisplayClipboardLogActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                        bus.post(new ClipboardLogDeletionEvent(position));
                        finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.create().show();

                break;
            case R.id.content_display_copy_button_share:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shareIntent.putExtra(Intent.EXTRA_TEXT, clipboardLog.getClip());
                startActivity(Intent.createChooser(shareIntent, "Share: " + clipboardLog.getClip()));
                break;
            case R.id.content_display_copy_button_fav:
                if (clipboardLog.getFav()) {
                    clipboardLog.setFav(false);
                    databaseHelper.removeFromFavorite(clipboardLog);
                    favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp, 0, 0, 0);
                    Toast.makeText(DisplayClipboardLogActivity.this, "Removed From Favorites", Toast.LENGTH_SHORT).show();

                    bus.post(new ClipboardLogFavoriteChangeEvent(position, false, Constants.Codes.SOURCE_DISPLAY_LOG_ACTIVTY));
                } else {
                    clipboardLog.setFav(true);
                    databaseHelper.addToFavorite(clipboardLog);
                    favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp, 0, 0, 0);
                    Toast.makeText(DisplayClipboardLogActivity.this, "Added To Favorites", Toast.LENGTH_SHORT).show();

                    bus.post(new ClipboardLogFavoriteChangeEvent(position, true, Constants.Codes.SOURCE_DISPLAY_LOG_ACTIVTY));
                }
                break;
        }
    }

    @Produce
    public ClipboardLogDeletionEvent produceDeletionEvent(){
        return null;
    }

    @Produce
    public ClipboardLogFavoriteChangeEvent produceFavoriteEvent(){
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
