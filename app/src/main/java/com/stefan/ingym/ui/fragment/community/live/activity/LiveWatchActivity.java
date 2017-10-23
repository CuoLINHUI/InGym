package com.stefan.ingym.ui.fragment.community.live.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.community.live.controller.MediaController;
import com.stefan.ingym.util.ToastUtil;

/**
 * easycomm
 * Created by Simo on 2017/7/18.
 */
public class LiveWatchActivity extends Activity implements PLMediaPlayer.OnPreparedListener,
                                                PLMediaPlayer.OnInfoListener, PLMediaPlayer.OnCompletionListener {
    @ViewInject(R.id.pl_video_view)
    private PLVideoView plVideoView;
    @ViewInject(R.id.loading_view)
    private View loadingView;
    // 定义推送路径
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_live_watch);
        // 注入框架
        ViewUtils.inject(this);
        // 调用界面初始化方法
        initUI();
    }

    /**
     * 设置用户点击监听事件
     */
    @OnClick({R.id.live_play_close_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_play_close_iv:  //用户点击了关闭直播按钮
                finish();
                break;
        }
    }

    /**
     * 界面初始化方法
     */
    private void initUI() {
        MediaController mMediaController = new MediaController(this);
        // 为播放控件设置媒体控制器
        plVideoView.setMediaController(mMediaController);
        // 设置缓冲进度条
        plVideoView.setBufferingIndicator(loadingView);
        // 设置监听
        plVideoView.setOnPreparedListener(this);     // 预备状态监听
        plVideoView.setOnInfoListener(this);         // 播放过程中信息监听
        plVideoView.setOnCompletionListener(this);   // 播放完成监听
        // 设置画面预览方式
        plVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);
        plVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        // 为URL路径赋值
        url = "rtmp://rtmp.soyask.top/701studio/javasdktest1489147454866b";
        // 设置进路径
        plVideoView.setVideoPath(url);
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        plVideoView.start();
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        ToastUtil.show(getApplication(), "播放结束");
    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
        switch (what) {
            case 702:
                ToastUtil.show(getApplication(), "缓冲结束");
                break;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        plVideoView.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        plVideoView.stopPlayback();
    }
}
