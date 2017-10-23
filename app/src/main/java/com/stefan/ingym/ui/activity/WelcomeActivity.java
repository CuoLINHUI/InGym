package com.stefan.ingym.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.stefan.ingym.R;
import com.stefan.ingym.util.ToolUtils;

/**
 * @ClassName: WelcomeActivity
 * @Description: WelcomeActivity.java
 * @Author Stefan
 * @Date 2017/9/21 15:11
 */
public class WelcomeActivity extends Activity {

    private String IS_FIRST="IS_FIRST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                // 去Sp中取出state状态
                String state = ToolUtils.getShareData(WelcomeActivity.this, "IS_FIRST", null);

                // 如果第一次启动，即数据不存在，state为空，向SharedPreferences写入数据
                if (state == null) {
                    // 往SP中插入字段
                    ToolUtils.putShareData(WelcomeActivity.this, "IS_FIRST", IS_FIRST);
                    // 开启首次进入欢迎导航界面
                    startActivity(new Intent(WelcomeActivity.this, WhatNewsActivity.class));
                    finish();
                } else if (state.equals("IS_FIRST")) {
                    // 直接进入程序主界面
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }

                return true;
            }
        }).sendEmptyMessageDelayed(0, 1000);     // Value为0的空消息，延迟一秒

    }
}
