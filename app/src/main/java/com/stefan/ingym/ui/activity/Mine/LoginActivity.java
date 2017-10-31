package com.stefan.ingym.ui.activity.Mine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @ClassName: LoginActivity
 * @Description: 登陆activity
 * @Author Stefan
 * @Date 2017/9/24 20:47
 */
public class LoginActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_back)           // 返回按钮
    private TextView tv_back;
    @ViewInject(R.id.tv_register)       // 注册按钮
    private  TextView tv_register;
    @ViewInject(R.id.fly_view)			// 登陆方法切换的view
    private View fly_view;
    @ViewInject(R.id.rg_login)			// RadioGroup的ID
    private RadioGroup rg_login;
    @ViewInject(R.id.btn_login)         // 登陆按钮
    private Button btn_login;
    @ViewInject(R.id.rb_account_login)	// 账号密码登陆按钮
    private Button rb_account_login;
    @ViewInject(R.id.rb_quick_login)	// 快速登录按钮
    private Button rb_quick_login;
    @ViewInject(R.id.et_username)		// 用户名输入框
    private EditText et_username;
    @ViewInject(R.id.et_password)		// 密码输入框
    private EditText et_password;
    @ViewInject(R.id.btn_vertify)		// 获取验证码按钮
    private Button btn_vertify;

    /**
     * 申明动画
     */
    private Animation move_to_left;
    private Animation move_to_right;

    /**
     * Text观察者
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 判断用户名框和密码框都不为空
            if (et_username.getText().toString().trim().length() > 0
                    && et_password.getText().toString().trim().length() > 0) {
                // 设置登陆按钮的状态为可以点击
                btn_login.setEnabled(true);
            } else {
                // 否则设置登陆按钮的状态为不可以点击
                btn_login.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 初始化，自动将框架与这些变量绑定
        ViewUtils.inject(this);
        // 初始化Toolbar
        init_toolbar();
        // 调用用户登陆选择方法
        initLoginSelection();

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
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
     * 初始化用户登陆选择（账户登陆，快速登录）
     */
    private void initLoginSelection() {
        // 载入动画
        move_to_left = AnimationUtils.loadAnimation(this, R.anim.move_to_left);
        move_to_right = AnimationUtils.loadAnimation(this, R.anim.move_to_right);

        et_username.addTextChangedListener(mTextWatcher);
        et_password.addTextChangedListener(mTextWatcher);

        // RadioGroup的点击监听事件方法
        rg_login.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_account_login:	 // 账户登录
                        fly_view.startAnimation(move_to_left);		// 往左的动画效果
                        btn_vertify.setVisibility(View.GONE);		// 获取验证码条目不可见
                        et_username.setText("用户名/邮箱/手机号");
                        et_password.setText("请输入密码");
                        break;

                    case R.id.rb_quick_login:	// 快速登录
                        fly_view.startAnimation(move_to_right);		// 往右的动画效果
                        btn_vertify.setVisibility(View.VISIBLE);	// 获取验证码条目可见
                        et_username.setText("请输入手机号");
                        et_password.setText("请输入密码");
                        break;
                }
            }
        });
    }

    /**
     * 设置用户点击监听事件
     */
    @OnClick({R.id.tv_register, R.id.btn_login, R.id.tv_forget_password})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:  // 用户点击了注册按钮
                // 跳转到用户注册界面
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;

            case R.id.btn_login:    // 用户点击了登陆按钮
                // 获取用户登陆时输入的用户名和密码
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                /**
                 * 非空校验
                 */
                // 判断用户名是否为空
                if (username.length() <= 0) {
                    et_username.setError("用户名不能为空！");
                    return;
                }
                // 判断密码是否为空
                if (password.trim().length() <= 0) {
                    et_password.setError("密码不能为空！");
                    return;
                }
                /**
                 * 以上条件都满足，则向服务器端发送登录请求
                 * 这里调用请求服务端账号密码登录方法
                 */
                userAccountLogin(username, password);
                break;

            case R.id.tv_forget_password:   // 用户点击了忘记密码
                ToastUtil.show(getApplication(), "我也很绝望╮(╯▽╰)╭");
                break;

        }
    }

    /**
     * 向服务器端发送用户登录请求方法（账号密码登陆）
     * @param username
     * @param password
     */
    private void userAccountLogin(String username, String password) {
        // 封装用户注册填写的用户名和密码到User中
        User user = new User();
        user.setUsername(username);
        user.setLoginPwd(password);
        // 向服务端发送请求（请求方法，维护的访问路径，需要传递的参数，返回值）
        com.stefan.ingym.util.HttpUtils.doPost(ConstantValue.USER_ACCOUNT_LOGIN, new Gson().toJson(user), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.show(getApplicationContext(), "抱歉，登陆失败！");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 解析服务器端返回过来的结果
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                System.out.println(json);

                final ResponseObject<User> object = gson.fromJson(json, new TypeToken<ResponseObject<User>>(){}.getType());
                // 对解析结果进行判断（服务器端传过来的状态码为1表示成功）
                if (object.getState() == 1) {
                    // 保存用户名到本地
                    SpUtil.putString(getApplicationContext(), ConstantValue.LOGIN_USER, gson.toJson(object.getDatas()));
                    // 登陆成功就关闭当前页面
                    finish();
                }
                // UI更新需要放在主线程（UI线程）中完成。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 提示服务器端返回的信息
                        ToastUtil.show(getApplicationContext(), object.getMsg());
                    }
                });
            }
        });
    }

}
