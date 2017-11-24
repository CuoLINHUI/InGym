package com.stefan.ingym.adapter.index;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Foods;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * @ClassName: FoodsViewAdapter
 * @Description: 食物数据适配器
 * @Author Stefan
 * @Date 2017/11/22 21:44
 */

public class FoodsViewAdapter extends BGAAdapterViewAdapter<Foods> {

    private String keyword;
    private Context context;

    public FoodsViewAdapter(Context context, String key_word) {
        super(context, com.stefan.ingym.R.layout.hi_food_item);
        this.context = context;
        this.keyword = key_word;
    }

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, Foods model) {
        // 毕加索框架加载网络图片，若加载失败显示默认图片
        Picasso.with(context).load(model.getFood_pic())
                .placeholder(R.drawable.default_pic).into(viewHolderHelper.getImageView(R.id.iv_food_pic));

        // 获取数据库中的食物全称
        String foodName = model.getFood_name();

        /**
         * 渲染条目数据
         */
        // 在所搜结果中标红搜索关键字
        if (foodName != null && foodName.contains(keyword)) {
            int index = foodName.indexOf(keyword);
            int len = keyword.length();
            Spanned temp = Html.fromHtml(foodName.substring(0, index)
                    + "<font color=#FF0000>"
                    + foodName.substring(index, index + len) + "</font>"
                    + foodName.substring(index + len, foodName.length()));
            viewHolderHelper.setText(R.id.food_title, temp);
        } else {
            viewHolderHelper.setText(R.id.food_title, foodName);
        }
        // 渲染上其余数据
        viewHolderHelper
                .setText(R.id.food_calorie, model.getFood_calorie())
                .setText(R.id.food_weight, model.getFood_weight());
    }

}
