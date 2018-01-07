package com.stefan.ingym.ui.activity.Mine;

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
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.SpUtil;
import com.stefan.ingym.util.ToastUtil;

import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.BottomDialog;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.City;
import chihane.jdaddressselector.model.County;
import chihane.jdaddressselector.model.Province;
import chihane.jdaddressselector.model.Street;
import mlxy.utils.T;

/**
 * @ClassName: AddressUpdateActivity
 * @Description: 用户地址修改及查看
 * @Author Stefan
 * @Date 2018/1/6 14:47
 */

public class AddressUpdateActivity extends AppCompatActivity implements OnAddressSelectedListener {

    @ViewInject(R.id.et_consignee)
    private EditText et_consignee;
    @ViewInject(R.id.et_contact_phone)
    private EditText et_contact_phone;
    @ViewInject(R.id.tv_city)
    private TextView tv_city;
    @ViewInject(R.id.address_detail)
    private EditText address_detail;

    private Address address;
    private AddressDao mDao;
    String itemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_update);
        ViewUtils.inject(this);
        init_toolbar();
        initUI();

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.update_address_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.modify_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    private void initUI() {
        // 城市选择器初始化
        AddressSelector selector = new AddressSelector(this);
        selector.setOnAddressSelectedListener(this);

        // 获取AddressManagementActivity传递过来的数据
        Bundle bundle = getIntent().getExtras();
        // 获取被点击的条目地址数据
        address = (Address) bundle.get("address_update");
        /**
         * 数据渲染
         */
        // 获取该条目ID
        itemID = address.getId();
        et_consignee.setText(address.getConsignee());   // 收件人
        et_contact_phone.setText(address.getPhone());   // 收件人联系电话
        tv_city.setText(address.getProvince());         // 省市地址
        address_detail.setText(address.getDetail());    // 详细地址
    }

    @OnClick({R.id.contact_list, R.id.ll_city_selector, R.id.update_address})
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

            case R.id.update_address:         // 保存新建
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
                updateLocalAddress(consignee, phone, address, addressDetail);

                break;
        }
    }

    /**
     * 向本地数据库更新该地址数据
     * @param consignee
     * @param phone
     * @param address
     * @param addressDetail
     */
    private void updateLocalAddress(String consignee, String phone, String address, String addressDetail) {
        mDao = AddressDao.getInstance(getApplicationContext());
        // 插入数据到本地数据库
        mDao.update(itemID, consignee, phone, address, addressDetail);
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
