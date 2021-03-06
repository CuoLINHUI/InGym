package com.stefan.ingym.ui.activity.Mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.ImageUtils;
import com.stefan.ingym.util.SpUtil;

public class AccountActivity extends AppCompatActivity {

    @ViewInject(R.id.civ_account_head_img)
    private de.hdodenhof.circleimageview.CircleImageView civ_account_head_img;
    @ViewInject(R.id.tv_nickname)
    private TextView tv_nickname;

    private User user;
    private Gson gson;

    private String modified_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ViewUtils.inject(this);
        init_toolbar();
        initUI();

    }

    /**
     * 初始化数据
     */
    private void initUI() {
        // 从Sp中获取本地用户名
        String username = SpUtil.getString(getApplicationContext(), ConstantValue.IDENTIFIED_USER, null);

        // 将登陆成功的用户信息封装到User实体类中
        gson = new GsonBuilder().create();
        user = gson.fromJson(username, User.class);
        // 设置用户头像
        Picasso.with(getApplicationContext()).load(user.getHeadUrl())
                .placeholder(R.mipmap.user_icon).into(civ_account_head_img);
        // 设置用户名
        tv_nickname.setText(user.getNickname());
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.account_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
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
    @OnClick({R.id.btn_exit, R.id.civ_account_head_img, R.id.ll_modify_nickname,
            R.id.ll_modify_password, R.id.ll_set_payment_password, R.id.ll_bind_phoneNum, R.id.ll_manage_address})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:             // 用户点击退出登录
                // 弹出对话框，提示用户是否确定退出当前用户
                showExitDialog();
                break;

            case R.id.civ_account_head_img:    // 用户点击更换头像
                // 显示获取照片不同方式对话框
                ImageUtils.showImagePickDialog(AccountActivity.this);
                break;

            case R.id.ll_modify_nickname:      // 用户修改昵称
                Intent nicknameIntent = new Intent(this, ModifyNicknameActivity.class);
                nicknameIntent.putExtra("nickname_modify", user);
                startActivityForResult(nicknameIntent, 10000);
                break;

            case R.id.ll_modify_password:      // 用户修改登陆密码
                Intent passwordIntent = new Intent(this, ModifyPasswordActivity.class);
                passwordIntent.putExtra("password_modify", user);
                startActivityForResult(passwordIntent, 20000);
                break;

            case R.id.ll_set_payment_password: // 用户设置支付密码
                // 判断用户是否设置过支付密码
                if (user.getPayPwd() == null) {
                    // 进入设置页
                    Intent setPaymentIntent = new Intent(this, SetPaymentActivity.class);
                    setPaymentIntent.putExtra("set_payment", user);
                    startActivityForResult(setPaymentIntent, 30000);
                } else {
                    // 进入更改页
                    Intent paymentIntent = new Intent(this, ModifyPaymentActivity.class);
                    paymentIntent.putExtra("payment_modify", user);
                    startActivityForResult(paymentIntent, 31000);
                }
                break;

            case R.id.ll_bind_phoneNum:       // 用户绑定手机号
                Intent bindPhoneIntent = new Intent(this, BindPhoneActivity.class);
                bindPhoneIntent.putExtra("bind_phone", user);
                startActivityForResult(bindPhoneIntent, 40000);
                break;

            case R.id.ll_manage_address:      // 用户管理收货地址
                Intent addressIntent = new Intent(this, AddressManagementActivity.class);
                addressIntent.putExtra("address_management", user);
                startActivityForResult(addressIntent, 50000);
//                startActivity(new Intent(getApplication(), AddressManagementActivity.class));
                break;
        }
    }

    /**
     * @param requestCode   确认返回的数据是从哪个Activity返回的
     * @param resultCode    由子Activity通过其setResult()方法返回
     * @param data          一个Intent对象，带有返回的数据。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 用户修改相应信息之后返回修改信息，这里需要修改Sp中的数据时用到。
         */

        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_ALBUM: {

                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }

                Uri imageUri = data.getData();
                ImageUtils.copyImageUri(this,imageUri);
                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            
            case ImageUtils.REQUEST_CODE_FROM_CAMERA: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    ImageUtils.deleteImageUri(this, ImageUtils.getCurrentUri());   //删除Uri
                }

                ImageUtils.cropImageUri(this, ImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            
            case ImageUtils.REQUEST_CODE_CROP: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    return;
                }

                Uri imageUri = ImageUtils.getCurrentUri();
                if (imageUri != null) {
                    civ_account_head_img.setImageURI(imageUri);
                }
                break;
            }
            
            case ModifyNicknameActivity.MODIFIED_NICKNAME:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    modified_nickname = data.getStringExtra("modified_nickname");
                    // 修改原Sp中的nickname
                    user.setNickname(modified_nickname);
                    // 修改好之后重新保存
                    SpUtil.putString(this, ConstantValue.IDENTIFIED_USER, gson.toJson(user));
                    // 设置上新的nickname
                    tv_nickname.setText(modified_nickname);
                }
                break;

            case ModifyPasswordActivity.MODIFIED_PASSWORD:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    String modified_password = data.getStringExtra("modified_password");
                    // 修改原Sp中的nickname
                    user.setLoginPwd(modified_password);
                    // 修改好之后重新保存
                    SpUtil.putString(this, ConstantValue.IDENTIFIED_USER, gson.toJson(user));
                }
                break;

            case SetPaymentActivity.SET_PAYMENT:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    String setPayment = data.getStringExtra("set_payment_ok");
                    // 将设置好的payment保存到Sp中
                    user.setPayPwd(setPayment);
                    // 重新保存更新完成的Sp
                    SpUtil.putString(this, ConstantValue.IDENTIFIED_USER, gson.toJson(user));
                }
                break;

            case ModifyPaymentActivity.MODIFY_PAYMENT:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    String setPayment = data.getStringExtra("modified_payment_ok");
                    // 将设置好的payment保存到Sp中
                    user.setPayPwd(setPayment);
                    // 重新保存更新完成的Sp
                    SpUtil.putString(this, ConstantValue.IDENTIFIED_USER, gson.toJson(user));
                }
                break;

            case BindPhoneActivity.BIND_PHONE_NUMBER:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    String bind_phone = data.getStringExtra("bind_phone");
                    // 将绑定的手机号保存到Sp中
                    user.setTel(bind_phone);
                    // 重新保存更新完成的Sp
                    SpUtil.putString(this, ConstantValue.IDENTIFIED_USER, gson.toJson(user));
                }
                break;
            
            default:
                break;
        }
    }

    /**
     * 创建询问用户是否确定退出当前用户的对话框
     */
    protected void showExitDialog() {
        // 对话框是依赖于activity存在的!!!!!!!!!
        Builder builder = new AlertDialog.Builder(this);  // 这里如果把this换成getApplicationContext()就会报错！
        // 设置对话框标题
        builder.setTitle("提示");
        //设置对话框标题
        builder.setMessage("确定退出当前用户？");
        // 设定对话框“确定”按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            // 用户选择了“确定”按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 清除之前保存在Sp中的用户数据
                SpUtil.remove(getApplicationContext(), ConstantValue.IDENTIFIED_USER);
                // 结束当前页面
                finish();
            }
        });
        // 设定对话框“取消”按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            // 用户选择了“取消”按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隐藏对话框
                dialog.dismiss();
            }
        });
        // 用户点击取消的事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 即使用户对话框中的什么按钮都不点击，也要让用户进入应用程序
                dialog.dismiss();
            }
        });
        // 显示对话框
        builder.show();
    }

}
