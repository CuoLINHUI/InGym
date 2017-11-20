package com.stefan.ingym.ui.activity.Community;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stefan.ingym.R;

public class GoodsDetailActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_goods_value)
    private TextView tv_goods_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ViewUtils.inject(this);

        init_toolbar();

        // 设置中划线并加清晰
        tv_goods_value.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);


    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.goods_detail_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

}
