package com.stefan.ingym.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.stefan.ingym.R;
import com.stefan.ingym.adapter.index.ArticleViewAdapter;
import com.stefan.ingym.custom.IOnSearchClickListener;
import com.stefan.ingym.pojo.ResponseObject;
import com.stefan.ingym.pojo.index.Article;
import com.stefan.ingym.ui.activity.index.ArticleDetailActivity;
import com.stefan.ingym.util.ConstantValue;
import com.stefan.ingym.util.HttpUtils;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.refreshlayout.BGAMeiTuanRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @ClassName: FragmentIndex
 * @Description: 应用程序“健身”模块，程序进入后的主显示界面
 * @Author Stefan
 * @Date 2017/9/22 15:37
 */
public class FragmentIndex extends Fragment implements Toolbar.OnMenuItemClickListener, IOnSearchClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    // FragmentIndex布局文件的view
    private View view;

    // 图片轮播布局文件的view
    private View carouselView;
    /**
     * 图片轮播的相关全局变量开始
     */
    @ViewInject(R.id.vp_carousel)	// 找到图片轮播控件
    private ViewPager mViewPaper;
    @ViewInject(R.id.tv_title)		// 找到图片轮播的图片标题控件
    private TextView tv_title;

    private List<ImageView> images; //创建集合用来存储要显示的图片
    private List<View> dots;        // 创建集合用来存放，图片下方显示的小点
    private int currentItem;
    private int oldPosition = 0;	// 记录上一次点的位置
    private int[] imageIds;			// 创建数组，存放图片的id
    private String[] titles;		// 创建数组，存放图片下方的标题文字
    private ViewPagerAdapter adapter;
    private ScheduledExecutorService scheduledExecutorService;
    private Handler os = new Handler(Looper.getMainLooper());

    private void runOnUIThread(Runnable runnable){
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            runnable.run();
        }
        os.post(runnable);
    }

    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            // 为图片轮播设置当前Item
            mViewPaper.setCurrentItem(currentItem);

            // 为资讯数据适配器添加数据
            mAdapter.addNewData(mList);
            // 通知资讯数据适配器刷新UI
            mAdapter.notifyDataSetChanged();

        }
    };
    /**
     * 图片轮播的相关全局变量结束
     */

    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;

    private SearchFragment searchFragment;

    // 下拉刷新上拉加载更多控件
    @ViewInject(R.id.rl_modulename_refresh)
    private BGARefreshLayout mRefreshLayout;

    // 当前页码，获取多少条数据， 多少页
    private int page = 1, size = 10, pageCount = 1;

    // 定义资讯集合
    private List<Article> mList = null;

    // 定义资讯数据适配器
    private ArticleViewAdapter mAdapter;

    // 资讯item
    @ViewInject(R.id.lv_article_item)
    private ListView articleListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 加载FragmentIndex的布局文件 Inflate the layout for this fragment
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_index, null);

        // 加载图片轮播布局文件
        carouselView = LayoutInflater.from(getActivity()).inflate(R.layout.picture_carousel, null);

        // 注入方法（系统在启动的时候可以将xml的控件的属性赋值给相应控件,通过反射和泛型来实现的，代替了findViewById）
        ViewUtils.inject(this, view);
        ViewUtils.inject(this, carouselView);

        // 调用初始化图片轮播相关数据的方法
        initPicData();

        // 初始化ToolBar的相关数据
        initToolBar();

        // 一定要加上这句才可使Fragment中的onCreateOptionsMenu生效！！！
        setHasOptionsMenu(true);

        // 初始化下拉刷新，下拉加载更多控件
        initRefreshLayout();

        mList = new ArrayList<>();

        mAdapter = new ArticleViewAdapter(getContext());

        // 为资讯item设置上数据适配器
        articleListView.setAdapter(mAdapter);

        // 返回布局文件
        return view;
    }

    /**
     * 资讯条目点击事件监听
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.lv_article_item)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.show(getActivity(), "你选中的资讯ID为： " + mList.get(position).getId());
        ToastUtil.show(getActivity(), "你选中的资讯标题为： " + mList.get(position).getTitle());

        Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
        intent.putExtra("article_id", mAdapter.getItem(position)); // 传递被选中的资讯ID到ArticleDetailActivity
        startActivity(intent);

    }

    /*
	 * 判断是否为要显示的界面，防止显示的界面内容重叠
	 */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        System.out.println("FragmentIndex - menuVisible" + menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE:View.GONE);
    }

    /**
     * 图片轮播的相关代码开始
     */
    /**
     * 初始化图片轮播的相关数据
     */
    private void initPicData() {
        // 创建数组，存放图片的id
        imageIds = new int[]{
                R.mipmap.banner_man,
                R.mipmap.banner_girl,
                R.mipmap.banner_fruit,
                R.mipmap.banner_woman,
                R.mipmap.banner_dumbbel
        };

        // 存放图片下方的标题文字
        titles = new String[]{
                // 加入到真实的项目中要把这些文字写在string.xml文件中，然后在外部引用这些资源
                "If you think you are beaten you are",
                "If you think you dare you don't",
                "It's almost a fact you won't...",
                "Success begins with a fellows will...",
                "You've got to be sure of yourself before you can ever win the prize."
        };

        // 创建集合用来存储要显示的图片
        images = new ArrayList<ImageView>();
        for(int i = 0; i < imageIds.length; i++){
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }

        // 创建集合用来存放，图片下方显示的小点
        dots = new ArrayList<View>();
        // 添加小点到集合中
        dots.add(carouselView.findViewById(R.id.dot_0));
        dots.add(carouselView.findViewById(R.id.dot_1));
        dots.add(carouselView.findViewById(R.id.dot_2));
        dots.add(carouselView.findViewById(R.id.dot_3));
        dots.add(carouselView.findViewById(R.id.dot_4));

        // 将维护好的图片标题展示到相关控件上
        tv_title.setText(titles[0]);

        // 创建ViewPager数据适配器
        adapter = new ViewPagerAdapter();
        // 将维护好的数据适配器添加到ViewPager控件中去
        mViewPaper.setAdapter(adapter);
        // 设置ViewPager控件的页面变换监听
        mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tv_title.setText(titles[position]);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                // 记录上一次点的位置
                oldPosition = position;
                // 记录当前点的位置
                currentItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });
    }

    /**
     * FragmentIndex中图片轮播的数据适配器，继承PagerAdapter
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(images.get(position));
            return images.get(position);
        }

    }

    /**
     * 利用线程池定时执行动画轮播
     */
    @Override
    public void onStart() {
        super.onStart();
        // 只有一个线程，用来调度执行将来的任务
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		/* 创建并执行一个在给定初始延迟后首次启用的定期操作
			   command - 要执行的任务
	    	   initialdelay - 首次执行的延迟时间
	    	   delay - 一次执行终止和下一次执行开始之间的延迟
	    	   unit - initialdelay 和 delay 参数的时间单位
		 */
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                2,
                3,
                TimeUnit.SECONDS);
    }

    private class ViewPageTask implements Runnable{
        @Override
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    /**
     * 图片轮播的相关代码结束
     */


    /**
     * 实例化ToolBar相关数据
     */
    private void initToolBar() {
        // 设置ToolBar的标题为空！
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        // 调用SearchFragment中的实例化方法
        searchFragment = SearchFragment.newInstance();
        // 为ToolBar设置点击监听事件
        toolbar.setOnMenuItemClickListener(this);
        // 调用SearchFragment中的搜索方法
        searchFragment.setOnSearchClickListener(this);
    }

    /**
     * 该方法用于加载菜单文件（这里的菜单就是ToolBar右边的搜索功能）
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //加载菜单文件
        // menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        System.out.println("FragmentIndex - onCreateOptionsMenu");

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search://点击搜索
                searchFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), SearchFragment.TAG);
                break;
        }
        return true;
    }


    /**
     * 点击搜索按钮之后执行操作的方法
     * @param keyword   搜索关键词
     */
    @Override
    public void OnSearchClick(String keyword) {
//        searchInfo.setText(keyword);    // 显示搜索关键词
    }


    /**
     * 下拉刷新上拉加载更多控件相关数据开始
     */
    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGAMeiTuanRefreshViewHolder refreshViewHolder = new BGAMeiTuanRefreshViewHolder(getActivity(), true);
        refreshViewHolder.setPullDownImageResource(R.mipmap.bga_refresh_mt_pull_down);
        refreshViewHolder.setChangeToReleaseRefreshAnimResId(R.drawable.bga_refresh_mt_change_to_release_refresh);
        refreshViewHolder.setRefreshingAnimResId(R.drawable.bga_refresh_mt_refreshing);

        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时不显示加载更多控件
        // mRefreshLayout.setIsShowLoadingMoreView(false);
        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("正在加载...");
        // 设置整个加载更多控件的背景颜色资源 id
        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.colorAccent);
        // 设置整个加载更多控件的背景 drawable 资源 id
        refreshViewHolder.setLoadMoreBackgroundDrawableRes(R.drawable.bga_refresh_loding);
        // 设置下拉刷新控件的背景颜色资源 id
