package com.wen.android.mtabuscomparison;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.wen.android.mtabuscomparison.Fragment.AboutFragment;
import com.wen.android.mtabuscomparison.Fragment.ComparisonFragment;
import com.wen.android.mtabuscomparison.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private EditText mBusStopCode;
    BottomNavigationView bottomNavigationView;

    private ViewPager viewPager;

    //Fragment
    SearchFragment searchFragment;
    ComparisonFragment comparisonFragment;
    AboutFragment aboutFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing search fragment component
        mBusStopCode = (EditText)findViewById(R.id.bus_stop_code);


        //Initializing vewPage
        viewPager = (ViewPager) findViewById(R.id.viewpaper);

        //Initializing bottomNavigationView
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation) ;
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.search_call:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.comparison_call:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.about_call:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else{
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        searchFragment = new SearchFragment();
        comparisonFragment = new ComparisonFragment();
        aboutFragment = new AboutFragment();
        adapter.addFragment(searchFragment);
        adapter.addFragment(comparisonFragment);
        adapter.addFragment(aboutFragment);
        viewPager.setAdapter(adapter);
    }



}
