package com.stefan.ingym.ui.activity.index;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lidroid.xutils.ViewUtils;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Article;

/**
 * @ClassName: ArticleDetailActivity
 * @Description: 资讯详情
 * @Author Stefan
 * @Date 2017/11/6 14:23
 */
public class ArticleDetailActivity extends AppCompatActivity {

//    @ViewInject(R.id.article_title)
//    private TextView title;

    private Article article;

//    private String articleID = null;

    private String URL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ViewUtils.inject(this);

        init_toolbar();

        // 接收FragmentIndex传递过来的参数
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) article = (Article) bundle.get("article_id");

        // 传递过来的条目ID不为空，渲染数据
        if (article != null) {
//            title.setText(article.getTitle_description());
            URL = article.getDetail();

        }

        WebView wView = (WebView)findViewById(R.id.wv_arcicle);
        WebSettings wSet = wView.getSettings();
        wSet.setJavaScriptEnabled(true);
        wView.loadUrl(URL);
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.article_toolbar);
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