//        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.colorAccent);
        // 设置下拉刷新控件的背景 drawable 资源 id
//        refreshViewHolder.setRefreshViewBackgroundDrawableRes(R.drawable.bga_refresh_loding);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        mRefreshLayout.setCustomHeaderView(carouselView, true);
        // 可选配置  -------------END

        // TODO 进入主界面就进行首次自动加载数据操作
		new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
                mRefreshLayout.beginRefreshing();
				return true;
			}
		}).sendEmptyMessageDelayed(0, 0);

    }

   /* private List<Article> mockData(int page, int size){
        List<Article> articles = new ArrayList<>();
        for(int i = 0; i < 10; ++i){
            Article article = new Article();
            article.setId(page * size + i + "");
            article.setAgree_number(new Random().nextInt());
            article.setBrowse_times(new Random().nextInt());
            article.setComments_number(new Random().nextInt());
            article.setDetail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            article.setPictureURL("http://img2.imgtn.bdimg.com/it/u=3962587452,4101692640&fm=27&gp=0.jpg");
            article.setTitle("title:" + i);
            article.setTitle_description("miaoshushsusushusus");
            articles.add(article);
        }
        return articles;
    }*/

    /**
     * 加载最新数据
     * @param refreshLayout
     */
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

/*
        // 请求数据
        mList = mockData(page, size);
        // 添加数据给适配器
        mAdapter.addNewData(mList);     */

