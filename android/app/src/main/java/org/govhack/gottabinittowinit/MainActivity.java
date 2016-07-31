package org.govhack.gottabinittowinit;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.govhack.gottabinittowinit.fragments.DashboardFragment;
import org.govhack.gottabinittowinit.fragments.GalleryFragment;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Process GEOJson data and determine where address is located in the city
        try {
            InputStream is = getResources().openRawResource(R.raw.melbourne_garbage_collection);
            GeoJSONObject data = GeoJSON.parse(is);
            data.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        setupToolbar();
        setupViewPager();
        setupTabLayout();
        setupFab();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        if (mViewPager != null) {
            Adapter adapter = new Adapter(getSupportFragmentManager());
            adapter.addFragment(new DashboardFragment(), "Dashboard");
            adapter.addFragment(new GalleryFragment(), "Gallery");
            mViewPager.setAdapter(adapter);
        }
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AchievementsActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mPageTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment f, String title) {
            mFragments.add(f);
            mPageTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles.get(position);
        }
    }
}
