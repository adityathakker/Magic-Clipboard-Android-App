package com.adityathakker.copyactions.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.adityathakker.copyactions.AppConst;
import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;
import com.adityathakker.copyactions.ui.fragments.HomeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String dbPath = AppConst.DB.DB_PATH + AppConst.DB.DB_NAME;
        File databaseFile = new File(dbPath);
        Log.v(TAG, "onCreate: Dictionary Path => " + databaseFile.getAbsolutePath());
        if (!databaseFile.exists()) {
            try {
                SQLiteDatabase checkDB = this.openOrCreateDatabase(AppConst.DB.DB_NAME, Context.MODE_PRIVATE, null);
                if (checkDB != null) {
                    checkDB.close();
                }

                InputStream inputStream = getApplicationContext().getAssets().open(AppConst.DB.DB_NAME);
                OutputStream fileOutputStream = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                inputStream.close();
                fileOutputStream.close();
                Log.d(TAG, "onCreate: Dictionary File Has Been Copied");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onCreate: Error Occurred While Copying", e);
            }
        } else {
            Log.d(TAG, "onCreate: Database File Already Exists");
        }


        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.IS_FIRST_TIME, true)) {
            Log.v(TAG, "onCreate: App Opened For The First Time");
            try {
                File databaseFile = this.getDatabasePath("copy_actions.db");
                Log.v(TAG, "onCreate: Dictionary Path => " + databaseFile.getAbsolutePath());
                if (!databaseFile.exists()) {
                    Log.v(TAG, "onCreate: Dictionary File Does Not Exist");

                    SQLiteDatabase checkDB = this.openOrCreateDatabase("copy_actions", Context.MODE_PRIVATE, null);
                    if (checkDB != null) {
                        checkDB.close();
                    }

                    InputStream inputStream = getAssets().open("copy_actions.db");
                    OutputStream fileOutputStream = new FileOutputStream(databaseFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                    Log.v(TAG, "onCreate: Dictionary File Has Been Copied");
                } else {
                    Log.v(TAG, "onCreate: File Already Exists");
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(AppConst.SharedPrefs.IS_FIRST_TIME, false);
                editor.commit();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: FileNotFoundException Occurred", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: IOException Occurred", e);
            }
        }*/


        viewPager = (ViewPager) findViewById(R.id.content_home_view_pager);
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
