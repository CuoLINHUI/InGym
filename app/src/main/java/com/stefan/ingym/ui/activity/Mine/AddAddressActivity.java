package com.stefan.ingym.ui.activity.Mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.db.dao.AddressDao;
import com.stefan.ingym.pojo.mine.Address;
import com.stefan.ingym.pojo.mine.User;
import com.stefan.ingym.ui.activity.MainActivity;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;

import java.util.ArrayList;

import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.BottomDialog;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.City;
import chihane.jdaddressselector.model.County;
import chihane.jdaddressselector.model.Province;
import chihane.jdaddressselector.model.Street;
import mlxy.utils.T;

import static android.R.attr.phoneNumber;
import static com.stefan.ingym.R.id.et_consignee;
import static com.stefan.ingym.R.id.et_username;

/**
 * @ClassName: AddAddressActivity
 * @Description: 新增地址
 * @Author Stefan
 * @Date 2017/12/27 19:58
 */

public class AddAddressActivity extends AppCompatActivity implements OnAddressSelectedListener {

    public static final int ADD_ADDRESS = 60000;

    @ViewInject(R.id.et_consignee)
    private EditText et_consignee;
    @ViewInject(R.id.et_contact_phone)
    private EditText et_contact_phone;
    @ViewInject(R.id.tv_city)
    private TextView tv_city;
    @ViewInject(R.id.address_detail)
    private EditText address_detail;

//    private User user;
    private AddressDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ViewUtils.inject(this);
        initUI();
        init_toolbar();
    }

    /**
     * 数据初始化
     */
    private void initUI() {
        // 获取AccountActivity传递过来的数据
//        Bundle bundle = getIntent().getExtras();
//        user = (User) bundle.get("add_address");

        // 城市选择器初始化
        AddressSelector selector = new AddressSelector(this);
        selector.setOnAddressSelectedListener(this);

        // 一进入该页面就先去Sp中读取一下有无用户已经设置好的电话号码，展示在【联系电话】条目中
        String phoneNumber = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        et_contact_phone.setText(phoneNumber);

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.add_address_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();

            }
        });
    }

    /**
     * 创建询问用户是否确定退出当前用户的对话框
     */
    protected void showExitDialog() {
        // 对话框是依赖于activity存在的!!!!!!!!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 这里如果把this换成getApplicationContext()就会报错！
        // 设置对话框标题
        builder.setTitle("确定要放弃此次编辑");
        //设置对话框标题
//        builder.setMessage("");
        // 设定对话框“确定”按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            // 用户选择了“确定”按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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

    @OnClick({R.id.contact_list, R.id.ll_city_selector, R.id.save_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_list:       // 添加联系人号码
                Intent addContactIntent = new Intent(this, ContactListActivity.class);
                // 开启有返回值的Activity（返回该界面的联系人选择结果）
                startActivityForResult(addContactIntent, 0);
                break;

            case R.id.ll_city_selector:     // 地址选择
                BottomDialog dialog = new BottomDialog(this);
                dialog.setOnAddressSelectedListener(this);
                dialog.show();
                break;

            case R.id.save_address:         // 保存新建
                // 获取收件人名
                String consignee = et_consignee.getText().toString().trim();
                // 获取收件人联系方式
                String phone = et_contact_phone.getText().toString().trim();
                // 获取省市地址
                String address = tv_city.getText().toString().trim();
                // 获取详细地址
                String addressDetail = address_detail.getText().toString().trim();
                // 判断输入不为空
                if (TextUtils.isEmpty(consignee)) {
                    ToastUtil.show(this, "收件人不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.show(this, "收件人联系方式不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.show(this, "收件省市不能为空，请选择！");
                    return;
                }
                if (TextUtils.isEmpty(addressDetail)) {
                    ToastUtil.show(this, "收件详细地址不能为空，请输入！");
                    return;
                }

                // 向本地数据库插入数据
                insertLocal(consignee, phone, address, addressDetail);

                break;
        }
    }

    /**
     * 向本地数据库插入一条地址数据
     * @param consignee
     * @param phone
     * @param address
     * @param addressDetail
     */
    private void insertLocal(String consignee, String phone, String address, String addressDetail) {
        mDao = AddressDao.getInstance(getApplicationContext());
        // 插入数据到本地数据库
        mDao.insert(consignee, phone, address, addressDetail);
        // 将新增的地址数据添加到Address Bean 中
        Address addressBean = new Address();
        addressBean.setConsignee(consignee);
        addressBean.setPhone(phone);
        addressBean.setProvince(address);
        addressBean.setDetail(addressDetail);
        // 带回新增数据到AddressManagementActivity.java（前提：要将Address.java实例化）
        Intent intent = new Intent();
        intent.putExtra("add_address", addressBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 地址选择完毕调用
     * @param province
     * @param city
     * @param county
     * @param street
     */
    @Override
    public void onAddressSelected(Province province, City city, County county, Street street) {
        String toast =
                (province == null ? "" : province.name) +
                        (city == null ? "" : "\n" + city.name) +
                        (county == null ? "" : "\n" + county.name) +
                        (street == null ? "" : "\n" + street.name);
        // 吐司显示选择完毕的地址
        T.showShort(this, toast);

        String address =
                (province == null ? "" : province.name) +
                        (city == null ? "" : city.name) +
                        (county == null ? "" : county.name) +
                        (street == null ? "" : street.name);
        // 为条目设置上用户选择完毕的地址
        tv_city.setText(address);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 在进行相关处理之前，先进行data是否为空的判断（用户进入联系人选择界面之后没有进行选择就退出，这时data为空）
        if (data != null) {
            // 拿到返回过来的数据（电话号码）
            String phone = data.getStringExtra("phone");
            // 拿到的格式可能有： 180-0000-0000 或者 170 0000 0000 等这样的格式，所以要将特殊字符过滤（中划线、空格都转成空字符串）
            phone = phone.replace("-", "").replace(" ", "").trim();
            // 将整理号的电话号码set进相关的显示控件中去
            et_contact_phone.setText(phone);

            // 将用户选中的联系人存储到Sp中去(用于回显)
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
