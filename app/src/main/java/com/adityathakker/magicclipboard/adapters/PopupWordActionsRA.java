package com.adityathakker.magicclipboard.adapters;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.events.ActionUsedEvent;
import com.adityathakker.magicclipboard.events.ClipboardLogFavoriteChangeEvent;
import com.adityathakker.magicclipboard.models.InstalledApp;
import com.adityathakker.magicclipboard.ui.activities.SettingWordActionsActivity;
import com.adityathakker.magicclipboard.utils.Constants;
import com.squareup.otto.Bus;

import java.util.List;

public class PopupWordActionsRA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = PopupWordActionsRA.class.getSimpleName();
    private Context context;
    private List<Object> localAppList;
    private String copiedString;
    private int INSTALLED_APP_ACTIONS = 1;
    private int BUILT_IN_ACTION = 2;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseHelper databaseHelper;
    private Bus bus;

    public PopupWordActionsRA(Context context, List<Object> localAppList, String copiedString, Bus bus) {
        this.context = context;
        this.localAppList = localAppList;
        this.copiedString = copiedString;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        this.bus = bus;
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BUILT_IN_ACTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_popup_actions_row, parent, false);
            WordPopupBuiltInViewHolder viewHolder = new WordPopupBuiltInViewHolder(view);
            return viewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_popup_actions_row, parent, false);
            WordPopupInstalledAppViewHolder viewHolder = new WordPopupInstalledAppViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == BUILT_IN_ACTION) {
            String actionType = (String) localAppList.get(position);
            WordPopupBuiltInViewHolder wordPopupBuiltInViewHolder = (WordPopupBuiltInViewHolder) holder;
            configureBuiltInAction(wordPopupBuiltInViewHolder, actionType);
        } else {
            WordPopupInstalledAppViewHolder installedAppViewHolder = (WordPopupInstalledAppViewHolder) holder;
            InstalledApp installedApp = (InstalledApp) localAppList.get(position);
            configureInstalledAppAction(installedAppViewHolder, installedApp);
        }
    }

    private void configureBuiltInAction(WordPopupBuiltInViewHolder wordPopupBuiltInViewHolder, String actionType) {
        switch (actionType) {
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH:
                wordPopupBuiltInViewHolder.launcherText.setText("Search");
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_black_48dp));
                wordPopupBuiltInViewHolder.launcherIcon.setColorFilter(ContextCompat.getColor(context,R.color.grey));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        incrementUsedActionsPref();
                        bus.post(new ActionUsedEvent());

                        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY, copiedString); // query contains search copiedText
                        context.startActivity(intent);

                    }
                });
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE:
                wordPopupBuiltInViewHolder.launcherText.setText("Share");
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_share_black_48dp));
                wordPopupBuiltInViewHolder.launcherIcon.setColorFilter(ContextCompat.getColor(context,R.color.green));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        incrementUsedActionsPref();
                        bus.post(new ActionUsedEvent());

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, copiedString);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                });
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS:
                wordPopupBuiltInViewHolder.launcherText.setText("Maps");
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_near_me_black_48dp));
                wordPopupBuiltInViewHolder.launcherIcon.setColorFilter(ContextCompat.getColor(context,R.color.blue));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        incrementUsedActionsPref();
                        bus.post(new ActionUsedEvent());

                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + copiedString);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        } else {
                            String url = "https://www.google.co.in/maps/search/" + copiedString;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE:
                wordPopupBuiltInViewHolder.launcherText.setText("Translate");
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_translate_black_48dp));
                wordPopupBuiltInViewHolder.launcherIcon.setColorFilter(ContextCompat.getColor(context,R.color.grey));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        incrementUsedActionsPref();
                        bus.post(new ActionUsedEvent());

                        String url = "https://translate.google.com/m/translate#auto/en/" + copiedString;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    }
                });
                break;
            case Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV:
                wordPopupBuiltInViewHolder.launcherText.setText("Favorite");
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_48dp));
                wordPopupBuiltInViewHolder.launcherIcon.setColorFilter(ContextCompat.getColor(context,R.color.red));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        incrementUsedActionsPref();
                        bus.post(new ActionUsedEvent());

                        databaseHelper.addToFavorite(databaseHelper.getLatestClipboardLog());
                        Toast.makeText(context, "Added to Favorite", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick: Favorite Clicked");
                        bus.post(new ClipboardLogFavoriteChangeEvent(-1, true, Constants.Codes.SOURCE_WORD_POPUP));
                    }
                });
                break;
            case Constants.Others.EDIT_ACTIONS_WORD:
                wordPopupBuiltInViewHolder.launcherText.setText("Edit Actions");
                Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_settings_black_48dp);
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(icon);
                wordPopupBuiltInViewHolder.launcherIcon.setColorFilter(Color.GRAY);
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SettingWordActionsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                break;
        }
    }

    private void configureInstalledAppAction(WordPopupInstalledAppViewHolder installedAppViewHolder, final InstalledApp installedApp) {
        installedAppViewHolder.launcherText.setText(installedApp.getLocalName());
        if (installedApp.getIcon() != null) {
            installedAppViewHolder.launcherIcon.setImageDrawable(installedApp.getIcon());
            installedAppViewHolder.launcherIcon.setColorFilter(Color.TRANSPARENT);
        }
        installedAppViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementUsedActionsPref();
                bus.post(new ActionUsedEvent());

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(installedApp.getPackageName(), installedApp.getActivityName()));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, copiedString);
                context.startActivity(intent);
            }
        });
    }

    //Todo: Speak Button not working

    private void incrementUsedActionsPref() {
        editor.putInt(Constants.SharedPrefs.TOTAL_USED_ACTIONS,sharedPreferences.getInt(Constants.SharedPrefs.TOTAL_USED_ACTIONS,0) + 1);
        editor.apply();
    }

    @Override
    public int getItemViewType(int position) {
        if (localAppList.get(position) instanceof InstalledApp) {
            return INSTALLED_APP_ACTIONS;
        } else if (localAppList.get(position) instanceof String) {
            return BUILT_IN_ACTION;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return localAppList.size();
    }

    private class WordPopupInstalledAppViewHolder extends RecyclerView.ViewHolder {
        TextView launcherText;
        ImageView launcherIcon;

        WordPopupInstalledAppViewHolder(View itemView) {
            super(itemView);
            launcherText = (TextView) itemView.findViewById(R.id.launcherText);
            launcherIcon = (ImageView) itemView.findViewById(R.id.launcherIcon);
        }

    }

    private class WordPopupBuiltInViewHolder extends RecyclerView.ViewHolder {
        TextView launcherText;
        ImageView launcherIcon;

        WordPopupBuiltInViewHolder(View itemView) {
            super(itemView);
            launcherText = (TextView) itemView.findViewById(R.id.launcherText);
            launcherIcon = (ImageView) itemView.findViewById(R.id.launcherIcon);
        }
    }
}
