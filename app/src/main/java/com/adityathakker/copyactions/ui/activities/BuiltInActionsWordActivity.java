package com.adityathakker.copyactions.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;

public class BuiltInActionsWordActivity extends AppCompatActivity {
    private SwitchCompat searchSwitch, shareSwitch, mapsSwitch, translateSwitch, speakSwitch;
    private SharedPreferences sharedPreferences;
    private RelativeLayout searchRelativeLayout, shareRelativeLayout, mapsRelativeLayout, translateRelativeLayout, speakRelativeLayout;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_built_in_actions_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        searchSwitch = (SwitchCompat) findViewById(R.id.content_built_in_actions_word_action_search_switch);
        shareSwitch = (SwitchCompat) findViewById(R.id.content_built_in_actions_word_action_share_switch);
        mapsSwitch = (SwitchCompat) findViewById(R.id.content_built_in_actions_word_action_maps_switch);
        translateSwitch = (SwitchCompat) findViewById(R.id.content_built_in_actions_word_action_translate_switch);
        speakSwitch = (SwitchCompat) findViewById(R.id.content_built_in_actions_word_action_speak_switch);


        searchSwitch.setChecked(sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true));
        shareSwitch.setChecked(sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true));
        mapsSwitch.setChecked(sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true));
        translateSwitch.setChecked(sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true));
        speakSwitch.setChecked(sharedPreferences.getBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true));

        searchRelativeLayout = (RelativeLayout) findViewById(R.id.content_built_in_actions_word_relative_layout_search);
        shareRelativeLayout = (RelativeLayout) findViewById(R.id.content_built_in_actions_word_relative_layout_share);
        mapsRelativeLayout = (RelativeLayout) findViewById(R.id.content_built_in_actions_word_relative_layout_maps);
        translateRelativeLayout = (RelativeLayout) findViewById(R.id.content_built_in_actions_word_relative_layout_translate);
        speakRelativeLayout = (RelativeLayout) findViewById(R.id.content_built_in_actions_word_relative_layout_speak);

        editor = sharedPreferences.edit();


        searchRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, !searchSwitch.isChecked());
                editor.commit();
                searchSwitch.setChecked(!searchSwitch.isChecked());
            }
        });

        shareRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SHARE, !shareSwitch.isChecked());
                editor.commit();
                shareSwitch.setChecked(!shareSwitch.isChecked());
            }
        });

        mapsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_MAPS, !mapsSwitch.isChecked());
                editor.commit();
                mapsSwitch.setChecked(!mapsSwitch.isChecked());
            }
        });

        translateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, !translateSwitch.isChecked());
                editor.commit();
                translateSwitch.setChecked(!translateSwitch.isChecked());
            }
        });

        speakRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, !speakSwitch.isChecked());
                editor.commit();
                speakSwitch.setChecked(!speakSwitch.isChecked());
            }
        });

    }

}
