package com.stefan.ingym.ui.activity.Community;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;
import static android.R.attr.id;

public class GoodsDetailActivity extends AppCompatActivity {

    @ViewInject(R.id.goods_image)           // 顶部商品展示图片
    private ImageView goods_image;
    @ViewInject(R.id.goods_title)           // 商品标题
    private TextView goods_title;
    @ViewInject(R.id.goods_desc)            // 商品描述
    private TextView goods_desc;
    @ViewInject(R.id.tv_goods_value)        // 商品原价
    private TextView tv_goods_value;
    @ViewInject(R.id.goods_price)           // 商品现价
    private TextView goods_price;
    @ViewInject(R.id.product_payments)      // 商品销量
    private TextView product_payments;
    @ViewInject(R.id.goods_detail_web_view) // 商品详情
    private WebView goods_view;
    @ViewInject(R.id.goods_collection)      // 收藏按钮
    private Button goods_collection;

    private Goods goods;

    private String unselectedStr = "收藏";
    private String selectedStr = "已收藏";

    // 定义保存在SP中商品是否被收藏信息的结点名称
    private String goodsSpName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ViewUtils.inject(this);
        init_toolbar();
        initData();
    }

    private void initData() {
        // 获取当前选中商品的数据
        Bundle bundle = getIntent().getExtras();
        goods = (Goods) bundle.get("product_item");
        if (goods != null) {
            /**
             * 数据渲染
             */
            Picasso.with(getApplicationContext()).load(goods.getBigPicURL())
                    .placeholder(R.drawable.default_pic).into(goods_image);
            goods_title.setText(goods.getTitle());
            goods_desc.setText(goods.getSort_title());
            goods_price.setText("" + goods.getPrice());
            tv_goods_value.setText("" + goods.getValue());
            // 设置商品原价的划线并加清晰显示
            tv_goods_value.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
            product_payments.setText("月销" + goods.getPayments() + "笔");
            // 向WebView加载商品详情数据
            WebSettings goodSettings = goods_view.getSettings();
            goodSettings.setJavaScriptEnabled(true);    // 支持JS脚本
            goodSettings.setBuiltInZoomControls(true);  // 支持显示缩放按钮
            goodSettings.setSupportZoom(true);          // 支持缩放
            // 把所有内容放大WebView等宽的一列中
            goodSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            // 加载数据
            goods_view.loadDataWithBaseURL("", goods.getDetails(), "text/html", "utf-8", "");
        }

        // 定义保存在SP中商品是否被收藏信息的结点名称
        goodsSpName = "商品" + goods.getId() + "号";
        // 设置默认商品收藏的标记为false
        goods_collection.setTag(false);
        // 若该节点没有值，则返回默认值false（该商品未收藏）
        boolean is_collection = SpUtil.getBoolean(getApplicationContext(), goodsSpName, false);
        // 更新商品收藏的标记
        goods_collection.setTag(is_collection);
        // 判断该商品是否被收藏
        if(is_collection) {     // 显示收藏状态
            // 设置上端初始图片为选中状态
            Drawable drawable = getResources().getDrawable(R.mipmap.goods_collection_selected);
            // x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的高度
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            goods_collection.setCompoundDrawables(null, drawable, null, null);
            // 设置显示的文字为：已收藏
            goods_collection.setText(selectedStr);
        }

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.goods_detail_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    @OnClick({R.id.goods_collection, R.id.add_to_cart, R.id.purchase_immediately})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goods_collection:         // 点击收藏
                // 从Sp中获取保存在本地的用户数据 TODO: 2018/1/7
                String identify_user = SpUtil.getString(getApplicationContext(), ConstantValue.IDENTIFIED_USER, null);
                // 保存在本地的用户信息封装到User实体类中
                Gson gson = new GsonBuilder().create();
                User user = gson.fromJson(identify_user, User.class);
                // 获取用户名ID
                String userID = user.getId();
                // 获取商品ID
                String goodsID = goods.getId();

                // 获取按钮当前状态
                boolean flag = (boolean) goods_collection.getTag();
                if(!flag) {     // 点击收藏
//                    ToastUtil.show(this, "已收藏");
                    // 设置上端初始图片为选中状态
                    Drawable drawable = getResources().getDrawable(R.mipmap.goods_collection_selected);
                    // x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的高度
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    goods_collection.setCompoundDrawables(null, drawable, null, null);
                    // 设置显示的文字为：已收藏
                    goods_collection.setText(selectedStr);
                    // 将标记取反
                    goods_collection.setTag(true);

                    // 将收藏请求提交到数据库
                    goodsCollection(userID, goodsID);
                } else {       // 点击取消收藏
//                    ToastUtil.show(this, "未收藏");
                    // 设置上端初始图片为未选中状态状态
                    Drawable drawable = getResources().getDrawable(R.mipmap.goods_collection_unselected);
                    // x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的高度
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    goods_collection.setCompoundDrawables(null, drawable, null, null);
                    // 设置显示的文字为：收藏（未收藏）
                    goods_collection.setText(unselectedStr);
                    // 将标记取反
                    goods_collection.setTag(false);

                    // 将取消收藏请求提交到数据库
                    goodsCollectionCancel(userID, goodsID);
                }
                break;

            case R.id.add_to_cart:              // 加入购物车

                break;

            case R.id.purchase_immediately:     // 立即购买
                Intent intent = new Intent(this, GoodsPurchaseActivity.class);
                intent.putExtra("goods_info", goods);
                startActivity(intent);
                break;

        }
    }



    /**
     * 向服务器端发送用户请求收藏商品的方法
     * @param userID
     * @param goodsID
     */
    private void goodsCollection(final String userID, final String goodsID) {

        // 封装请求参数
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("user_id", String.valueOf(userID));
                put("goods_id", String.valueOf(goodsID));
            }
        };

        HttpUtils.doGet(ConstantValue.COLLECTION_GOODS, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getApplicationContext(), "商品收藏失败！请检查网络！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<String> object = gson.fromJson(
                        json,
                        new TypeToken<ResponseObject<String>>(){}.getType()
                );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (object.getState() == 1) {
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        } else {
                            // 提示服务器端返回错误的信息
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        }
                    }
                });
                // 将该商品被收藏的信息保存在Sp中（true）
                SpUtil.putBoolean(getApplicationContext(), goodsSpName, true);

            }
        });

    }

    /**
     * 向服务器端发送用户请求取消收藏商品的方法
     * @param userID
     * @param goodsID
     */
    private void goodsCollectionCancel(final String userID, final String goodsID) {
        // 封装请求参数
        Map<String, String> params = new HashMap<String, String>() {
            {
                put("user_id", String.valueOf(userID));
                put("goods_id", String.valueOf(goodsID));
            }
        };

        HttpUtils.doGet(ConstantValue.COLLECTION_CANCEL, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getApplicationContext(), "商品取消收藏失败！请检查网络！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();
                String json = response.body().string();
                final ResponseObject<String> object = gson.fromJson(
                        json,
                        new TypeToken<ResponseObject<String>>(){}.getType()
                );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (object.getState() == 1) {
                            ToastUtil.show(getApplicationContext(), object.getMsg());

                        } else {
                            // 提示服务器端返回错误的信息
                            ToastUtil.show(getApplicationContext(), object.getMsg());
                        }
                    }
                });
                // 将该商品被取消收藏的信息保存在Sp中（false）
                SpUtil.putBoolean(getApplicationContext(), goodsSpName, false);
            }
        });
    }

}
