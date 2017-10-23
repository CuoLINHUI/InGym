package com.stefan.ingym.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.FragmentCommunity;
import com.stefan.ingym.ui.fragment.FragmentIndex;
import com.stefan.ingym.ui.fragment.FragmentMine;
import com.stefan.ingym.ui.fragment.FragmentSports;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.DailyReportFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.MonthlyReportFragment;
import com.stefan.ingym.ui.fragment.sports.pedometer.fragment.WeeklyReportFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: MainActivity
 * @Description: 控制管理界面底部的四个Fragmet
 * @Author Stefan
 * @Date 2017/9/21
 */
public class MainActivity extends AppCompatActivity implements DailyReportFragment.OnFragmentInteractionListener, WeeklyReportFragment.OnFragmentInteractionListener, MonthlyReportFragment.OnFragmentInteractionListener {

    @ViewInject(R.id.bottom_bar)        // 向框架注入id,现在只是申明，还没
    private RadioGroup bottom_bar;      // 赋值
    @ViewInject(R.id.viewpager_content)
    private ViewPager viewpager_content;

    // 定义界面是否已经初始化过的布尔类型变量
    private boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化，自动将框架与这些变量绑定，相当省略书写findViewById(id)方法
        ViewUtils.inject(this);

        // 直播初始化
        StreamingEnv.init(getApplicationContext());

    }

    /**
     * 注入框架
     * RadioGroup按钮监听事件
     * 使其自动绑定按钮事件监听
     */
    @OnRadioGroupCheckedChange({ R.id.bottom_bar })
    // 按钮事件监听方法实现
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        // 设置默认选中的是“健身”模块（IndexFragment）
        int index = 0;
        switch (checkedId) {
            case R.id.body_building_rb0:
                index = 0;  // 0表示“健身”模块
                break;
            case R.id.sports_rb1:
                index = 1;  // 1表示“运动”模块
                break;
            case R.id.community_rb2:
                index = 2;  // 2表示“社区”模块
                break;
            case R.id.mine_rb3:
                index = 3;  // 3表示“我的”模块
                break;
        }

        // 将下面维护好的FragmentStatePagerAdapter放入适配器
        viewpager_content.setAdapter(fragments);
        // 将维护好的index值设置进当前条目
        viewpager_content.setCurrentItem(index);
        //
        viewpager_content.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    // 为使开始就触发onCheckedChanged事件，默认进入"健身"模块显示
    protected void onResume() {
        super.onResume();
        // 友盟启动统计的初始化
        MobclickAgent.onResume(this);
        // 如果界面已经初始化过了就不要在初始化了（这样可以保证当用户在FragmentMine.java中登陆完成，结束登陆界面后，还是停留在FragmentMine.java界面中）
        if (!isInit) {
            bottom_bar.check(R.id.body_building_rb0);
            isInit = true;
        }
    };

    protected void onPause() {
        super.onPause();
        // 暂停时调用(使用友盟统计该App一天启动了多少次，有多少人使用等信息)
        MobclickAgent.onPause(this);
    };

    /**
     * 创建一个Fragment适配器，用于管理这四个fragment的变化
     */
    FragmentStatePagerAdapter fragments = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public int getCount() {
            return 4;   // 需要管理的fragment的数量是4
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: // “健身”模块（应用程序主界面）
                    fragment = new FragmentIndex();
                    break;
                case 1: // “运动”模块
                    fragment = new FragmentSports();
                    break;
                case 2: // “社区”模块
                    fragment = new FragmentCommunity();
                    break;
                case 3: // “我的”模块
                    fragment = new FragmentMine();
                    break;
                default:
                    fragment = new FragmentIndex();
                    break;
            }
            // 返回相应的Fragment
            return fragment;
        }

    };

    /**
     * 内部类
     * 页面切换监听
     */
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int id) {
            switch (id) {
                case 0:
                    bottom_bar.check(R.id.body_building_rb0);
                    break;
                case 1:
                    bottom_bar.check(R.id.sports_rb1);
                    break;
                case 2:
                    bottom_bar.check(R.id.community_rb2);
                    break;
                case 3:
                    bottom_bar.check(R.id.mine_rb3);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

}
