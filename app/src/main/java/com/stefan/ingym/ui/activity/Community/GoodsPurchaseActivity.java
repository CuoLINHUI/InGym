package com.stefan.ingym.ui.activity.Community;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.ResponseObject;
import com.stefan.ingym.pojo.community.Goods;
import com.stefan.ingym.pojo.mine.Address;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.ui.activity.Mine.SetPaymentActivity;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.HttpUtils;
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;
import com.stefan.ingym.view.CustomDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @ClassName: GoodsPurchaseActivity
 * @Description: 商品购买确认页面（确认订单）
 * @Author Stefan
 * @Date 2018/1/19 14:41
 */

public class GoodsPurchaseActivity extends AppCompatActivity {

    /*地址部分*/
    @ViewInject(R.id.tv_addressee)      // 收件人
    private TextView tv_addressee;
    @ViewInject(R.id.addressee_phone)   // 联系电话
    private TextView addressee_phone;
    @ViewInject(R.id.tv_address)        // 收件地址
    private TextView tv_address;

    /*purchase_goods_item*/
    @ViewInject(R.id.purchase_num)           // 购买总数
    private TextView purchase_num;
    /*purchase_goods_list*/
    @ViewInject(R.id.goods_total_num)        // 购买总量
    private TextView goods_total_num;
    @ViewInject(R.id.goods_count)            // 初始值：共1件商品
    private TextView goods_count;
    @ViewInject(R.id.purchase_total_money)   // 购买总价
    private TextView purchase_total_money;
    /*activity_goods_purchase*/
    @ViewInject(R.id.total_money)           // 购买总价(合计金额：)
    private TextView total_money;


    /*商品部分*/
    @ViewInject(R.id.goods_pic)             // 商品图片
    private ImageView goods_pic;
    @ViewInject(R.id.goods_title)           // 商品标题
    private TextView goods_title;
    @ViewInject(R.id.goods_single_price)    // 商品单件价格
    private TextView goods_single_price;

    private Address address;
    private Goods goods;
    private User user;
    private Gson gson;
    private int integral;       // 本次购买所需的积分
    private double totalPrice;
    private int count;          // 商品总量

