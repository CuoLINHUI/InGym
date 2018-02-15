package com.stefan.ingym.ui.fragment.community.moments.widgets.videolist.model;

import android.media.MediaPlayer;

import com.stefan.ingym.ui.fragment.community.moments.widgets.videolist.widget.TextureVideoView;

/**
 * @author Wayne
 */
public interface VideoLoadMvpView {

    TextureVideoView getVideoView();

    void videoBeginning();

    void videoStopped();

    void videoPrepared(MediaPlayer player);

    void videoResourceReady(String videoPath);
}
