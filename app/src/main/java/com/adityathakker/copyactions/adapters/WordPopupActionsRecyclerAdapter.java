package com.adityathakker.copyactions.adapters;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.models.LocalApp;

import java.util.List;

public class WordPopupActionsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = WordPopupActionsRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<Object> localAppList;
    private String copiedString;
    private int LOCAL_ACTIONS = 1;
    private int BUILT_IN_ACTION = 2;

    public WordPopupActionsRecyclerAdapter(Context context, List<Object> localAppList, String copiedString) {
        this.context = context;
        this.localAppList = localAppList;
        this.copiedString = copiedString;
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
            LocalApp localApp = (LocalApp) localAppList.get(position);
            configureInstalledAppAction(installedAppViewHolder, localApp);
        }

    }

    private void configureBuiltInAction(WordPopupBuiltInViewHolder wordPopupBuiltInViewHolder, String actionType) {
        switch (actionType) {
            case AppConst.SharedPrefs.BUILT_IN_ACTIONS_SEARCH:
                wordPopupBuiltInViewHolder.launcherText.setText("Search");
                wordPopupBuiltInViewHolder.launcherIcon.setAlpha(0.5f);
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_black_48dp));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY, copiedString); // query contains search string
                        context.startActivity(intent);
                    }
                });
                break;
            case AppConst.SharedPrefs.BUILT_IN_ACTIONS_SHARE:
                wordPopupBuiltInViewHolder.launcherText.setText("Share");
                wordPopupBuiltInViewHolder.launcherIcon.setAlpha(0.5f);
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_share_black_48dp));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, copiedString);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                });
                break;
            case AppConst.SharedPrefs.BUILT_IN_ACTIONS_MAPS:
                wordPopupBuiltInViewHolder.launcherText.setText("Maps");
                wordPopupBuiltInViewHolder.launcherIcon.setAlpha(0.5f);
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_near_me_black_48dp));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
            case AppConst.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE:
                wordPopupBuiltInViewHolder.launcherText.setText("Translate");
                wordPopupBuiltInViewHolder.launcherIcon.setAlpha(0.5f);
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_translate_black_48dp));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://translate.google.com/m/translate#auto/en/" + copiedString;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    }
                });
                break;
            case AppConst.SharedPrefs.BUILT_IN_ACTIONS_SPEAK:
                wordPopupBuiltInViewHolder.launcherText.setText("Speak");
                wordPopupBuiltInViewHolder.launcherIcon.setAlpha(0.5f);
                wordPopupBuiltInViewHolder.launcherIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_volume_up_black_48dp));
                wordPopupBuiltInViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Speak Clicked");
                    }
                });
                break;
        }
    }

    private void configureInstalledAppAction(WordPopupInstalledAppViewHolder installedAppViewHolder, final LocalApp localApp) {
        installedAppViewHolder.launcherText.setText(localApp.getLocalName());
        if (localApp.getIcon() != null) {
            installedAppViewHolder.launcherIcon.setImageDrawable(localApp.getIcon());
        }
        installedAppViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(localApp.getPackageName(), localApp.getActivityName()));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, copiedString);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (localAppList.get(position) instanceof LocalApp) {
            return LOCAL_ACTIONS;
        } else if (localAppList.get(position) instanceof String) {
            return BUILT_IN_ACTION;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return localAppList.size();
    }

    class WordPopupInstalledAppViewHolder extends RecyclerView.ViewHolder {
        TextView launcherText;
        ImageView launcherIcon;

        public WordPopupInstalledAppViewHolder(View itemView) {
            super(itemView);
            launcherText = (TextView) itemView.findViewById(R.id.launcherText);
            launcherIcon = (ImageView) itemView.findViewById(R.id.launcherIcon);
        }

    }

    class WordPopupBuiltInViewHolder extends RecyclerView.ViewHolder {
        TextView launcherText;
        ImageView launcherIcon;

        public WordPopupBuiltInViewHolder(View itemView) {
            super(itemView);
            launcherText = (TextView) itemView.findViewById(R.id.launcherText);
            launcherIcon = (ImageView) itemView.findViewById(R.id.launcherIcon);
        }
    }
}
