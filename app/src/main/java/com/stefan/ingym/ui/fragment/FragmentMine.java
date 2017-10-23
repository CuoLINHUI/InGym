package com.stefan.ingym.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.activity.Mine.AboutActivity;
import com.stefan.ingym.ui.activity.Mine.AccountActivity;
import com.stefan.ingym.ui.activity.Mine.LoginActivity;
import com.stefan.ingym.ui.activity.Mine.SettingActivity;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.ui.fragment.mine.memorandum.MemorandumActivity;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.SpUtil;

/**
 * @ClassName: FragmentMine
 * @Description: 应用程序“我的”模块
 * @Author Stefan
 * @Date 2017/9/22 15:52
 */

public class FragmentMine extends Fragment {
    // 布局文件的Vew
    private View view;

    @ViewInject(R.id.btn_login)				// 登陆按钮
    private Button btn_login;
    @ViewInject(R.id.ll_setting)            // 进入设置界面按钮
    private LinearLayout ll_setting;
    @ViewInject(R.id.user_info_group)       // 用户信息LinearLayout
    private LinearLayout user_info_group;
    @ViewInject(R.id.to_login_group)       // 用户登陆LinearLayout
    private LinearLayout to_login_group;
    @ViewInject(R.id.tv_username)			// 用户名信息
    private TextView tv_username;
    @ViewInject(R.id.tv_follow)             // 关注数
    private TextView tv_follow;
    @ViewInject(R.id.tv_integral)			// 用户积分信息
    private TextView tv_integral;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mine, null);

        // 注入方法（系统在启动的时候可以将xml的控件的属性赋值给相应控件,通过反射和泛型来实现的，代替了findViewById）
        ViewUtils.inject(this, view);

        initData();

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

    private void initData() {
        tv_follow.setText("0");
        tv_integral.setText("0");
    }

    /**
     * 用户点击事件的监听
     */
    @OnClick({R.id.btn_login, R.id.ll_setting, R.id.user_info_group, R.id.ll_about, R.id.btn_memorandum})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:    // 用户点击了登陆按钮
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

            case R.id.ll_setting:   // 用户点击了设置条目
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;

            case R.id.user_info_group:  // 用户点击了用户信息条目
                startActivity(new Intent(getActivity(), AccountActivity.class));
                break;

            case R.id.ll_about:         // 用户点击了关于信息条目
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;

            case R.id.btn_memorandum:    // 用户点击了记事本
                startActivity(new Intent(getActivity(), MemorandumActivity.class));
                break;

        }
    }

    /*
	 * onPause 状态转换到 Active 状态时被调用。
	 * onPause() : 当 Activity 被另一个透明或者 Dialog 样式的 Activity 覆盖时的状态。
	 * 			        此时它依然与窗口管理器保持连接，系统继续维护其内部状态，所以它仍然可见，但它已经失去了焦点故不可与用户交互。
	 */
    // 在这里当用户登陆完毕之后界面会返回到FragmentMy.java，此时会调用onResume()方法
    @Override
    public void onResume() {
        super.onResume();
        // 从Sp中获取本地用户名
        String username = SpUtil.getString(getActivity(), ConstantValue.LOGIN_USER, null);

        // 将登陆成功的用户信息封装到User实体类中
        Gson gson = new GsonBuilder().create();
        User user = gson.fromJson(username, User.class);

        if (user != null) {
            to_login_group.setVisibility(View.GONE);		        // 设置登陆条目不可见
            user_info_group.setVisibility(View.VISIBLE);	        // 设置用户信息条目可见
            tv_username.setText(user.getUsername());			    // 给条目设置上用户名数据
            if (user.getIntegral() != null) {
                tv_integral.setText(user.getIntegral());	// 给条目设置上积分数据
            } else {
                tv_integral.setText("0");
            }
        } else {
            to_login_group.setVisibility(View.VISIBLE);		        // 设置登陆条目可见
            user_info_group.setVisibility(View.GONE);		        // 设置用户信息条目不可见
        }

    }


}
