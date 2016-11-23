package com.adityathakker.magicclipboard.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.adapters.PopupWordActionsRA;
import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.database.DictionaryDatabaseHelper;
import com.adityathakker.magicclipboard.events.ClipboardLogAdditionEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogFavoriteChangeEvent;
import com.adityathakker.magicclipboard.ui.custom.RecyclerEmptyView;
import com.adityathakker.magicclipboard.utils.BusProvider;
import com.adityathakker.magicclipboard.utils.CommonTasks;
import com.adityathakker.magicclipboard.utils.Constants;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.util.List;

public class WordPopupActivity extends AppCompatActivity {
    private static final String TAG = WordPopupActivity.class.getSimpleName();
    private RecyclerEmptyView actionsRecyclerEmptyView;
    private RelativeLayout meaningLayout;
    private String copiedString;
    private DatabaseHelper databaseHelper;
    private DictionaryDatabaseHelper dictionaryDatabaseHelper;
    private SharedPreferences sharedPreferences;
    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_word_popup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        copiedString = getIntent().getStringExtra("copiedString");
        databaseHelper = new DatabaseHelper(this);
        dictionaryDatabaseHelper = new DictionaryDatabaseHelper(this);
        bus = BusProvider.getInstance();
        bus.register(this);

        if(sharedPreferences.getBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_SAVING_ENABLED,true)){
            databaseHelper.addClipboardLog(copiedString);
            bus.post(new ClipboardLogAdditionEvent(databaseHelper.getLatestClipboardLog()));
            Log.d(TAG, "onCreate: Saved in History");
        }else{
            Log.d(TAG, "onCreate: Saving is Disabled");
        }

        actionsRecyclerEmptyView = (RecyclerEmptyView) findViewById(R.id.activity_word_popup_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        actionsRecyclerEmptyView.setLayoutManager(linearLayoutManager);

        List<Object> actionsList = CommonTasks.getEnabledActionsWordList(this);

        PopupWordActionsRA popupWordActionsRA = new PopupWordActionsRA(this, actionsList, copiedString, bus);
        actionsRecyclerEmptyView.setAdapter(popupWordActionsRA);

        meaningLayout = (RelativeLayout) findViewById(R.id.activity_popup_meaning_layout);
        if(sharedPreferences.getBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_MEANING_ENABLED,true)){

            String meaning = dictionaryDatabaseHelper.getMeaning(copiedString.toLowerCase());
            if(meaning != null && !meaning.trim().equals("")){
                TextView meaningSupport = (TextView) findViewById(R.id.activity_popup_support_meaning);
                meaningSupport.setText("Meaning Of The Word \"" + copiedString + "\"");
                meaningLayout.setVisibility(View.VISIBLE);
                WebView meaningTextView = (WebView) findViewById(R.id.activity_popup_webview_meaning);
                meaningTextView.loadData(meaning, "text/html; charset=UTF-8", null);
            }else{
                meaningLayout.setVisibility(View.GONE);
                Log.d(TAG, "onCreate: No Meaning Available");
                Toast.makeText(WordPopupActivity.this, "No Meaning Defined For Word \"" + copiedString + "\"", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG, "onCreate: Meaning Box is Disabled");
            meaningLayout.setVisibility(View.GONE);
        }
    }

    @Produce
    public ClipboardLogAdditionEvent additionEvent(){
        return null;
    }

    @Produce
    public ClipboardLogFavoriteChangeEvent favChangeEvent(){
        return null;
    }


    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }
}
