package com.wen.android.mtabuscomparison;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wen.android.mtabuscomparison.screens.about.AboutFragment;
import com.wen.android.mtabuscomparison.screens.favorite.FavoriteFragment;
import com.wen.android.mtabuscomparison.screens.stopmap.StopMapFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private static final int REQUEST_ERROR = 0;

    private ViewPager viewPager;

    //Fragment
    StopMapFragment stopMapFragment;
    FavoriteFragment comparisonFragment;
    AboutFragment aboutFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing vewPage
        viewPager = (ViewPager) findViewById(R.id.viewpaper);
        //disable swipe page
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Initializing bottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
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

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    //leave if services are unavailable
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        stopMapFragment = new StopMapFragment();
        comparisonFragment = new FavoriteFragment();
        aboutFragment = new AboutFragment();
        adapter.addFragment(stopMapFragment);
        adapter.addFragment(comparisonFragment);
        adapter.addFragment(aboutFragment);
        viewPager.setAdapter(adapter);
    }

}
