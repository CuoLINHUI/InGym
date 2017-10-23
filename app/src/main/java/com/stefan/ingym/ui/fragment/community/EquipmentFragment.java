package com.stefan.ingym.ui.fragment.community;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.activity.Community.GoodsListActivity;
import com.stefan.ingym.adapter.community.EquipmentAdapter;
import com.stefan.ingym.util.ToastUtil;

/**
 * @ClassName: EquipmentFragment
 * @Description: “社区”模块之“装备”部分
 * @Author Stefan
 * @Date 2017/9/27 10:26
 */
public class EquipmentFragment extends Fragment {

    // 布局文件的View
    private View view;

    // 九宫格控件
    @ViewInject(R.id.equipment_grid_view)
    private GridView equipment_grid_view;

    public EquipmentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_equipment, null);
        // 注入方法
        ViewUtils.inject(this, view);

        // 为九宫格控件设置上数据适配器
        equipment_grid_view.setAdapter(new EquipmentAdapter());

        return view;
    }

    @OnItemClick({R.id.equipment_grid_view})
    public void OnItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(getActivity(), "你选中了第" + position + "项");

        // 根据用户选中的宫格，将其对应的分类ID传入GoodsListActivity，并开启该activity
        Intent intent = new Intent(getActivity(), GoodsListActivity.class);
        intent.putExtra("category", ((EquipmentAdapter)parent.getAdapter()).getItem(position));
        startActivity(intent);

        ToastUtil.show(getActivity(), "类别为：" + ((EquipmentAdapter)parent.getAdapter()).getItem(position));

    }



}
