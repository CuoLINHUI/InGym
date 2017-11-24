package com.stefan.ingym.adapter.index;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Article;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * @ClassName: ArticleViewAdapter
 * @Description: 资讯数据适配器
 * @Author Stefan
 * @Date 2017/11/6 16:17
 */
public class ArticleViewAdapter extends BGAAdapterViewAdapter<Article> {
    /**
     * 当前处于打开状态的item
     */
//    private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();
    private Context context;
    public ArticleViewAdapter(Context context) {
        super(context, com.stefan.ingym.R.layout.hi_article_item);
        this.context = context;
    }

/*    @Override
    public void addNewData(List<Article> data) {
        super.addNewData(data);
    }

    @Override
    public void addMoreData(List<Article> data) {
        super.addMoreData(data);
    }*/

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, Article model) {
        // fixme 把model.get***url 加载成 bitmap，然后setImageResource -> setImageBitmap
        /**
         *  Bitmap bitmap = ImageLoader.load(url)
         */
        /*Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(context).load(model.getPictureURL()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // fixme 判断bitmap是空
        Picasso.with(context).load(model.getPictureURL())
                .placeholder(R.drawable.default_pic).into(viewHolderHelper.getImageView(R.id.iv_article_pic));

        viewHolderHelper
                .setText(R.id.tv_title, model.getTitle())
                .setText(R.id.tv_agree, model.getAgree_number() + "赞同")
                .setText(R.id.tv_comments, model.getComments_number() + "评论")
                .setText(R.id.tv_browse, model.getBrowse_times() + "")
                .setText(R.id.tv_description, model.getTitle_description());

    }


}