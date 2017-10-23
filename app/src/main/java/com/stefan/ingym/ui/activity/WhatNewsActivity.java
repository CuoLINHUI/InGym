package com.stefan.ingym.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.stefan.ingym.R;
import com.stefan.ingym.adapter.GuideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: WhatNewsActivity
 * @Description: WhatNewsActivity.java
 * @Author Stefan
 * @Date 2017/9/21 15:36
 */
public class WhatNewsActivity extends Activity {

    private ViewPager viewPager;
    private Button btn_start;
    // 创建一个集合
    List<View> mList = new ArrayList<View>();
    // 创建图片（首次进入的导航图片）数组
    int[] mImage = new int[] {R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_news);

        // 调用方法初始化界面方法
        initUI();
        // 调用监听事件方法
        setListener();
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        btn_start = (Button) findViewById(R.id.btn_start);

        ImageView[] imageViews = new ImageView[3];

        // 将引导界面的三张图片添加到list里面去，以便用于加载viewpager
        for (int i = 0; i <= 2; i++) {
            imageViews[i] = new ImageView(WhatNewsActivity.this);
            imageViews[i].setImageResource(mImage[i]);
            // 添加循环到的图片至集合中去
            mList.add(imageViews[i]);
        }
        // 初始化导航数据适配器
        GuideAdapter adapter = new GuideAdapter(mList);
        // 将维护好的数据适配器添加到控件中去
        viewPager.setAdapter(adapter);
    }

    /**
     * 为控件设置监听器
     */
    private void setListener() {
        // mButton点击监听事件
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用户点击按钮跳转到程序主界面
                startActivity(new Intent(WhatNewsActivity.this, MainActivity.class));
                // 结束当前页面
                finish();
            }
        });

        // mViewPager页面切换监听事件
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageId) {
                // 控制第三页显示按钮，前面两页按钮隐藏
                if (pageId == 2) {
                    btn_start.setVisibility(View.VISIBLE);
                } else {
                    btn_start.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

        });
    }

}
