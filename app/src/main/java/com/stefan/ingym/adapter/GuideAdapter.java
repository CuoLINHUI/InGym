/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */
package com.stefan.ingym.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @ClassName: GuideAdapter
 * @Description: 引导界面ViewPager的适配器，继承 PagerAdapter
 * @Author Stefan
 * @Date 2017/9/21 17:11
 */
public class GuideAdapter extends PagerAdapter {

    private List<View> mList;

    public GuideAdapter(List<View> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * 判断视图view是否为Object类型
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 这里进行回收，当我们左右滑动的时候，会把早期的图片回收掉.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mList.get(position));
    }

    /**
     * 初始化添加ViewPager每一页的布局
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mList.get(position), position);
        return mList.get(position);
    }

}
