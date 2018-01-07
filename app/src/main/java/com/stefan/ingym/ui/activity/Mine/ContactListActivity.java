package com.stefan.ingym.ui.activity.Mine;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stefan.ingym.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: ContactListActivity
 * @Description: 选择联系人
 * @Author Stefan
 * @Date 2017/12/29 21:24
 */

public class ContactListActivity extends AppCompatActivity {

    @ViewInject(R.id.lv_contact_list)
    private ListView lv_contact_list;

    private ContactAdapter mAdapter;

    private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

    // 创建消息机制
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 填充数据适配器
            mAdapter = new ContactAdapter();
            // 最后设置给ListView显示
            lv_contact_list.setAdapter(mAdapter);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ViewUtils.inject(this);
        init_toolbar();
        initUI();
        initData();
    }

    private void initData() {
        // 读取联系人的过程中，当联系人数量很多的时候将会是一个耗时操作，所以这里将其处理过程放入子线程
        new Thread() {
            public void run() {
                // 1、获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                // 2、做查询系统联系人数据库表的过程（这里需要读取联系人权限）
                Cursor cursor = contentResolver.query(
                        Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null, null, null);

                // 在把数据装入List前先清空一下容器,避免出现数据重复的问题
                contactList.clear();

                // 3、循环游标，知道没有数据为止
                while (cursor.moveToNext()) {
                    // columIndex:列的索引
                    String id = cursor.getString(0);	// 查询的字段只有一个，所以为0
//					Log.i(tag, "id = " + id);	// 打印测试，测试是否能查询到联系人ID
                    // 4、根据用户唯一性id值，查询data表和mimetype表生成的视图，获取data以及mimetype字段
                    // projection:映射    selectionArgs:选择的参数	Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
                    Cursor indexCursor = contentResolver.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?", new String[]{id}, null);

                    // 获取出了联系人及其电话号码之后要将其数据封装到HashMap中去
                    HashMap<String, String> hashMap = new HashMap<String, String>();	// 当Map中的数据准备号之后要放到List中去

                    // 5、循环获取每一个联系人的电话号码以及姓名
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);

                        // 根据mimetype来判断数据类型
                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            // 匹配成功上面那一段则为电话号码数据
                            // 进行非空判断在放入hashMap中
                            if (!TextUtils.isEmpty(type)) {
                                hashMap.put("phone", data);
                            }
                        } else if(type.equals("vnd.android.cursor.item/name")) {
                            // 为联系人名字
                            // 进行非空判断在放入hashMap中
                            if (!TextUtils.isEmpty(type)) {
                                hashMap.put("name", data);
                            }
                        }
                    }
                    // 游标用完关闭
                    indexCursor.close();
                    // 循环遍历完一个id下的数据之后将读取到的数据放进hashMap
                    contactList.add(hashMap);
                }
                // 游标用完之后要关闭
                cursor.close();
                // 全部id下的数据遍历完成之后，发送消息机制，通知主线程，集合准备完毕，赶紧去填充数据适配器
                mHandler.sendEmptyMessage(0);	// 发送一个空的消息即可

            };

        }.start();
    }

    /**
     * 获取联系人数据的的方法
     */
    private void initUI() {
        lv_contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1、获取用户点中的条目的索引所指向的集合中的对象
                if (mAdapter != null) {
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    // 2、获取当前条目指向集合对应的电话号码
                    String phone = hashMap.get("phone");
                    // 获取到的电话号码需要传给Setup3Activity.class导航界面使用
                    // 所以在结束词界面返回到Setup3Activity.class导航界面的时候，需要带回数据
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);	// （带回的参数名，参数的数据）
                    setResult(0,intent);
                    // 完成所有操作后结束当前界面
                    finish();
                }
            }
        });
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.add_contact_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            tv_contact_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return view;
        }
    }

}
