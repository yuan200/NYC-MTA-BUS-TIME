//package com.wen.android.mtabuscomparison;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Build;
//import android.os.Bundle;
//import android.transition.Explode;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.Window;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewpager.widget.ViewPager;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.wen.android.mtabuscomparison.feature.ad.FetchAdUnitUseCase;
//import com.wen.android.mtabuscomparison.screens.favorite.FavoriteFragment;
//import com.wen.android.mtabuscomparison.screens.stopmap.StopMapFragment;
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class MainActivity extends AppCompatActivity {
//
//    BottomNavigationView bottomNavigationView;
//
//    private static final int REQUEST_ERROR = 0;
//
//    private ViewPager viewPager;
//
//    //Fragment
//    StopMapFragment stopMapFragment;
//    FavoriteFragment comparisonFragment;
//    MenuItem prevMenuItem;
//    @Inject FetchAdUnitUseCase fetchAdUnitUseCase;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setExitTransition(new Explode());
//        }
//        setContentView(R.layout.activity_main);
//        fetchAdUnitUseCase.fetchAdUnit();
//
//        //Initializing vewPage
//        viewPager = (ViewPager) findViewById(R.id.view_pager);
//        //disable swipe page
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//
//        //Initializing bottomNavigationView
//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.mainFragment:
//                                viewPager.setCurrentItem(0);
//                                break;
//                            case R.id.favoriteFragment:
//                                viewPager.setCurrentItem(1);
//                                break;
//                        }
//                        return false;
//                    }
//                });
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (prevMenuItem != null) {
//                    prevMenuItem.setChecked(false);
//                } else {
//                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
//                }
//                bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        setupViewPager(viewPager);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
//
//        if (errorCode != ConnectionResult.SUCCESS) {
//            Dialog errorDialog = apiAvailability
//                    .getErrorDialog(this, errorCode, REQUEST_ERROR,
//                            new DialogInterface.OnCancelListener() {
//                                @Override
//                                public void onCancel(DialogInterface dialog) {
//                                    //leave if services are unavailable
//                                    finish();
//                                }
//                            });
//            errorDialog.show();
//        }
//    }
//
//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        stopMapFragment = new StopMapFragment();
//        comparisonFragment = new FavoriteFragment();
//        adapter.addFragment(stopMapFragment);
//        adapter.addFragment(comparisonFragment);
//        viewPager.setAdapter(adapter);
//    }
//
//}
