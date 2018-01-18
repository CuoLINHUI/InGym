package com.stefan.ingym.ui.activity.Mine;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.stefan.ingym.R.id.et_set_nickname;

public class BindPhoneActivity extends AppCompatActivity {

    public static final int BIND_PHONE_NUMBER = 40000;

    @ViewInject(R.id.bind_phone_number)
    private EditText bind_phone_number;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ViewUtils.inject(this);
        init_toolbar();
        intiData();

    }

    private void intiData() {
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.get("bind_phone");
        String alreadyExist = user.getTel();
        if (alreadyExist != null)
            // 如果用户已经绑定了电话号码，则将其设置在EditView上显示
            bind_phone_number.setText(user.getTel());
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.bind_phone_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    @OnClick({R.id.iv_clear_number, R.id.tv_save_bind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear_number:  // 清除输入框内容
                bind_phone_number.setText("");
                break;

            case R.id.tv_save_bind:     // 绑定到数据库
                String phoneNumber = bind_phone_number.getText().toString().trim();
                String userID = user.getId();
                if (phoneNumber.isEmpty()) {
                    ToastUtil.show(this, "输入不能为空！");
                    return;
                }
                if (11 != (phoneNumber.length())) {
                    ToastUtil.show(this, "请检查输入格式是否正确！");
                    return;
                }

                bindPhone(phoneNumber, userID);
                break;
        }
    }

    private void bindPhone(final String phoneNumber, final String userID) {
        // 封装请求参数
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("phone_number", String.valueOf(phoneNumber));
                put("user_id", String.valueOf(userID));
            }
        };

        HttpUtils.doGet(ConstantValue.BIND_PHONE, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "绑定手机号码失败！请检查网络！");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<String> object = gson.fromJson(json, new TypeToken<ResponseObject<String>>() {
                }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 对解析结果进行判断（服务器端传过来的状态码为1表示成功）
                        if (object.getState() == 1) {
                            bind_phone_number.setText(phoneNumber);
                            ToastUtil.show(getApplicationContext(), "绑定成功！");
                            // 在返回AccountActivity的时候带回修改好的phoneNumber数据
                            setResult(RESULT_OK, new Intent().putExtra("bind_phone", phoneNumber));
                            finish();
                        } else {
                            // 提示服务器端返回错误的信息
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        }
                    }
                });
            }
        });

    }

}
