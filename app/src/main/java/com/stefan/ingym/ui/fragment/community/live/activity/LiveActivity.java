package com.stefan.ingym.ui.fragment.community.live.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.stefan.ingym.R;
import com.stefan.ingym.ui.fragment.community.live.view.CameraPreviewFrameView;
import com.stefan.ingym.util.ToastUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;


/**
 * easycomm
 * Created by Simo on 2017/7/18.
 */

public class LiveActivity extends Activity implements CameraPreviewFrameView.Listener, StreamingStateChangedListener {

    @ViewInject(R.id.cameraPreview_surfaceView)
    CameraPreviewFrameView cameraPreviewSurfaceView;
    @ViewInject(R.id.cameraPreview_afl)
    AspectFrameLayout cameraPreviewAfl;

    private MediaStreamingManager streamingManager;
    private StreamingProfile streamingProfile;
    private MicrophoneStreamingSetting mMicrophoneStreamingSetting;

    private static final String TAG = "LiveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        // 注入框架
        ViewUtils.inject(this);
        // 界面初始化方法
        initUI();
    }

    /**
     * 设置用户点击监听事件
     */
    @OnClick({R.id.live_play_close_iv, R.id.live_play_foot_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_play_close_iv:  //用户点击了关闭直播按钮
                finish();
                break;

            case R.id.live_play_foot_rl:
                ToastUtil.show(getApplication(), "该部分功能模块还未实现哦！");
                break;
        }
    }

    /**
     * UI初始化方法
     */
    private void initUI() {
        // 设置摄像头模式
        cameraPreviewAfl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        cameraPreviewSurfaceView.setListener(this);
        // 调用初始化直播方法
        initLive();
    }

    /**
     * 初始化直播的方法
     */
    private void initLive() {
        String publishUrl = getIntent().getStringExtra("video_url");
        Log.i(TAG, "initUI: " + publishUrl);

        if (publishUrl == null) {
            publishUrl = "rtmp://live.soyask.top/701studio/javasdktest1489147454866b";
        }
        streamingProfile = new StreamingProfile();

        try {
            streamingProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM2)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
//                .setPreferredVideoEncodingSize(960, 544)
                    .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.BITRATE_PRIORITY)
//                .setAVProfile(avProfile)
                    .setDnsManager(getMyDnsManager())
                    .setAdaptiveBitrateEnable(true)
                    .setFpsControllerEnable(true)
                    .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                    .setPublishUrl(publishUrl)
//                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                    .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
            CameraStreamingSetting setting = new CameraStreamingSetting();
            setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                    .setContinuousFocusModeEnabled(true)
                    .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                    .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
            Log.i(TAG, "initLive: ");
            streamingManager = new MediaStreamingManager(this, cameraPreviewAfl, cameraPreviewSurfaceView,
                    AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC); // hw codec  // soft codec
            mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
            mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);
            streamingManager.prepare(setting, mMicrophoneStreamingSetting, null, streamingProfile);
            streamingManager.setStreamingStateListener(this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }

    @Override
    public void onResume() {
        super.onResume();
        streamingManager.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // You must invoke pause here.
        streamingManager.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamingManager.destroy();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onZoomValueChanged(float factor) {
        return false;
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object o) {
        switch (streamingState) {
            case PREPARING:
                break;
            case READY:
                // start streaming when READY
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (streamingManager != null) {
                            streamingManager.startStreaming();
                        }
                    }
                }).start();
                break;
            case CONNECTING:
                break;
            case STREAMING:
                // The av packet had been sent.
                break;
            case SHUTDOWN:
                // The streaming had been finished.
                break;
            case IOERROR:
                // Network connect error.
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                // Failed to record audio.
                break;
            case OPEN_CAMERA_FAIL:
                // Failed to open camera.
                break;
            case DISCONNECTED:
                // The socket is broken while streaming
                break;
        }
    }
}
