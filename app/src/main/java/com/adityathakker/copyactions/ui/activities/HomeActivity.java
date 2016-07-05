package com.adityathakker.copyactions.ui.activities;

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

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.async.CopyAsync;
import com.adityathakker.copyactions.database.DatabaseHelper;
import com.adityathakker.copyactions.interfaces.ICopyAsyncCallback;
import com.adityathakker.copyactions.models.LocalApp;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;
import com.adityathakker.copyactions.ui.fragments.HomeFragment;

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


        String dbPath = AppConst.DB.DB_PATH + AppConst.DB.DB_NAME;
        File databaseFile = new File(dbPath);
        Log.v(TAG, "doInBackground: Dictionary Path => " + databaseFile.getAbsolutePath());
        if (!databaseFile.exists()) {
            Log.d(TAG, "onCreate: Database Does Not Exists");
            CopyAsync copyAsync = new CopyAsync(this, new ICopyAsyncCallback() {
                @Override
                public void onCopyComplete() {
                    setupHomeActivity();
                }
            });
            copyAsync.execute();

        } else {
            Log.d(TAG, "onCreate: Database Already Exists");
            setupHomeActivity();
        }
    }

    private void setupHomeActivity() {
        doFirstTimeStuff();

        viewPager = (ViewPager) findViewById(R.id.content_home_view_pager);
        viewPager.setPageMargin(16);
        viewPager.setPageMarginDrawable(android.R.color.transparent);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new HistoryFragment(), "History");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    private void doFirstTimeStuff() {
        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.IS_FIRST_TIME, true)) {
            Log.d(TAG, "doFirstTimeStuff: First Time");
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Setting Up...");
            progressDialog.show();
            //Do Stuff

            List<LocalApp> localAppList = getLocalAppsListForSent();
            databaseHelper.storeAllLocalAppsForSent(localAppList);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(AppConst.SharedPrefs.IS_FIRST_TIME, false);


            editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SEARCH, true);
            editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SHARE, true);
            editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_MAPS, true);
            editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_TRANSLATE, true);
            editor.putBoolean(AppConst.SharedPrefs.BUILT_IN_ACTIONS_SPEAK, true);

            editor.commit();
            progressDialog.dismiss();
        } else {
            Log.d(TAG, "doFirstTimeStuff: Not First Time");
        }
    }

    private List<LocalApp> getLocalAppsListForSent() {
        List<LocalApp> localAppsList = new ArrayList<>();

        // Intents with SEND action
        PackageManager packageManager = this.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(sendIntent, 0);

        LocalApp localApps = null;
        ActivityInfo activityInfo = null;
        for (int j = 0; j < resolveInfoList.size(); j++) {
            ResolveInfo resolveInfo = resolveInfoList.get(j);
            String packageName = resolveInfo.activityInfo.packageName;
            String activityName = resolveInfo.activityInfo.name;
            try {
                activityInfo = packageManager.getActivityInfo(new ComponentName(packageName, activityName), PackageManager.GET_META_DATA);
                localApps = new LocalApp(packageName, activityInfo.loadLabel(packageManager).toString(), activityName, activityInfo.loadIcon(packageManager), true);
                localAppsList.add(localApps);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return localAppsList;
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
