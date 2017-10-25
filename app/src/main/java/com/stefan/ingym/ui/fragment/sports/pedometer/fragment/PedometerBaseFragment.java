package com.stefan.ingym.ui.fragment.sports.pedometer.fragment;


import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.stefan.ingym.R;
import com.stefan.ingym.ui.activity.MainActivity;
import com.stefan.ingym.ui.fragment.sports.pedometer.activity.DistanceMeasurementActivity;
import com.stefan.ingym.ui.fragment.sports.pedometer.activity.PreferencesActivity;
import com.stefan.ingym.ui.fragment.sports.pedometer.activity.TrainingOverviewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedometerBaseFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    // delay to launch nav drawer item, to allow close animation to play
    static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    protected View view;
    // Helper
    private Handler mHandler;
    protected SharedPreferences mSharedPreferences;
    public PedometerBaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mHandler = new Handler();
    }


/*    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    protected int getNavigationDrawerID() {
        return 0;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        return goToNavigationItem(itemId);
    }

    protected boolean goToNavigationItem(final int itemId) {

        if (itemId == getNavigationDrawerID()) {
            // just close drawer because we are already in this activity
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        // delay transition so the drawer can close
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callDrawerItem(itemId);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        mDrawerLayout.closeDrawer(GravityCompat.START);

        selectNavigationItem(itemId);

        // fade out the active activity
        View mainContent = view.findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }
        return true;
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
            boolean b = itemId == mNavigationView.getMenu().getItem(i).getItemId();
            mNavigationView.getMenu().getItem(i).setChecked(b);
        }
    }

    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     *
     * @param intent
     */
    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(getContext());
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
//            finish();
        }
    }

    private void callDrawerItem(final int itemId) {

        Intent intent;

        switch (itemId) {
            case R.id.menu_home:
                //Fragment fragment = new MainFragment();

                //FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
            case R.id.menu_training:
                intent = new Intent(getContext(), TrainingOverviewActivity.class);
                createBackStack(intent);
                break;
            case R.id.menu_distance_measurement:
                intent = new Intent(getContext(), DistanceMeasurementActivity.class);
                createBackStack(intent);
                break;
//            case R.id.menu_about:
//                intent = new Intent(this, AboutActivity.class);
//                createBackStack(intent);
//                break;
//            case R.id.menu_help:
//                intent = new Intent(this, HelpActivity.class);
//                createBackStack(intent);
//                break;
            case R.id.menu_settings:
                intent = new Intent(getContext(), PreferencesActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            View mainContent = view.findViewById(R.id.main_content);
            if (mainContent != null && mainContent.getAlpha() != 1.0f) {
                mainContent.setAlpha(0);
                mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
            }

            // selectNavigationItem(getNavigationDrawerID());
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) view.findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        selectNavigationItem(getNavigationDrawerID());

        View mainContent = view.findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }


}
