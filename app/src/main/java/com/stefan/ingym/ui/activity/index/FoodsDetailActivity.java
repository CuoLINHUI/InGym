package com.stefan.ingym.ui.activity.index;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Foods;

import static com.stefan.ingym.R.id.ll_cuisine;

public class FoodsDetailActivity extends AppCompatActivity {

    @ViewInject(R.id.iv_food_pic)               // 食物图片
    private ImageView iv_food_pic;
    @ViewInject(R.id.food_name)                 // 食物名称
    private TextView food_name;
    @ViewInject(R.id.food_calorie)              // 食物每100g的卡路里
    private TextView food_calorie;
    @ViewInject(R.id.food_weight)               // 食物质量
    private TextView food_weight;
    @ViewInject(R.id.food_kcal)                 // 食物每100g的卡路里
    private TextView food_kcal;
    @ViewInject(R.id.food_protein)              // 食物每100g的蛋白质含量
    private TextView food_protein;
    @ViewInject(R.id.food_carbohydrate)         // 食物每100g的碳水化合物含量
    private TextView food_carbohydrate;
    @ViewInject(R.id.food_fat)                  // 食物每100g的脂肪含量
    private TextView food_fat;
    @ViewInject(R.id.food_recommended_type)     // 食物推荐类型
    private TextView food_recommended_type;
    @ViewInject(R.id.food_recommended_detail)   // 食物推荐详情
    private TextView food_recommended_detail;
    @ViewInject(R.id.tv_food_cuisine)           // 菜品条目文字
    private TextView tv_food_cuisine;
    @ViewInject(R.id.ll_materials_practices)    // 食物菜菜品做法条目
    private LinearLayout ll_materials_practices;

    private Foods food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_detail);
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
        // 不为空，则渲染数据
        if (food != null) {
            Picasso.with(getApplicationContext()).load(food.getFood_pic())
                    .placeholder(R.drawable.default_pic).into(iv_food_pic);
            food_name.setText(food.getFood_name());
            food_calorie.setText(food.getFood_calorie());
            food_weight.setText(food.getFood_weight());
            food_kcal.setText(food.getFood_calorie());
            food_protein.setText(food.getFood_protein());
            food_carbohydrate.setText(food.getFood_carbohydrate());
            food_fat.setText(food.getFood_fat());
            food_recommended_type.setText(food.getFood_recommended_types());
            food_recommended_detail.setText(food.getFood_detail());
            tv_food_cuisine.setText(food.getFood_cuisine());
            // 若查询出的菜品做法为空的话，则在该食物详情页面隐藏该条目
            if (food.getFood_cooking_detail() == null) ll_materials_practices.setVisibility(View.GONE);
        }
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.food_detail_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    @OnClick({R.id.more_nutrition, ll_cuisine, R.id.ll_materials_practices})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.more_nutrition:           // 更多营养元素
                Intent intent = new Intent(this, MoreNutritionActivity.class);
                intent.putExtra("more_nutrition", food);
                startActivity(intent);
                break;
            case ll_cuisine:                    // 食物分类
                Intent cuisineIntent = new Intent(this, CuisineActivity.class);
                cuisineIntent.putExtra("food_id", food);
                startActivity(cuisineIntent);
                break;
            case R.id.ll_materials_practices:   // 食物的烹饪方法
                Intent methodIntent = new Intent(this, CookingActivity.class);
                methodIntent.putExtra("food_id", food);
                startActivity(methodIntent);
                break;
        }
    }

}

