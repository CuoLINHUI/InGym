package com.stefan.ingym.ui.activity.Community;

import android.content.Intent;
import android.net.Uri;
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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.community.Goods;
import com.stefan.ingym.pojo.mine.Address;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.SpUtil;

import static java.lang.Integer.parseInt;

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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.goods_purchase);
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
                int purchase_num_sub = Integer.parseInt(purchase_num.getText().toString().trim()) - 1;
                // 商品价格减去相应的单价
                double priceSub =
                    Double.parseDouble(total_money.getText().toString().trim()) - goods.getPrice();
                purchase_num.setText("" + purchase_num_sub);
                goods_total_num.setText("" + purchase_num_sub);
                goods_count.setText("共" + purchase_num_sub + "件商品");
                purchase_total_money.setText("" + priceSub);
                total_money.setText("" + priceSub);

                break;

            case R.id.goods_num_add:            // 购买数量加一
                // 商品数量减一之后的数量
                int purchase_num_add = Integer.parseInt(purchase_num.getText().toString().trim()) + 1;
                // 商品价格减去相应的单价
                double priceAdd =
                        Double.parseDouble(total_money.getText().toString().trim()) + goods.getPrice();
                // 重新设置上数据
                purchase_num.setText("" + purchase_num_add);
                goods_total_num.setText("" + purchase_num_add);
                goods_count.setText("共" + purchase_num_add + "件商品");
                purchase_total_money.setText("" + priceAdd);
                total_money.setText("" + priceAdd);
                break;

            case R.id.submit_order:         // 提交订单
                // FIXME: 2018/1/23
                break;

        }
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
