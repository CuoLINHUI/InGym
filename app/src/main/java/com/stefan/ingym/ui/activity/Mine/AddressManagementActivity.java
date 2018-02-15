package com.stefan.ingym.ui.activity.Mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.db.dao.AddressDao;
import com.stefan.ingym.pojo.mine.Address;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.SpUtil;

import java.util.ArrayList;
import java.util.List;

import static com.stefan.ingym.R.id.set_default_address;
import static com.stefan.ingym.R.id.tv_address;
import static com.stefan.ingym.util.ConstantValue.ADDRESS_POSITION;

/**
 * @ClassName: AddressManagementActivity
 * @Description: 我的地址
 * @Author Stefan
 * @Date 2017/12/26 21:52
 */

public class AddressManagementActivity extends AppCompatActivity {

    public static final int ADDRESS_MANAGEMENT = 50000;

    @ViewInject(R.id.address_list)
    private ListView address_list;

    private boolean mIsLoad = false; // 初值为false取反后，满足加载条件之一
    private AddressDao mDao;
    private List<Address> mAddressLists;
    private int mCount;
    private MyAdapter mAdapter;
    private Address newAddress;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 数据适配器对象没有的话new一个出来，已存在的话，直接去刷新一下界面就好了（避免多余的创建）
            if (mAdapter == null) {
                // 4、告知ListView可以去设置数据适配器
                mAdapter = new MyAdapter();
                // 最后将数据显示在ListView中
                address_list.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);
        ViewUtils.inject(this);
        init_toolbar();
        initData();
    }

    private void initData() {
        // 设置ListView条目间距
        address_list.setDividerHeight(10);

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

    @OnClick({R.id.ll_add_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_add_address:       // 添加新地址
                Intent addAddressIntent = new Intent(this, AddAddressActivity.class);
//                addAddressIntent.putExtra("add_address", user);
                startActivityForResult(addAddressIntent, 60000);
                break;

        }
    }

    /**
     * toolbar初始化 + ListView条目的滚动状态的监听
     */
    private void init_toolbar() {
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
        address_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                            && address_list.getLastVisiblePosition() >= mAddressLists.size() -1
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

    /**
     * @param requestCode   确认返回的数据是从哪个Activity返回的
     * @param resultCode    由子Activity通过其setResult()方法返回
     * @param data          一个Intent对象，带有返回的数据。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        } else {
            // 获取新增的Address Bean 数据
            newAddress = (Address) data.getSerializableExtra("add_address");
//            Log.i("新增的地址：", newAddress.getConsignee());
            // 手动将新添加的数据插入到集合的最顶部
            mAddressLists.add(0, newAddress);
            // 通知主线程更新UI，显示新增的地址信息
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
        }
    }

    private void aa() {
        mAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter {

//        private int index = 1;

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
                convertView = View.inflate(getApplicationContext(), R.layout.address_lists_item, null);
                // 将findViewById放到convertView == null的时候去调用优化
                // 复用ViewHolder 步骤三：
                holder = new ViewHolder();
                // 复用ViewHolder 步骤四：
                holder.tv_addressee = (TextView) convertView.findViewById(R.id.tv_addressee);
                holder.tv_address = (TextView) convertView.findViewById(tv_address);
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

            holder.edit_address = (LinearLayout) convertView.findViewById(R.id.edit_address);
            // 点击进入相应的地址详情（修改）
            holder.edit_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mAddressLists.get(position)包含了所有数据
                    Address itemAddress = mAddressLists.get(position);
//                    ToastUtil.show(getApplicationContext(), mAddressLists.get(position) + "");
                    // 跳转到修改页面
                    Intent addressUpdate = new Intent(getApplicationContext(), AddressUpdateActivity.class);
                    addressUpdate.putExtra("address_update", itemAddress);
                    startActivityForResult(addressUpdate, 001);
                }
            });

            holder.delete_address = (LinearLayout) convertView.findViewById(R.id.delete_address);
            // 点击删除该条目（删除）
            holder.delete_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 弹出对话框，提示用户是否确定删除该条记录
                    showDeleteDialog(position);
                }
            });

            // set_default_address
            holder.set_default_address = (ImageView)convertView.findViewById(set_default_address);
            holder.tv_set_default = (TextView) convertView.findViewById(R.id.tv_set_default);
            // 设置图片状态
            int index = SpUtil.getInt(getApplicationContext(), ConstantValue.ADDRESS_POSITION, -1);
            if (index != -1) {
                if (position == index) {
                    holder.set_default_address.setImageResource(R.mipmap.address_default_set);
                    holder.tv_set_default.setText("默认地址");
                    holder.tv_set_default.setTextColor(getResources().getColor(R.color.address_set_color));
                } else {
                    holder.set_default_address.setImageResource(R.mipmap.address_default_unset);
                    holder.tv_set_default.setText("设为默认");
                    holder.tv_set_default.setTextColor(getResources().getColor(R.color.address_default_color));
                }
            }

            // 设为默认地址
            holder.set_default_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index = position;
                    SpUtil.putInt(getApplicationContext(), ADDRESS_POSITION, index);

                    // 获取当前选中条目地址数据
                    Address addressData = mAddressLists.get(position);
                    // 转成gson字符串保存到Sp
                    Gson gson = new GsonBuilder().create();
                    SpUtil.putString(getApplicationContext(), ConstantValue.DEFAULT_ADDRESS, gson.toJson(addressData));

                    mAdapter.notifyDataSetChanged();

                }
            });

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
        LinearLayout edit_address;
        LinearLayout delete_address;
        ImageView set_default_address;
        TextView tv_set_default;
    }

    /**
     * 对话框：是否删除该地址
     * @param position
     */
    protected void showDeleteDialog(final int position) {
        // 对话框是依赖于activity存在的!!!!!!!!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 这里如果把this换成getApplicationContext()就会报错！
        // 设置对话框标题
        builder.setTitle("提示");
        //设置对话框标题
        builder.setMessage("确定删除此地址？");
        // 设定对话框“确定”按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            // 用户选择了“确定”按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 1、删除数据库中的该条记录
                mDao.delete(mAddressLists.get(position).getId());
                // 2、删除数据集合中的该条记录
                mAddressLists.remove(position);
                // 3、通知数据适配器刷新UI
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
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
