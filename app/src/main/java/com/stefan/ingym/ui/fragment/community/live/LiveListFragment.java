package com.stefan.ingym.ui.fragment.community.live;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.community.live.activity.LiveActivity;
import com.stefan.ingym.ui.fragment.community.live.activity.LiveWatchActivity;
import com.stefan.ingym.ui.fragment.community.live.adapter.LiveListAdapter;

import java.util.ArrayList;

/**
 * @ClassName: LiveListFragment
 * @Description: “社区”模块之“直播”部分入口文件
 * @Author Stefan
 * @Date 2017/9/27 10:28
 */
public class LiveListFragment extends Fragment {

    // 布局文件的view
    private View view;

    @ViewInject(R.id.live_list)         // 直播item控件
    private RecyclerView liveList;

    private Context context;
    // 直播条目数据适配器
    private LiveListAdapter liveListAdapter = null;

    public LiveListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_live_list, null);
        // 框架注入
        ViewUtils.inject(this, view);
        // 调用界面初始化方法
        initUI();

        return view;
    }

    /**
     * 设置用户点击监听事件
     */
    @OnClick({R.id.fab_to_live})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_to_live:  // 用户点击了开启直播按钮
                Intent intent = new Intent(getActivity(), LiveActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 界面初始化方法
     */
    private void initUI() {
        // 得到上下文
        context = getActivity();
        // LinearLayout布局为垂直
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        // 设置直播描述数组
        final ArrayList<String> list = new ArrayList<String>();
        list.add("施瓦辛格正在直播");
        list.add("史泰龙正在直播");
        list.add("巨石强森正在直播");

        liveListAdapter = new LiveListAdapter(context, list, new LiveListAdapter.OnLiveClickListener() {
            @Override
            public void onLiveClickListener(final int position) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String playUrl = "rtmp://live.soyask.top/701studio/javasdktest1489147454866b";
                        Intent intent = new Intent(getActivity(), LiveWatchActivity.class);
                        intent.putExtra("play_url", playUrl);
                        startActivity(intent);
                    }
                }).start();
            }
        });

        liveList.setLayoutManager(manager);
        liveList.setAdapter(liveListAdapter);
    }

}