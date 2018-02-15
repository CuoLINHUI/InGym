package com.stefan.ingym.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.adapter.community.MyPagerAdapter;
import com.stefan.ingym.ui.activity.MainActivity;
import com.stefan.ingym.ui.fragment.community.MomentsFragment;
import com.stefan.ingym.ui.fragment.community.EquipmentFragment;
import com.stefan.ingym.ui.fragment.community.live.LiveListFragment;
import com.stefan.ingym.ui.fragment.community.post.PostMomentsActivity;
import com.stefan.ingym.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;

/**
 * @ClassName: FragmentCommunity
 * @Description: 应用程序“社区”模块
 * @Author Stefan
 * @Date 2017/9/22 15:50
 */
public class FragmentCommunity extends Fragment {

    @ViewInject(R.id.vp_community)          // 选项卡的ViewPage控件
    private ViewPager mViewPager;
    @ViewInject(R.id.coordinatortablayout)  // 选项卡控件
    private CoordinatorTabLayout mCoordinatorTabLayout;

    private int[] mImageArray, mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"发现", "装备", "直播"};

    public FragmentCommunity() {
    }

    // 布局文件的view
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件 Inflate the layout for this fragment
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_community, null);
        // 注入方法
        ViewUtils.inject(this, view);
        // 调用初始化Fragment的方法
        initFragments();
        // 调用初始化ViewPage的方法
        initViewPager();
        // 调用初始化选项卡的相关数据的方法
        initTabData();

        return view;
    }

    /**
     * 判断是否为要显示的界面，防止显示的界面内容重叠
     */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE:View.GONE);
    }

    /**
     * 初始化选项卡的相关数据
     */
    private void initTabData() {
        mImageArray = new int[]{
                R.mipmap.pic_discover,
                R.mipmap.pic_equipment,
                R.mipmap.pic_live,
        };

        mColorArray = new int[]{
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
        };
        // 为选项卡控件设置相关属性
        mCoordinatorTabLayout.setTransulcentStatusBar(getActivity())
                .setTitle("")      // 设置标题
                .setBackEnable(false)       // 是否设置返回箭头
                .setImageArray(mImageArray, mColorArray)
                .setupWithViewPager(mViewPager);
    }

    /**
     * 初始化子Fragment
     */
    private void initFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(new MomentsFragment());
        mFragments.add(new EquipmentFragment());
        mFragments.add(new LiveListFragment());
    }

    /**
     * 初始化ViewPage
     */
    private void initViewPager() {
        // 设置选项卡的数目为3
        mViewPager.setOffscreenPageLimit(3);
        // getChildFragmentManager()很关键，不然子Fragment会被父Fragment覆盖
        mViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), mFragments, mTitles));
    }

    @OnClick({R.id.post_moments})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_moments:
                Intent intent = new Intent(getActivity(), PostMomentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("limit", 9 );//number是选择图片的数量(设置图片选择数量)
                startActivityForResult(intent, 0);
                break;
        }
    }

    private static final String TAG = "FragmentCommunity";



    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 0:
//                if (data != null) {
//                    //path是选择拍照或者图片的地址数组
//                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");
//                    // 获取moment content
//                    String momentContent = data.getStringExtra("moment_content");
//
//                    Log.i(TAG, "onActivityResult: " + "图片路径：" + paths);
//                    Log.i(TAG, "onActivityResult: " + "内容：" + momentContent);
//
//                    ToastUtil.show(getActivity(), "图片路径：" + paths + "内容：" + momentContent);
//
//                    //处理代码
//
//                }
//                break;
//            default:
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}
