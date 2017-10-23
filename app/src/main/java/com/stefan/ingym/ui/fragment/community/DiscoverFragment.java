package com.stefan.ingym.ui.fragment.community;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stefan.ingym.R;

/**
 * @ClassName: DiscoverFragment
 * @Description: “社区”模块之“发现”部分
 * @Author Stefan
 * @Date 2017/9/27 10:17
 */

public class DiscoverFragment extends Fragment {

    //  布局文件的View
    private View view;

    public DiscoverFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 载入布局文件
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover, null);

        return view;
    }

}
