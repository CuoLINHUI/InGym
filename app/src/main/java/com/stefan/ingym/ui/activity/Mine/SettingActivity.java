package com.stefan.ingym.ui.activity.Mine;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;

/**
 * @ClassName: SettingActivity
 * @Description: 设置activity
 * @Author Stefan
 * @Date 2017/10/7 21:31
 */
public class SettingActivity extends AppCompatActivity {

    @ViewInject(R.id.current_verison)   //当前版本
    private TextView current_verison;
    @ViewInject(R.id.ingym_tel)         // InGym客服电话
    private TextView ingym_tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 初始化，自动将框架与这些变量绑定，相当省略书写findViewById(id)方法
        ViewUtils.inject(this);
        // 初始化Toolbar
        init_toolbar();
        // 设置上当前版本信息
        current_verison.setText(getResources().getString(R.string.current_version) + getVersion());

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.ingym_setting_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    /**
     * 设置用户点击监听事件
     */
    @OnClick({R.id.check_update, R.id.feedback, R.id.custom_service})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_update:  // 用户点击了检查更新

                break;
            case R.id.feedback:      // 用户点击了意见反馈

                break;
            case R.id.custom_service:     // 用户点击了客服电话
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + ingym_tel.getText().toString().trim())));
                break;
        }
    }

    /**
     * 获取App的版本信息
     * @return 返回版本信息
     */
    private String getVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;   // 得到版本号
            return versionName; // 返回得到的版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 未能获取到版本信息返回Unknown
        return "Unknown";
    }


}
