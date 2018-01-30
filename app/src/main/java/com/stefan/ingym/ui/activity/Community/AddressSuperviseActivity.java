package com.stefan.ingym.ui.activity.Community;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.stefan.ingym.R;
import com.stefan.ingym.db.dao.AddressDao;
import com.stefan.ingym.pojo.mine.Address;
import com.stefan.ingym.ui.activity.Mine.AddAddressActivity;
import com.stefan.ingym.ui.activity.Mine.AddressManagementActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: AddressSuperviseActivity
 * @Description: 地址管理
 * @Author Stefan
 * @Date 2018/1/26 16:36
 */

public class AddressSuperviseActivity extends AppCompatActivity {
    public static final int ADDRESS_SUPERVISE = 6000;

    @ViewInject(R.id.address_supervise_list)
    private ListView address_supervise_list;

    private boolean mIsLoad = false; // 初值为false取反后，满足加载条件之一
    private AddressDao mDao;
    private List<Address> mAddressLists;
    private int mCount;
    private MyAdapter mAdapter;
    //    private User user;
    private Address newAddress;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 数据适配器对象没有的话new一个出来，已存在的话，直接去刷新一下界面就好了（避免多余的创建）
            if (mAdapter == null) {
                // 4、告知ListView可以去设置数据适配器
                mAdapter = new MyAdapter();
                // 最后将数据显示在ListView中
                address_supervise_list.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_supervise);
        ViewUtils.inject(this);
        initUI();
        initData();
    }

    private void initData() {
        // 获取数据库中的所有地址（耗时操作，开线程）
        new Thread() {
            @Override
            public void run() {
                mDao =  AddressDao.getInstance(getApplicationContext());
                mAddressLists = mDao.find(0);
                // 查询出数据库共有几条数据
                mCount = mDao.count();
                // 查询完毕得到数据集合，通过消息机制告知主线线程，可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @OnClick({R.id.manager_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manager_address:      // 进入地址管理
                Intent intent = new Intent(this, AddressManagementActivity.class);
                startActivity(intent);
                break;
        }

    }

    /**
     * 地址条目点击事件监听
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.address_supervise_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = (Address) mAdapter.getItem(position);
        setResult(RESULT_OK, new Intent().putExtra("address_detail", address));
        finish();
    }

    /**
     * toolbar初始化 + ListView条目的滚动状态的监听
     */
    private void initUI() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.address_management_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        // 滚动监听
        address_supervise_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 条目滚动状态发生改变的监听方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                /* 三种状态
				OnScrollListener.SCROLL_STATE_FLING				飞速滚动状态
			    OnScrollListener.SCROLL_STATE_IDLE				空闲状态（滚到底部了）
			    OnScrollListener.SCROLL_STATE_TOUCH_SCROLL		拿手触摸着去滚动的状态（收一直贴着屏幕拉条目）
                */
                // 集合非空
                if (mAddressLists != null) {
                    /*
					 * 下拉加载数据的条件：
					 * 条件一：滚动到停止状态
					 * 条件二：最后一个条目可见（最后一个条目的索引值(从1开始计数) >= 数据适配器中集合条目个数(从0开始计数) - 1）
					 */
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && address_supervise_list.getLastVisiblePosition() >= mAddressLists.size() -1
                            && !mIsLoad) {
                        // 需要数据库中条目的总数 > 集合大小的时候才可以去加载更多
                        if (mCount > mAddressLists.size()) {
                            // 加载下一页数据（耗时操作）
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    mDao = AddressDao.getInstance(getApplicationContext());
                                    ArrayList<Address> moreData = mDao.find(mAddressLists.size());
                                    // 添加下一页数据的过程
                                    mAddressLists.addAll(moreData);
                                    // 告知主线线程刷新显示一下UI
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }
            }

            // 条目滚动过程中的监听方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAddressLists.size();
        }

        @Override
        public Object getItem(int position) {
            return mAddressLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            /*
			 * 当ListView中需要展示的数据非常多的时候，需要对ListView进行优化
			 * 1、复用convertView
			 * 2、对findViewById次数的优化（用ViewHolder，要创建ViewHolder类，定义成静态static，使之不会去创建多个对象）
			 * 3、ListView如果有多个条目的时候，可以做一个分页算法，设定每次加载的条数，逆序返回
			 */
            // 复用ViewHolder 步骤一：
            ViewHolder holder = null;
            // 复用convertView
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.address_supervise_item, null);
                // 将findViewById放到convertView == null的时候去调用优化
                // 复用ViewHolder 步骤三：
                holder = new ViewHolder();
                // 复用ViewHolder 步骤四：
                holder.tv_addressee = (TextView) convertView.findViewById(R.id.tv_addressee);
                holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
                holder.addressee_phone = (TextView) convertView.findViewById(R.id.addressee_phone);
                // 复用ViewHolder 步骤五：
                convertView.setTag(holder);
            } else {
                // 已经有了的话直接拿出来
                holder = (ViewHolder) convertView.getTag();
            }
            // 设置收件人名称
            holder.tv_addressee.setText(mAddressLists.get(position).getConsignee());
            // 设置收件人联系方式
            holder.addressee_phone.setText(mAddressLists.get(position).getPhone());
            // 设置收件人地址
            String finalAddress = mAddressLists.get(position).getProvince() + mAddressLists.get(position).getDetail();
            holder.tv_address.setText(finalAddress);

            return convertView;
        }
    }

    /**
     *	复用ViewHolder 步骤二：
     *	定义成static,不会去创建多个对象
     */
    static class ViewHolder {
        TextView tv_addressee;
        TextView tv_address;
        TextView addressee_phone;
    }

}
