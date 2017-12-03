package com.stefan.ingym.ui.activity.index;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Foods;

import static android.R.attr.data;
import static com.stefan.ingym.R.id.food_calorie;
import static com.stefan.ingym.R.id.food_carbohydrate;
import static com.stefan.ingym.R.id.food_fat;
import static com.stefan.ingym.R.id.food_kcal;
import static com.stefan.ingym.R.id.food_name;
import static com.stefan.ingym.R.id.food_protein;
import static com.stefan.ingym.R.id.food_recommended_detail;
import static com.stefan.ingym.R.id.food_recommended_type;
import static com.stefan.ingym.R.id.food_weight;
import static com.stefan.ingym.R.id.iv_food_pic;

/**
 * @ClassName: CookingActivity
 * @Description: 食物的原料及做法
 * @Author Stefan
 * @Date 2017/12/3 19:56
 */

public class CookingActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_food_cooking)            // 食物名称（也是ToolBar标题）
    private TextView tv_food_cooking;
    @ViewInject(R.id.wv_food_cooking_detail)     // 详细做菜法
    private WebView wv_food_cooking_detail;

    private Foods food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking);
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
            tv_food_cooking.setText(food.getFood_name());
            WebSettings cookingSetting = wv_food_cooking_detail.getSettings();
            // 把所有内容放大WebView等宽的一列中
            cookingSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS.SINGLE_COLUMN);
            //向webview加载数据
            String[] cookingData = htmlSub(food.getFood_cooking_detail());
            wv_food_cooking_detail.loadDataWithBaseURL("", cookingData[0], "text/html", "utf-8", "");
        }
    }

    /**
     *从一大段的景点描述中截取出本单详情和温馨提示
     * @param html 一大段的景点描述
     */
    public String[] htmlSub(String html){
        char[] str=html.toCharArray();
        int len=str.length;
        System.out.println(len);
        int n=0;
        String[] data=new String[3];
        int oneindex=0;
        int secindex=1;
        int thrindex=2;
        for(int i=0;i<len;i++){
            if(str[i]=='【'){
                n++;
                if(n==1)oneindex=i;
                if(n==2)secindex=i;
                if(n==3)thrindex=i;
            }
        }
        if(oneindex>0&&secindex>1&&thrindex>2){
            data[0]=html.substring(oneindex, secindex);
            data[1]=html.substring(secindex, thrindex);
            data[2]=html.substring(thrindex, len-6);

        }
        return data;
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.food_cooking_toolbar);
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
