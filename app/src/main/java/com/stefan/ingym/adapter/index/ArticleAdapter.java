/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */
package com.stefan.ingym.adapter.index;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.stefan.ingym.R;
import com.stefan.ingym.pojo.index.Article;

import java.util.List;

/**
 * @ClassName: ArticleAdapter
 * @Description: 资讯数据适配器
 * @Author Stefan
 * @Date 2017/10/29 21:37
 */
public class ArticleAdapter extends BaseAdapter{

    private List<Article> mList = null;

    public ArticleAdapter(List<Article> articles) {
        this.mList = articles;
    }

    public void addData(List<Article> articles){
        mList.addAll(articles);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mList != null ? mList.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return (mList != null && mList.size() > position) ? mList.get(position - 1) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hi_article_item, null);
            holder = new ViewHolder();
            // 使用框架为控件绑定id
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // 得到集合中的位置
        Article articles = mList.get(position);
        // 使用Picasso毕加索框架先获得context对象，然后加载一个图片url路径，设置没获取到图片加载默认图片，最后将获取的图片注入到id中
        Picasso.with(parent.getContext()).load(articles.getPictureURL()).placeholder(R.drawable.default_pic).into(holder.article_picture);

        Log.i("ArticleAdapter", "图片路径： " + articles.getPictureURL());

        /**
         * 将数据渲染到界面上
         */
        holder.article_title.setText(articles.getTitle());                  // 渲染资讯标题数据
        holder.title_description.setText(articles.getTitle_description());  // 渲染资讯标题描述数据
        holder.agree_number.setText(articles.getAgree_number());            // 渲染资讯获得同意次数数据
        holder.comments_number.setText(articles.getComments_number());      // 渲染资讯被评论次数数据
        holder.browse_times.setText(articles.getBrowse_times());            // 渲染资讯浏览次数数据

        return convertView;
    }

    /**
     * 对布局进行优化处理， 需要加载的布局，得声明里面的控件
     */
    class ViewHolder {
        // 向外框架注入
        @ViewInject(R.id.iv_article_pic)            // 资讯条目图片
        private ImageView article_picture;
        @ViewInject(R.id.tv_title)              // 资讯标题
        private TextView article_title;
        @ViewInject(R.id.tv_description)        // 标题描述
        private TextView title_description;
        @ViewInject(R.id.tv_agree)              // 赞同人数
        private TextView agree_number;
        @ViewInject(R.id.tv_comments)           // 评论人数
        private TextView comments_number;
        @ViewInject(R.id.tv_browse)             // 浏览数
        private TextView browse_times;
    }


}
