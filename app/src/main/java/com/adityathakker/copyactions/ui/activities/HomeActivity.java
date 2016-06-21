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

import com.adityathakker.copyactions.R;
import com.adityathakker.copyactions.ui.fragments.HistoryFragment;
import com.adityathakker.copyactions.ui.fragments.HomeFragment;

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
        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(AppConst.SharedPrefs.IS_FIRST_TIME, true)) {
            Log.v(TAG, "onCreate: App Opened For The First Time");
            try {
                File dictionaryFile = this.getDatabasePath("copy_actions.db");
                Log.v(TAG, "onCreate: Dictionary Path => " + dictionaryFile.getAbsolutePath());
                if (!dictionaryFile.exists()) {
                    Log.v(TAG, "onCreate: Dictionary File Does Not Exist");

                    SQLiteDatabase checkDB = this.openOrCreateDatabase("copy_actions", Context.MODE_PRIVATE, null);
                    if (checkDB != null) {
                        checkDB.close();
                    }

                    InputStream inputStream = getAssets().open("copy_actions.db");
                    OutputStream fileOutputStream = new FileOutputStream(dictionaryFile);
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
