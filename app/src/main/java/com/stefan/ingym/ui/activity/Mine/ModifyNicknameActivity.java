package com.stefan.ingym.ui.activity.Mine;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @ClassName: ModifyNicknameActivity
 * @Description: 修改昵称
 * @Author Stefan
 * @Date 2017/12/7 16:27
 */

public class ModifyNicknameActivity extends AppCompatActivity {

    public static final int MODIFIED_NICKNAME = 1;

    @ViewInject(R.id.et_set_nickname)       // 设置昵称
    private EditText et_set_nickname;

    private User user;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname);
        ViewUtils.inject(this);
        init_toolbar();
        intiData();

    }

    private void intiData() {
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.get("nickname_modify");
        // 给输入框中设置上用户原有昵称
        et_set_nickname.setText(user.getNickname());
    }

    @OnClick({R.id.iv_clear_nickname, R.id.tv_save_modify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear_nickname:        // 清除输入框内容
                et_set_nickname.setText("");
                break;

            case R.id.tv_save_modify:           // 更新数据库昵称
                String newNickname = et_set_nickname.getText().toString().trim();
                String userID = user.getId();
                if (newNickname.isEmpty()) {
                    ToastUtil.show(this, "昵称不能为空！");
                    return;
                }
                updateNickname(newNickname, userID);
                break;

        }
    }

    /**
     * 请求服务端更新数据库的用户昵称
     */
    private void updateNickname(final String newNickname, final String userID) {
        // 请求参数
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("new_nickname", String.valueOf(newNickname));
                put("user_id", String.valueOf(userID));
            }

        };
        // 请求服务器
        HttpUtils.doGet(ConstantValue.MODIFY_NICKNAME, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                ToastUtil.show(getApplicationContext(), "修改昵称失败！请检查网络！");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<User> object = gson.fromJson(json, new TypeToken<ResponseObject<User>>(){}.getType());
                // UI更新需要放在主线程（UI线程）中完成。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 对解析结果进行判断（服务器端传过来的状态码为1表示成功）
                        if (object.getState() == 1) {
                            et_set_nickname.setText(newNickname);
//                            Log.i("ModifyNicknameActivity:", gson.toJson(object.getDatas()));
                            ToastUtil.show(getApplicationContext(), "修改成功！");
                            // 在返回AccountActivity的时候带回修改好的nickname数据
                            setResult(RESULT_OK, new Intent().putExtra("modified_nickname", newNickname));
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
        Toolbar mToolbar = (Toolbar) findViewById(R.id.modify_nickname_toolbar);
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
