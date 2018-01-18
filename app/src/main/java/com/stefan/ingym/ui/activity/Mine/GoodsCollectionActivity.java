package com.stefan.ingym.ui.activity.Mine;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.stefan.ingym.R;
import com.stefan.ingym.adapter.community.CollectionsViewAdapter;
import com.stefan.ingym.pojo.ResponseObject;
import com.stefan.ingym.pojo.community.Collections;
import com.stefan.ingym.ui.activity.Community.GoodsDetailActivity;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.HttpUtils;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGAMeiTuanRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * @ClassName: GoodsCollectionActivity
 * @Description: 查看商品收藏
 * @Author Stefan
 * @Date 2018/1/17 20:36
 */

public class GoodsCollectionActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    // 下拉刷新上拉加载更多控件
    @ViewInject(R.id.goods_collection_refresh)
    private BGARefreshLayout mRefreshLayout;
    // 内空间ListView
    @ViewInject(R.id.goods_collection_item)
    private ListView goodsListView;

    // 当前页码，获取多少条数据， 多少页
    private int page = 1, size = 10, pageCount = 1;

    // 定义商品数据适配器
    private CollectionsViewAdapter mAdapter;

    // 定义装备商品集合
    private List<Collections> mList = null;

    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(android.os.Message msg) {
            // 为资讯数据适配器添加数据
            mAdapter.addNewData(mList);
            // 通知资讯数据适配器刷新UI
            mAdapter.notifyDataSetChanged();

        }
    };

    private void runOnUIThread(Runnable runnable){
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            runnable.run();
        }
        mHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_collection);
        ViewUtils.inject(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        /*
         * toolbar初始化
         */
        Toolbar mToolbar = (Toolbar) findViewById(R.id.goods_collection_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        // 初始化下拉刷新，下拉加载更多控件
        initRefreshLayout();
        // 创建装备商品集合
        mList = new ArrayList<>();
        mAdapter = new CollectionsViewAdapter(getApplicationContext());
        // 为资讯item设置上数据适配器
        goodsListView.setAdapter(mAdapter);
    }

    /**
     * 商品条目点击事件监听
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.goods_collection_item)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, GoodsDetailActivity.class);
        // 传递被选中的商品所有数据到GoodsDetailActivity（前提Goods实体类要实现序列化接口）
        intent.putExtra("product_item", mAdapter.getItem(position).getGoods());
        startActivity(intent);
    }

    /**
     * 下拉刷新上拉加载更多控件相关数据开始
     */
    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGAMeiTuanRefreshViewHolder refreshViewHolder = new BGAMeiTuanRefreshViewHolder(GoodsCollectionActivity.this, true);
        refreshViewHolder.setPullDownImageResource(R.mipmap.bga_refresh_mt_pull_down);
        refreshViewHolder.setChangeToReleaseRefreshAnimResId(R.drawable.bga_refresh_mt_change_to_release_refresh);
        refreshViewHolder.setRefreshingAnimResId(R.drawable.bga_refresh_mt_refreshing);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("正在加载...");
        // 设置整个加载更多控件的背景颜色资源 id
        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.colorAccent);
        // 设置整个加载更多控件的背景 drawable 资源 id
        refreshViewHolder.setLoadMoreBackgroundDrawableRes(R.drawable.bga_refresh_loding);

        // TODO 进入主界面就进行首次自动加载数据操作
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                mRefreshLayout.beginRefreshing();
                return true;
            }
        }).sendEmptyMessageDelayed(0, 0);
    }

    /**
     * 加载最新数据
     * @param refreshLayout
     */
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        // 请求参数
        final Map<String, String> params = new HashMap<String ,String>(){
            {
                put("page", String.valueOf(page));
                put("size", String.valueOf(size));
            }
        };

        new Thread() {
            @Override
            public void run() {
                // 向服务端发送请求（请求方法，维护的访问路径，需要传递的参数，返回值）
                HttpUtils.doGet(ConstantValue.LOAD_COLLECTION, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                // 结束刷新动画
                                mRefreshLayout.endRefreshing();
                                // 提示用户数据请求失败
                                ToastUtil.show(getApplication(), "抱歉，数据请求失败,请检查网络~");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        /**
                         *  解析服务器端返回过来的结果
                         */
                        Gson gson = new GsonBuilder().create();
                        // 得到响应体
                        String json = response.body().string();
                        // 通过fromJson方法将json数据转化成实体类,用于解析
                        ResponseObject<List<Collections>> object = gson.fromJson(
                                json,
                                new TypeToken<ResponseObject<List<Collections>>>() {}.getType()
                        );
                        // 获得商品结果集
                        mList = object.getDatas();
                        // 为数据适配器设置上结果集数据
//                        mAdapter.addNewData(mList);

                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.endRefreshing();
                            }
                        });

                        // 通知刷新UI
                        mHandler.sendEmptyMessage(0);

                    }
                });
            }
        }.start();

    }

    /**
     * 上拉加载更多
     * @param refreshLayout
     * @return
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // 在这里加载更多数据，或者更具产品需求实现上拉刷新也可以

// TODO


        return true;
    }

    // 通过代码方式控制进入正在刷新状态。应用场景：某些应用在 activity 的 onStart 方法中调用，自动进入正在刷新状态获取最新数据
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    // 通过代码方式控制进入加载更多状态
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
    }
    /**
     * 下拉刷新上拉加载更多控件相关数据结束
     */

}
