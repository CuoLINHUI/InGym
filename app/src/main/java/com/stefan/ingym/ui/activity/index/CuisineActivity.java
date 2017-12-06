package com.stefan.ingym.ui.activity.index;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Foods;

/**
 * @ClassName: CuisineActivity
 * @Description: 菜系条目
 * @Author Stefan
 * @Date 2017/12/6 15:16
 */

public class CuisineActivity extends AppCompatActivity {

    @ViewInject(R.id.food_cuisine_title)
    private TextView food_cuisine_title;

    private Foods food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);
        ViewUtils.inject(this);
        init_toolbar();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) food = (Foods) bundle.get("food_id");
        // 加上标题
        if (food != null) food_cuisine_title.setText(food.getFood_cuisine());

    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.food_cuisine_toolbar);
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
