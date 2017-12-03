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

public class MoreNutritionActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_more_nutrition)      // ToolBar Title
    private TextView tv_more_nutrition;
    @ViewInject(R.id.tv_weight)              // food weight
    private TextView tv_weight;
    @ViewInject(R.id.tv_cal)                 // food calorie
    private TextView tv_cal;
    @ViewInject(R.id.tv_fat)                 // food fat
    private TextView tv_fat;
    @ViewInject(R.id.tv_carbohydrate)        // food carbohydrate
    private TextView tv_carbohydrate;
    @ViewInject(R.id.tv_protein)             // food protein
    private TextView tv_protein;
    @ViewInject(R.id.tv_cellulose)           // food cellulose
    private TextView tv_cellulose;

    private Foods foodNutrition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_nutrition);
        ViewUtils.inject(this);
        init_toolbar();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) foodNutrition = (Foods) bundle.get("more_nutrition");

        /**
         * 数据渲染
         */
        // 标题
        tv_more_nutrition.setText(foodNutrition.getFood_name());
        tv_weight.setText("每" + foodNutrition.getFood_weight() + "克");
        tv_cal.setText(foodNutrition.getFood_calorie() + "大卡");
        tv_fat.setText(foodNutrition.getFood_fat() + "克");
        tv_carbohydrate.setText(foodNutrition.getFood_carbohydrate() + "克");
        tv_protein.setText(foodNutrition.getFood_protein() + "克");
        tv_cellulose.setText(foodNutrition.getFood_cellulose() + "克");
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.more_nutrition_toolbar);
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
