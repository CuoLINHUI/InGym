package com.stefan.ingym.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FragmentSports extends PedometerBaseFragment implements DailyReportFragment.OnFragmentInteractionListener, WeeklyReportFragment.OnFragmentInteractionListener, MonthlyReportFragment.OnFragmentInteractionListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sports, null);

        // init preferences
        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(getContext(), R.xml.pref_notification, false);

        // Load first view
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new MainFragment(), "MainFragment");
        fragmentTransaction.commit();

        // Start step detection if enabled and not yet started
        StepDetectionServiceHelper.startAllIfEnabled(getContext());
        //Log.i(LOG_TAG, "MainActivity initialized");
        
        return view;
    }
    /*
      * 判断是否为要显示的界面，防止显示的界面内容重叠
      */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE:View.GONE);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.menu_home;
    }


}
