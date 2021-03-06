package com.stefan.ingym.ui.activity.Mine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import com.stefan.ingym.R;
import com.stefan.ingym.pojo.ResponseObject;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.Md5Util;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @ClassName: RegisterActivity
 * @Description: 用户注册activity
 * @Author Stefan
 * @Date 2017/9/25 10:57
 */
public class RegisterActivity extends Activity {

    @ViewInject(R.id.register_username)		// 用户注册名
    private EditText register_username;
    @ViewInject(R.id.register_pwd)			// 用户注册密码
    private EditText register_pwd;
    @ViewInject(R.id.register_confirm_pwd)	// 用户注册确认密码
    private EditText register_confirm_pwd;
    @ViewInject(R.id.register_email)	    // 用户邮箱
    private EditText register_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewUtils.inject(this);
        // 初始化Toolbar
        init_toolbar();
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
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
    @OnClick({R.id.btn_register, R.id.tv_reset})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: // 用户点击了注册按钮
                // 获取用户注册填入的用户名
                String username = register_username.getText().toString().trim();
                // 获取用户注册填入的密码
                String password = register_pwd.getText().toString().trim();
                // 获取用户注册填入的确认密码
                String confirm_password = register_confirm_pwd.getText().toString().trim();
                // 获取用户注册填入的邮箱地址
                String email = register_email.getText().toString().trim();
                /**
                 * 非空校验
                 */
                // 判断用户名是否为空
                if (username.length() <= 0) {
                    register_username.setError("用户名不能为空！");
                    return;
                }
                // 判断密码是否为空
                if (password.trim().length() <= 0) {
                    register_pwd.setError("密码不能为空！");
                    return;
                }
                // 判断确认密码是否为空
                if (confirm_password.trim().length() <= 0) {
                    register_confirm_pwd.setError("确认密码不能为空！");
                    return;
                }
                // 判断两次密码输入是否一致
                if (!confirm_password.toString().equals(password.toString())) {
                    register_confirm_pwd.setError(Html.fromHtml("<font color=red>两次密码不一致！</font>"));
                    return;
                }
                // 判断邮箱地址是否为空
                if (email.trim().length() <= 0) {
                    register_email.setError("邮箱地址不能为空！");
                    return;
                }
                /**
                 * 以上条件都满足，则向服务器端发送注册请求
                 * 这里调用请求服务端注册方法
                 */
                // 给密码MD5加密
                String encoderPW = Md5Util.encoder(password);
//                Log.i("密码加密：", encoderPW);
                register(username, encoderPW);
                break;

            case R.id.tv_reset: // 用户点击了重置按钮
                register_username.setText("");
                register_pwd.setText("");
                register_confirm_pwd.setText("");
                register_email.setText("");
                break;

        }
    }

    /**
     * 向服务器端发送注册请求方法
     * @param username
     * @param password
     */
    private void register(final String username, final String password) {
        // 封装用户注册填写的用户名和密码到User中
        User user = new User();
        user.setUsername(username);
        user.setLoginPwd(password);

        // 向服务端发送请求
        com.stefan.ingym.util.HttpUtils.doPost(ConstantValue.USER_REGISTER, new Gson().toJson(user), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "注册失败！请检查网络！");
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                /**
                 * 若用户名存在，提示用户该用户名已经存在
                 */
                String json = response.body().string();
//                System.out.println("RegisterActivity:::" + json);
                // 解析服务器端返回过来的结果
               final ResponseObject<String> object = new GsonBuilder().create().fromJson(json, new TypeToken<ResponseObject<String>>(){}.getType());
                // UI更新需要放在主线程（UI线程）中完成。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 返回的状态码为1表示注册成功
                        if (object.getState() == 1) {
                            ToastUtil.show(getApplicationContext(), "注册成功！");
                            // 注册成功弹出吐司提示用户之后，直接关闭当前注册页面
                            finish();
                        } else {	// 否则表示注册失败，吐司出服务器返回的错误信息
                            ToastUtil.show(getApplicationContext(), "错误信息:" + object.getMsg());
                        }
                    }
                });
            }
        });
    }
}