//        mRefreshLayout.endRefreshing();

        // 请求参数
        final Map<String, String> params = new HashMap<String ,String>(){
            {
                put("page", String.valueOf(page));
                put("size", String.valueOf(size));
            }
        };

        new Thread() {
            @Override
            public void run() {
                // 向服务端发送请求（请求方法，维护的访问路径，需要传递的参数，返回值）
                HttpUtils.doGet(ConstantValue.HI_ARTICLE, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 无论请求成功或者失败都要求ListView控件复位，变回原来的状态
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.endRefreshing();
                            }
                        });
                        Looper.prepare();
                        // 提示用户数据请求失败
                        ToastUtil.show(getActivity(), "抱歉，数据请求失败,请检查网络~");
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        /**
                         *  解析服务器端返回过来的结果
                         */
                        Gson gson = new GsonBuilder().create();
                        // 得到响应体
                        String json = response.body().string();
                        // 通过fromJson方法将json数据转化成实体类,用于解析
                        ResponseObject<List<Article>> object = gson.fromJson(json, new TypeToken<ResponseObject<List<Article>>>() {
                        }.getType());

                        // 下拉刷新，表示mList重新加载数据，但总数据不变
                        mList = object.getDatas();
//                        mAdapter.addNewData(mList);
                        // 无论请求成功或者失败都要求ListView控件复位，变回原来的状态
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.endRefreshing();
                            }
                        });
                    }
                });

            }
        }.start();

    }

    /**
     * 上拉加载更多
     * @param refreshLayout
     * @return
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // 在这里加载更多数据，或者更具产品需求实现上拉刷新也可以

//        if (mIsNetworkEnabled) {
//            // 如果网络可用，则异步加载网络数据，并返回 true，显示正在加载更多
//            new AsyncTask<Void, Void, Void>() {
//
//                @Override
//                protected Void doInBackground(Void... params) {
//                    try {
//                        Thread.sleep(MainActivity.LOADING_DURATION);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    // 加载完毕后在 UI 线程结束加载更多
//                    mRefreshLayout.endLoadingMore();
//                    mAdapter.addDatas(DataEngine.loadMoreData());
//                }
//            }.execute();
//
//            return true;
//        } else {
//            // 网络不可用，返回 false，不显示正在加载更多
//            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    // 通过代码方式控制进入正在刷新状态。应用场景：某些应用在 activity 的 onStart 方法中调用，自动进入正在刷新状态获取最新数据
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    // 通过代码方式控制进入加载更多状态
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
    }
    /**
     * 下拉刷新上拉加载更多控件相关数据结束
     */

}