    // 定义一个Builder和一个CustomDialog实例
    private CustomDialog.Builder builder;
    private CustomDialog mDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_purchase);
        ViewUtils.inject(this);
        init_toolbar();
        initData();

        builder = new CustomDialog.Builder(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.goods_purchase_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    private void initData() {
        // 获取当前选中商品的数据
        Bundle bundle = getIntent().getExtras();
        goods = (Goods) bundle.get("goods_info");
        // 渲染商品数据
        Picasso.with(getApplicationContext()).load(goods.getBigPicURL())
                .placeholder(R.drawable.default_pic).into(goods_pic);
        String titleStr = goods.getTitle() + " " + goods.getSort_title();
        goods_title.setText(titleStr);
        goods_single_price.setText("" + goods.getPrice());
        // 相关数据初始化（商品价格、数量）
        purchase_num.setText("1");
        goods_total_num.setText("1");
        goods_count.setText("共1件商品");
        purchase_total_money.setText("" + goods.getPrice());
        total_money.setText("" + goods.getPrice());

        totalPrice = goods.getPrice();      // 初始化购买本次订单总价
        count = 1;
        integral = goods.getIntegral();   // 初始化购买本次订单总积分

        // 渲染地址数据
        // 获取保存在Sp中的默认地址数据
        String gsonStr = SpUtil.getString(this, ConstantValue.DEFAULT_ADDRESS, null);
        if (gsonStr != null) {
            // 将存储的地址gson字符串转换为pojo类型
            Gson gson = new GsonBuilder().create();
            address = gson.fromJson(gsonStr, Address.class);
            // 将获取的数据设置给对应的控件
            tv_addressee.setText("收件人:" + address.getConsignee());   // 收件人
            addressee_phone.setText(address.getPhone());               // 联系电话
            String finalAddress = "收件地址：" + address.getProvince() + address.getDetail();
            tv_address.setText(finalAddress);                          // 收件详细地址
        }

    }

    @OnClick({R.id.choose_address, R.id.goods_num_subtract, R.id.goods_num_add, R.id.submit_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_address:         // 点击选择地址
                Intent addressIntent = new Intent(this, AddressSuperviseActivity.class);
//                addressIntent.putExtra("address_management", "");
                startActivityForResult(addressIntent, 6000);
                break;

            case R.id.goods_num_subtract:       // 购买数量减一
                // 如果总数是1，则不允许数量再下减（最低显示数要为1）
                int totalNum = Integer.parseInt(goods_total_num.getText().toString());
                if (totalNum == 1) {
                    return;
                }
                // 商品数量减一之后的数量
                count --;
                // 计算总价：订单总价减去相应的单价
                totalPrice -= goods.getPrice();
                // 计算所需总积分
                integral -= goods.getIntegral();
                // 重新渲染数据
                purchase_num.setText("" + count);
                goods_total_num.setText("" + count);
                goods_count.setText("共" + count + "件商品");
                purchase_total_money.setText("" + totalPrice);
                total_money.setText("" + totalPrice);
                break;

            case R.id.goods_num_add:            // 购买数量加一
                // 商品数量减一之后的数量
                count ++;
                // 计算总价：订单总价加上相应的单价
                totalPrice += goods.getPrice();
                // 计算所需总积分
                integral += goods.getIntegral();
                // 重新渲染数据
                purchase_num.setText(count + "");
                goods_total_num.setText("" + count);
                goods_count.setText("共" + count + "件商品");
                purchase_total_money.setText("" + totalPrice);
                total_money.setText("" + totalPrice);
                break;

            case R.id.submit_order:         // 提交订单
                // 从Sp中获取本地用户名
                String username = SpUtil.getString(getApplicationContext(), ConstantValue.IDENTIFIED_USER, null);
                // 将登陆成功的用户信息封装到User实体类中
                gson = new GsonBuilder().create();
                user = gson.fromJson(username, User.class);
                // 判断是否设置支付密码
                if (user.getPayPwd() == null) {
                    // 用户还未设置支付密码
                    toSetPaymentActivityDialog("您还未设置支付密码哦！", "传送门", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            Intent setPaymentIntent = new Intent(getApplicationContext(), SetPaymentActivity.class);
                            setPaymentIntent.putExtra("set_payment", user);
                            startActivityForResult(setPaymentIntent, 30000);
                        }
                    });
                } else {
                    showPaymentDialog("本次购买会消耗" + integral + "个积分，确认购买？", "取消", "确认购买", new View.OnClickListener() {
                        // 取消按钮
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            //这里写自定义处理XXX
                        }
                    }, new View.OnClickListener() {
                        // 确认按钮
                        @Override
                        public void onClick(View v) {
                            // 对话框消失
                            mDialog.dismiss();

                            // 检查用户剩余积分是否够用
                            if (user.getIntegral() == null) {
                                ToastUtil.show(getApplicationContext(), "同志，你的积分不够用啊！");
                            } else {
                                // 转成int类型
                                int userIntegral = Integer.parseInt(user.getIntegral());
                                // 积分够用，减去此次消耗的积分
                                int restIntegral = userIntegral - integral;
                                // 更新本地SP中的积分数据
                                user.setIntegral(restIntegral + "");
                                SpUtil.putString(getApplicationContext(), ConstantValue.IDENTIFIED_USER, gson.toJson(user));
                                // 通知服务端更新用户积分数据
                                updateIntegral(restIntegral, user.getId());
                            }
                        }
                    });
                }
                break;
        }
    }

    /**
     * 更新数据库中的用户积分数据
     * @param restIntegral
     */
    private void updateIntegral(int restIntegral, String userID) {
        // 封装请求数据
        User user = new User();
        user.setId(userID);
        user.setIntegral(restIntegral + "");

        HttpUtils.doPost(ConstantValue.UPDATE_INTEGRAL, new Gson().toJson(user), new Callback() {
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
                            // 提示用户商品购买成功
                            ToastUtil.show(getApplication(), "购买成功！");
//                            // 在返回AccountActivity的时候带回修改好的payment数据
//                            setResult(RESULT_OK, new Intent().putExtra("set_payment_ok", encoderPayment));
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
     * 显示单按钮对话框
     * @param alertText
     * @param btnText
     * @param onClickListener
     */
    private void toSetPaymentActivityDialog(String alertText, String btnText, View.OnClickListener onClickListener) {
        mDialog = builder.setMessage(alertText)
                .setSingleButton(btnText, onClickListener)
                .createSingleButtonDialog();
        mDialog.show();
    }

    private void showPaymentDialog(String alertText, String cancelText, String confirmText,
                                   View.OnClickListener cancelListener, View.OnClickListener conFirmListener) {
        mDialog = builder.setMessage(alertText)
                .setNegativeButton(cancelText, cancelListener)
                .setPositiveButton(confirmText, conFirmListener)
                .createTwoButtonDialog();
        mDialog.show();
    }

    /**
     * @param requestCode 确认返回的数据是从哪个Activity返回的
     * @param resultCode  由子Activity通过其setResult()方法返回
     * @param data        一个Intent对象，带有返回的数据。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AddressSuperviseActivity.ADDRESS_SUPERVISE:  // 接收地址管理activity中获取的地址数据
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    // 获取新增的Address Bean 数据
                    address = (Address) data.getSerializableExtra("address_detail");
                    // 渲染地址数据
                    tv_addressee.setText("收件人:" + address.getConsignee());   // 收件人
                    addressee_phone.setText(address.getPhone());    // 联系电话
                    String finalAddress = "收件地址：" + address.getProvince() + address.getDetail();
                    tv_address.setText(finalAddress);               // 收件详细地址
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
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("GoodsPurchase Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
