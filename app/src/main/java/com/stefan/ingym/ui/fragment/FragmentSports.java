package com.stefan.ingym.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.DailyReportFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.MainFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.MonthlyReportFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.PedometerBaseFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.WeeklyReportFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.utils.StepDetectionServiceHelper;

/**
 * @ClassName: FragmentSports
 * @Description: 应用程序“运动”模块
 * @Author Stefan
 * @Date 2017/9/22 15:43
 */

public class FragmentSports extends PedometerBaseFragment implements DailyReportFragment.OnFragmentInteractionListener, WeeklyReportFragment.OnFragmentInteractionListener, MonthlyReportFragment.OnFragmentInteractionListener, View.OnClickListener {
    private static final String TAG = FragmentSports.class.getSimpleName();
    private View continueBtn;
    private View pauseBtn;
    @Override
    public void onClick(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        switch(view.getId()){
            case R.id.menu_pause_step_detection:
                Log.d(TAG, "暂停计数");
                editor.putBoolean(getString(R.string.pref_step_counter_enabled), false);
                editor.apply();
                StepDetectionServiceHelper.stopAllIfNotRequired(getActivity().getApplicationContext());
                break;
            case R.id.menu_continue_step_detection:
                Log.d(TAG, "开始计数");
                editor.putBoolean(getString(R.string.pref_step_counter_enabled), true);
                editor.apply();
                StepDetectionServiceHelper.startAllIfEnabled(true, getActivity().getApplicationContext());
                break;
            default:
        }
        setPauseContinueMenuItemVisibility();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sports, null);

        // init preferences
        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_notification, false);

        // Load first view
//        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new MainFragment(), "MainFragment");
        fragmentTransaction.commit();

        // Start step detection if enabled and not yet started
        StepDetectionServiceHelper.startAllIfEnabled(getContext());
        //Log.i(LOG_TAG, "MainActivity initialized");

        ((TextView)view.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.sports_title));

        continueBtn = view.findViewById(R.id.menu_continue_step_detection);
        continueBtn.setOnClickListener(this);
        pauseBtn = view.findViewById(R.id.menu_pause_step_detection);
        pauseBtn.setOnClickListener(this);

//        setHasOptionsMenu(true);
        setPauseContinueMenuItemVisibility();

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu.clear();
        System.out.println("FragmentSports - onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_options_overview, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        setPauseContinueMenuItemVisibility(menu);
    }

    private void setPauseContinueMenuItemVisibility(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean isStepCounterEnabled = sharedPref.getBoolean(getString(R.string.pref_step_counter_enabled), true);
        if(isStepCounterEnabled){
            continueBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        }else {
            continueBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
        }
    }
    /*
      * 判断是否为要显示的界面，防止显示的界面内容重叠
      */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        /*System.out.println("FragmentSports - menuVisible" + menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE:View.GONE);*/
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.menu_home;
    }


}
