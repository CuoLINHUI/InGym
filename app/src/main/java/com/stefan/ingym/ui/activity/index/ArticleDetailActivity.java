package com.stefan.ingym.ui.activity.index;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Article;

/**
 * @ClassName: ArticleDetailActivity
 * @Description: 资讯详情
 * @Author Stefan
 * @Date 2017/11/6 14:23
 */
public class ArticleDetailActivity extends AppCompatActivity {

    @ViewInject(R.id.article_title)
    private TextView title;

    private Article article;

//    private String articleID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ViewUtils.inject(this);

        // 接收FragmentIndex传递过来的参数
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) article = (Article) bundle.get("article_id");

        // 传递过来的条目ID不为空，渲染数据
        if (article != null) {
            title.setText(article.getTitle_description());
        }

    }


}
