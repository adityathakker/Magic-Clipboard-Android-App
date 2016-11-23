package com.adityathakker.magicclipboard.ui.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.adityathakker.magicclipboard.R;
import com.adityathakker.magicclipboard.async.MoveDatabaseAsync;
import com.adityathakker.magicclipboard.database.DatabaseHelper;
import com.adityathakker.magicclipboard.interfaces.IMoveDatabaseAsync;
import com.adityathakker.magicclipboard.models.InstalledApp;
import com.adityathakker.magicclipboard.ui.fragments.HistoryFragment;
import com.adityathakker.magicclipboard.ui.fragments.HomeFragment;
import com.adityathakker.magicclipboard.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        databaseHelper = new DatabaseHelper(this);

        String dbPath = Constants.DB.DB_PATH + Constants.DB.DB_NAME;
        File databaseFile = new File(dbPath);
        Log.v(TAG, "onCreate: Dictionary Path => " + databaseFile.getAbsolutePath());
        if (!databaseFile.exists()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Setting Up...");
            progressDialog.show();
            Log.d(TAG, "onCreate: Database Does Not Exists");
            MoveDatabaseAsync moveDatabaseAsync = new MoveDatabaseAsync(this, new IMoveDatabaseAsync() {
                @Override
                public void onCopyComplete() {
                    setupHomeActivity();
                    progressDialog.dismiss();
                }
            });
            moveDatabaseAsync.execute();

        } else {
            Log.d(TAG, "onCreate: Database Already Exists");
            setupHomeActivity();
        }
    }

    private void setupHomeActivity() {
        doFirstTimeStuff();

        viewPager = (ViewPager) findViewById(R.id.content_home_view_pager);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setPageMargin(16);
        viewPager.setPageMarginDrawable(android.R.color.transparent);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new HistoryFragment(), "History");
//        adapter.addFragment(new DictionaryFragment(), "Dictionary");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    private void doFirstTimeStuff() {
        if (sharedPreferences.getBoolean(Constants.SharedPrefs.IS_FIRST_TIME, true)) {
            Log.d(TAG, "doFirstTimeStuff: First Time");
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Setting Up...");
            progressDialog.show();
            //Do Stuff

            List<InstalledApp> installedAppList = getLocalAppsListForSent();
            databaseHelper.addAllLocalAppsForSent(installedAppList);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SharedPrefs.IS_FIRST_TIME, false);

            //Actions Count
            int totalActions = installedAppList.size() + Constants.Others.BUILT_IN_ACTIONS_COUNT;
            editor.putInt(Constants.SharedPrefs.TOTAL_AVAILABLE_ACTIONS,totalActions);
            editor.putInt(Constants.SharedPrefs.TOTAL_USED_ACTIONS,0);

            //Default Values
            editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true);
            editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true);
            editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true);
            editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true);
            editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true);
            editor.putBoolean(Constants.SharedPrefs.BUILT_IN_ACTIONS_FAV, true);
            editor.putBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_SAVING_ENABLED,true);
            editor.putBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_MEANING_ENABLED,true);
            editor.putBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_ENABLED,true);
            editor.putBoolean(Constants.SharedPrefs.PREF_POPUP_BOX_WORD_DISPLAY_COPIED_ENABLED,true);


            editor.commit();
            progressDialog.dismiss();
        } else {
            Log.d(TAG, "doFirstTimeStuff: Not First Time");
        }
    }

    private List<InstalledApp> getLocalAppsListForSent() {
        List<InstalledApp> installedAppsList = new ArrayList<>();

        // Intents with SEND action
        PackageManager packageManager = this.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sendIntent, 0);

        InstalledApp installedApps = null;
        ActivityInfo activityInfo = null;
        for (int j = 0; j < resolveInfoList.size(); j++) {
            ResolveInfo resolveInfo = resolveInfoList.get(j);
            String packageName = resolveInfo.activityInfo.packageName;
            String activityName = resolveInfo.activityInfo.name;
            try {
                activityInfo = packageManager.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
                installedApps = new InstalledApp(packageName, activityInfo.loadLabel(packageManager).toString(), activityName, activityInfo.loadIcon(packageManager), true);
                installedAppsList.add(installedApps);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return installedAppsList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
