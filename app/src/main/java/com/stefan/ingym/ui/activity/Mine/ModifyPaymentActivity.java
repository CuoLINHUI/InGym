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

/**
 * @ClassName: ModifyPaymentActivity
 * @Description: 支付密码修改
 * @Author Stefan
 * @Date 2018/1/30 21:28
 */

public class ModifyPaymentActivity extends AppCompatActivity {

    public static final int MODIFY_PAYMENT = 31000;

    @ViewInject(R.id.input_old_payment)     // 输入旧支付密码
    private EditText input_old_payment;
    @ViewInject(R.id.input_new_payment)     // 输入新支付密码
    private EditText input_new_payment;
    @ViewInject(R.id.confirm_new_payment)   // 确认新支付密码
    private EditText confirm_new_payment;

    private User user;
    private String oldPayment;
    private String newPayment;
    private String confirmPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_payment);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.modify_payment_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        // 获取AccountActivity传递过来的数据
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.get("payment_modify");
    }

    @OnClick({R.id.payment_modify, R.id.forgot_old_payment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payment_modify:              // 修改用户登陆密码
                /**
                 * 获取用户输入
                 */
                oldPayment = input_old_payment.getText().toString().trim();
                newPayment = input_new_payment.getText().toString().trim();
                confirmPayment = confirm_new_payment.getText().toString().trim();
                if (TextUtils.isEmpty(oldPayment)) {
                    input_old_payment.setError("请填写原支付密码!");
                    return;
                }
                if (TextUtils.isEmpty(newPayment)) {
                    input_new_payment.setError("请输入新支付密码!");
                    return;
                }
                if (TextUtils.isEmpty(confirmPayment)) {
                    confirm_new_payment.setError("请再次确认支付密码!");
                    return;
                }

                // 判断旧密码是否正确，不正确则不允许继续往下执行
                if (!user.getPayPwd().equals(Md5Util.encoder(oldPayment))) {
                    ToastUtil.show(getApplicationContext(), "旧密码输入有误");
                    return;
                }
                // 判断两次密码输入是否一致，不一致则不允许继续往下执行
                if (!newPayment.equals(confirmPayment)) {
                    confirm_new_payment.setError(Html.fromHtml("<font color=red>确认密码不一致！</font>"));
                    return;
                }
                /**
                 * 以上条件都满足，则向服务器端发送注册请求
                 * 这里调用请求服务端注册方法
                 */
                // 给密码MD5加密
                String encoderNewPayment = Md5Util.encoder(confirmPayment);
                modifyPayment(user.getId(), encoderNewPayment);
                break;

            case R.id.forgot_old_payment:      // 该功能未完成
                ToastUtil.show(this, "不好意思，我也很无奈——_——");
                break;
        }
    }

    /**
     * 请求服务端修改用户登陆密码
     * @param userID
     * @param newPayment
     */
    private void modifyPayment(String userID, final String newPayment) {

        // 封装用户注册填写的用户名和密码到User中
        User user = new User();
        user.setId(userID);
        user.setPayPwd(newPayment);

        HttpUtils.doPost(ConstantValue.MODIFY_PAYMENT, new Gson().toJson(user), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "修改失败！请检查网络！");
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
                            // 在返回AccountActivity的时候带回修改好的password数据
                            setResult(RESULT_OK, new Intent().putExtra("modified_payment_ok", newPayment));
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

}
