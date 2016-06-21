package com.adityathakker.copyactions.ui.activities;

import android.content.SharedPreferences;
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
import com.adityathakker.copyactions.async.CopyAsync;
import com.adityathakker.copyactions.interfaces.ICopyAsyncCallback;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
