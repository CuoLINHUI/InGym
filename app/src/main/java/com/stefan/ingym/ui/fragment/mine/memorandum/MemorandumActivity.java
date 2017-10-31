package com.stefan.ingym.ui.fragment.mine.memorandum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.mine.memorandum.activity.CreateActivity;
import com.stefan.ingym.ui.fragment.mine.memorandum.activity.FilesActivity;
import com.stefan.ingym.ui.fragment.mine.memorandum.activity.QuickCreateActivity;
import com.stefan.ingym.ui.fragment.mine.memorandum.adapter.MainSwipeAdapter;
import com.stefan.ingym.ui.fragment.mine.memorandum.manager.DBManager;
import com.stefan.ingym.ui.fragment.mine.memorandum.manager.NoteManager;
import com.stefan.ingym.ui.fragment.mine.memorandum.model.Note;
import com.stefan.ingym.ui.fragment.mine.memorandum.util.ComparatorUtil;
import com.stefan.ingym.ui.fragment.mine.memorandum.view.MainCreator;
import com.stefan.ingym.ui.fragment.mine.memorandum.view.MainScrollview;
import com.stefan.ingym.ui.fragment.mine.memorandum.view.SwipeListView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @ClassName: MemorandumActivity
 * @Description: 备忘录入口
 * @Author Stefan
 * @Date 2017/10/14 21:10
 */
public class MemorandumActivity extends AppCompatActivity {

    // 定义Note数组集合
    private ArrayList<Note> mData;
    //初始化
    private String currentFolderName ="Notes";
    //Note管理类
    private NoteManager noteManager;
    // 左滑控件
    private SwipeListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorandum);
        // 注入框架
        ViewUtils.inject(this);
        // 初始化Toolbar
        init_toolbar();
        // 调用初始化UI方法
        initUI();
    }

    /**
     * toolbar初始化
     */
    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.memorandum_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }

    /**
     * 主界面的初始化
     */
    public void initUI(){
        listView_setting();
        // 调用FAB按钮相关设置的方法
        fab_setting();
    }

    /**
     * onResume()是当该activity与用户能进行交互时被执行，用户可以获得activity的焦点，能够与用户交互时调用。
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 调用listView设置方法
        listView_setting();
        findViewById(R.id.action_menu).bringToFront();
    }

    /**
     * onStart()是activity界面被显示出来的时候执行的，用户可见，包括有一个activity在他上面，
     * 但没有将它完全覆盖，用户可以看到部分activity但不能与它交互。
     */
    protected void onStart(){
        super.onStart();
        // 调用listView设置方法
        listView_setting();
        findViewById(R.id.action_menu).bringToFront();
    }

    /**
     * 用户点击事件的监听
     */
    @OnClick({R.id.tv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:    // 用户点击了返回按钮
                finish();
                break;
        }
    }

    /**
     *
     */
    public void listView_setting(){
        // 调用关闭FAB菜单方法
        hide_fabMenu();
        // 调用数据库管理类中的search方法
        mData = new DBManager(this).search(currentFolderName);

        Collections.sort(mData, new ComparatorUtil());
        // 将Note数组集合设置到主菜单适配器中
        MainSwipeAdapter adapter = new MainSwipeAdapter(this, mData);

        noteManager = new NoteManager(this, currentFolderName, mData, adapter);

        MainCreator mainCreator = new MainCreator(this);

        mListView = (SwipeListView) findViewById(R.id.list_view);
        mListView.setMenuCreator(mainCreator);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        mListView.setAdapter(adapter);

        MainScrollview scrollview = (MainScrollview) findViewById(R.id.main_scrollView);
        // 主界面上下滑动监听
        scrollview.setOnScrollListener(new MainScrollview.ScrollViewListener() {
            @Override
            public void onScroll(int dy) {
                if (dy > 0) {//下滑
                    showOrHideFab(false);
                } else if (dy <= 0 ) {//上滑
                    showOrHideFab(true);
                }
            }
        });

        // 调用条目滑动事件监听方法
        view_Listener();
        // 备忘录是否为空检测
        emptyListCheck();
    }

    /**
     * 关闭fab菜单
     */
    private void hide_fabMenu(){
        FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.action_menu);
        if(menu!=null) menu.collapse();
    }

    /**
     * 对FloatingActionButton按钮相关设置的方法
     */
    private void fab_setting(){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.action_note);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //noteManager.add();
                //关闭fab菜单
                FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.action_menu);
                menu.collapse();

                Intent intent = new Intent(MemorandumActivity.this, CreateActivity.class);
                intent.putExtra("currentFolderName", currentFolderName);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                findViewById(R.id.action_menu).bringToFront();
            }
        });

        FloatingActionButton fab_quick = (FloatingActionButton)findViewById(R.id.action_quick);

        fab_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //noteManager.add();
                //关闭fab菜单
                FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.action_menu);
                menu.collapse();

                Intent intent = new Intent(MemorandumActivity.this, QuickCreateActivity.class);
                intent.putExtra("currentFolderName",currentFolderName);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                findViewById(R.id.action_menu).bringToFront();
            }
        });
    }

    private void showOrHideFab(boolean show){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.action_menu);

        if(show){
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }
    }

    /**
     * 条目滑动（左滑）事件监听方法（修改、分享、删除）
     */
    public void view_Listener() {
        //点击监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                noteManager.ItemClick(position);
            }
        });

        mListView.setOnMenuItemClickListener( new SwipeMenuListView.OnMenuItemClickListener(){
            //具体实现
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index){
                    //edit
                    case 0:
                        noteManager.editClick(position);
                        break;
                    case 1:
                        Intent intent = new Intent(MemorandumActivity.this, FilesActivity.class);
                        intent.putExtra("move",true);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("note",mData.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);

                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        //move
                        break;
                    case 2:
                        noteManager.deleteClick(position);
                        emptyListCheck();
                    default:
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 备忘录是否为空检测
     */
    public void emptyListCheck(){
        int number = 0;
        if(mData!=null){
            number=mData.size();
        }
        // 备忘录为空
        if(number == 0) {
            //hide and show
            mListView.setVisibility(View.GONE);
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
            TextView info = (TextView) findViewById(R.id.text_empty);
            info.setText(R.string.main_empty_info);
        }else{  // 备忘录不为空
            mListView.setVisibility(View.VISIBLE);
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.empty);
            empty.setVisibility(View.GONE);
        }
    }
}
