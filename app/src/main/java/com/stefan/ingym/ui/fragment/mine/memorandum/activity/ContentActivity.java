package com.stefan.ingym.ui.fragment.mine.memorandum.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.mine.memorandum.dialog.ChooseDialog;
import com.stefan.ingym.ui.fragment.mine.memorandum.dialog.MyOnClickListener;
import com.stefan.ingym.ui.fragment.mine.memorandum.dialog.ProDialog;
import com.stefan.ingym.ui.fragment.mine.memorandum.manager.NoteManager;
import com.stefan.ingym.ui.fragment.mine.memorandum.model.Note;
import com.stefan.ingym.ui.fragment.mine.memorandum.util.LocationUtil;
import com.stefan.ingym.ui.fragment.mine.memorandum.util.MsgToast;
import com.stefan.ingym.ui.fragment.mine.memorandum.util.ShareUtil;
import com.stefan.ingym.ui.fragment.mine.memorandum.util.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.richeditor.RichEditor;

/**
 * 内容
 * 由Main触发的活动
 */
public class ContentActivity extends BaseActivity {

    //展示的Note类
    private Note note;
    //Note管理类
    private NoteManager mNoteManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //获取mainActivity传来的信息
        Intent intent = this.getIntent();
        note = (Note) intent.getSerializableExtra("note");
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ContentActivity初始化
     */
    private void init() {
        init_toolbar();
        init_view();
        init_bottom();
    }

    /**
     * toolbar的初始化
     */
    private void init_toolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_content);
        mToolbar.setNavigationIcon(R.drawable.pic_back);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final ChooseDialog dialog = new ChooseDialog(ContentActivity.this);

                dialog.show();
                dialog.setTitle(ContentActivity.this.getResources().getString(R.string.share));
                //你想怎样分享
                dialog.setInfo(ContentActivity.this.getResources().getString(R.string.howShare));
                //分享截图
                dialog.setChoose1(ContentActivity.this.getResources().getString(R.string.imgShare));
                dialog.setListener_1(new MyOnClickListener() {
                    @Override
                    public void onClick() {
                        ShareUtil.shareImg(ContentActivity.this);
                    }
                });
                //分享文字
                dialog.setChoose2(ContentActivity.this.getResources().getString(R.string.textShare));
                dialog.setListener_2(new MyOnClickListener() {
                    @Override
                    public void onClick() {
                        ShareUtil.shareText(ContentActivity.this,
                                StringUtil.clearHtml(note.getText()));
                    }
                });
                dialog.setChoose3("取消");
                return false;
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_left);
            }
        });

        //toolbar上的标题
        TextView mTitle = (TextView) findViewById(R.id.title_toolbar);
        mTitle.setText(note.getName());

    }

    /**
     * view的初始化
     */
    private void init_view() {
        //Note管理类
        mNoteManager = new NoteManager(this, note.getFolderName());

        //日期
        final TextView date = (TextView) findViewById(R.id.date_content);
        date.setText(note.getDate().getDate() + "");

        //位置
        final TextView location = (TextView) findViewById(R.id.location_content);

        if (StringUtil.isEmpty(note.getLocation()))
            location.setVisibility(View.GONE);
        else {
            location.setText(note.getLocation());
        }

        RichEditor content = (RichEditor) findViewById(R.id.editor);
        content.setHtml(note.getText());
        content.setInputEnabled(false);

        TextView numberFollow = (TextView) findViewById(R.id.numberFollow_content);
        numberFollow.setText(" " + StringUtil.clearHtml(content.getHtml()).length() + " ");

        //Level
        switch (note.getLevel()) {
            case Note.GRE_LEVEL:
                findViewById(R.id.level_content).setBackgroundResource(R.drawable.radius_green);
                break;
            case Note.ORA_LEVEL:
                findViewById(R.id.level_content).setBackgroundResource(R.drawable.radius_orange);
                break;
            case Note.RED_LEVEL:
                findViewById(R.id.level_content).setBackgroundResource(R.drawable.radius_red);
                break;
        }
    }

    /**
     * 底部栏的初始化 注册监听
     */
    private void init_bottom() {
        //编辑
        findViewById(R.id.edit_bottom_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });
        //删除
        findViewById(R.id.delete_bottom_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteManager.deleteNote(note);
                //已经移动到最近删除
                MsgToast.showToast(ContentActivity.this,
                        getResources().getString(R.string.move_recycle));
                finish();
            }
        });
        //移动到
        findViewById(R.id.move_bottom_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ContentActivity.this, FilesActivity.class);
                intent.putExtra("move", true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

            }
        });
        //位置
        findViewById(R.id.location_bottom_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    /**
     * 编辑响应
     */
    private void edit() {
        Intent intent = new Intent(this, CreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("note", note);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }

    /**
     * 获取位置响应
     */
    private void getLocation() {
        //正在获取定位....
        final ProDialog proDialog = new ProDialog(this, getResources().getString(R.string.location));
        proDialog.show();

        LocationUtil mLocationMag = new LocationUtil(getApplicationContext());

        final TextView location = (TextView) findViewById(R.id.location_content);
        location.setVisibility(View.VISIBLE);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                proDialog.dismiss();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1500);

        String address = mLocationMag.getLocation();

        note = mNoteManager.updateLocation(note, address);

        location.setText(address);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Content Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
