package com.stefan.ingym.ui.activity.Mine;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import com.stefan.ingym.ui.fragment.mine.memorandum.util.StringUtil;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.HttpUtils;
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static okhttp3.Protocol.get;

/**
 * @ClassName: ModifyPasswordActivity
 * @Description: 修改登录密码
 * @Author Stefan
 * @Date 2017/12/9 20:57
 */

public class ModifyPasswordActivity extends AppCompatActivity {

    @ViewInject(R.id.show_username)          // 显示用户名
    private TextView show_username;
    @ViewInject(R.id.input_old_password)     // 输入旧密码
    private EditText input_old_password;
    @ViewInject(R.id.input_new_password)     // 输入新密码
    private EditText input_new_password;
    @ViewInject(R.id.confirm_new_password)   // 确认新密码
    private EditText confirm_new_password;

    private User user;

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ViewUtils.inject(this);
        init_toolbar();
        initData();
    }

    private void initData() {
        // 获取AccountActivity传递过来的数据
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.get("password_modify");
        // 给输入框中设置上用户原有用户名
        show_username.setText(user.getUsername());
    }

    @OnClick({R.id.save_modify, R.id.forgot_old_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_modify:              // 修改用户登陆密码
                /**
                 * 获取用户输入
                 */
                oldPassword = input_old_password.getText().toString().trim();
                newPassword = input_new_password.getText().toString().trim();
                confirmPassword = confirm_new_password.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)) {
                    input_old_password.setError("请填写旧密码!");
                     return;
                }
                if (TextUtils.isEmpty(newPassword)) {
                    input_new_password.setError("请输入新的密码!");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    confirm_new_password.setError("请再次输入新密码!");
                    return;
                }
                // 判断两次密码输入是否一致
                if (!newPassword.equals(confirmPassword)) {
                    confirm_new_password.setError(Html.fromHtml("<font color=red>确认密码不一致！</font>"));
                }
                /**
                 * 以上条件都满足，则向服务器端发送注册请求
                 * 这里调用请求服务端注册方法
                 */
                modifyPassword(user.getId(), oldPassword, newPassword);
                break;

            case R.id.forgot_old_password:      // 该功能未完成
                ToastUtil.show(this, "不好意思，我也很无奈——_——");
                break;
        }

    }

    /**
     * 请求服务端修改用户登陆密码
     * @param oldPassword
     * @param newPassword
     */
    private void modifyPassword(final String userID, final String oldPassword, final String newPassword) {

        Map<String, String> params = new HashMap<String, String>() {
            {
                put("user_id", String.valueOf(userID));
                put("old_password", String.valueOf(oldPassword));
                put("new_password", String.valueOf(newPassword));
            }
        };

        HttpUtils.doPost(ConstantValue.MODIFY_PASSWORD, new Gson().toJson(params), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "注册失败！请检查网络！");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<String> object = gson.fromJson(json, new TypeToken<ResponseObject<String>>() {
                }.getType());
                // 需要更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 对解析结果进行判断（服务器端传过来的状态码为1表示成功）
                        if (object.getState() == 1) {
                            // 提示用户密码修改成功
                            ToastUtil.show(getApplication(), "修改成功！");
                            // 清除之前保存在Sp中的用户数据
                            SpUtil.remove(getApplicationContext(), ConstantValue.IDENTIFIED_USER);
                            finish();
                        } else {
                            // 提示服务器端返回的信息
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        }
                    }
                });
            }
        });

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.modify_password_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

}
