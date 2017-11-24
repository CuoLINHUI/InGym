package com.stefan.ingym.ui.activity.Mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.activity.MainActivity;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.ImageUtils;
import com.stefan.ingym.util.SpUtil;

import static com.stefan.ingym.R.id.imageView;

public class AccountActivity extends AppCompatActivity {

    @ViewInject(R.id.civ_mine_head_img)
    private de.hdodenhof.circleimageview.CircleImageView civ_mine_head_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ViewUtils.inject(this);

    }

    /**
     * 设置用户点击监听事件
     */
    @OnClick({R.id.tv_back, R.id.btn_exit, R.id.civ_mine_head_img, R.id.tv_save})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:              // 用户点击了回退按钮
                finish();
                break;

            case R.id.civ_mine_head_img:    // 用户点击更换头像
                // 显示获取照片不同方式对话框
                ImageUtils.showImagePickDialog(AccountActivity.this);
                break;

            case R.id.tv_save:              // 用户点击保存修改

                break;

            case R.id.btn_exit:             // 用户点击退出登录
                // 弹出对话框，提示用户是否确定退出当前用户
                showExitDialog();
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
                    civ_mine_head_img.setImageURI(imageUri);
                }
                break;
            }
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
                SpUtil.remove(getApplicationContext(), ConstantValue.LOGIN_USER);
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
