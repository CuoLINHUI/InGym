package com.stefan.ingym.ui.activity.Mine;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
import com.stefan.ingym.util.HttpUtils;
import com.stefan.ingym.util.Md5Util;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SetPaymentActivity extends AppCompatActivity {

    public static final int SET_PAYMENT = 30000;

    @ViewInject(R.id.set_payment)       // 输入支付密码
    private EditText set_payment;
    @ViewInject(R.id.confirm_payment)   // 确认支付密码
    private EditText confirm_payment;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_payment);

        // 获取AccountActivity传递过来的数据
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.get("set_payment");

        ViewUtils.inject(this);
        init_toolbar();
    }

    @OnClick({R.id.save_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_set:              // 设置支付密码
                String setPayment = set_payment.getText().toString().trim();
                String confirmPayment = confirm_payment.getText().toString().trim();
                if (TextUtils.isEmpty(setPayment)) {
                    set_payment.setError("请输入支付密码!");
                    return;
                }
                if (TextUtils.isEmpty(confirmPayment)) {
                    confirm_payment.setError("请再次确认支付密码!");
                    return;
                }
                // 判断两次密码输入是否一致，不一致则不允许继续往下执行
                if (!setPayment.equals(confirmPayment)) {
                    confirm_payment.setError(Html.fromHtml("<font color=red>确认密码不一致！</font>"));
                    return;
                }
                /**
                 * 以上条件都满足，则向服务器端发送注册请求
                 * 这里调用请求服务端注册方法
                 */
                // 给密码MD5加密
                String encoderPayment = Md5Util.encoder(confirmPayment);
                setPayment(user.getId(), encoderPayment);
                break;
        }
    }

    private void setPayment(String id, final String encoderPayment) {
        // 封装用户注册填写的用户名和密码到User中
        User user = new User();
        user.setId(id);
        user.setPayPwd(encoderPayment);

        HttpUtils.doPost(ConstantValue.SET_PAYMENT, new Gson().toJson(user), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "设置失败！请检查网络！");
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
                            ToastUtil.show(getApplication(), "设置成功！");
                            // 在返回AccountActivity的时候带回修改好的payment数据
                            setResult(RESULT_OK, new Intent().putExtra("set_payment_ok", encoderPayment));
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
        Toolbar mToolbar = (Toolbar) findViewById(R.id.set_payment_toolbar);
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
