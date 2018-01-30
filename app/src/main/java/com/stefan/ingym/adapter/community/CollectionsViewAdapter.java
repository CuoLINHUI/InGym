package com.stefan.ingym.adapter.community;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.community.Collections;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * @ClassName: CollectionsViewAdapter
 * @Description: 渲染商品收藏条目的数据适配器
 * @Author Stefan
 * @Date 2017/11/6 16:21
 */
public class CollectionsViewAdapter extends BGAAdapterViewAdapter<Collections>{

    private Context context;

    public CollectionsViewAdapter(Context context) {
        super(context, R.layout.equipment_goods_item);
        this.context = context;
    }

    @Override
    protected void fillData(BGAViewHolderHelper viewHolderHelper, int position, Collections model) {
        Picasso.with(context).load(model.getGoods().getImageURL())
                .placeholder(R.drawable.default_pic).into(viewHolderHelper.getImageView(R.id.iv_goods_pic));

        // 商品条目的数据渲染
        viewHolderHelper
                .setText(R.id.tv_title, model.getGoods().getTitle())                   // 标题
                .setText(R.id.tv_description, model.getGoods().getSort_title())        // 描述
                .setText(R.id.goods_price, "" + model.getGoods().getPrice())                // 价格
                .setText(R.id.goods_payments, "" +model.getGoods().getPayments());         // 购买人数

    }

}
