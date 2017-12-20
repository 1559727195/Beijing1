package com.massky.new119eproject.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.net.URL;

import butterknife.InjectView;

/**
 * Created by zhu on 2017/12/19.
 */

public class VideoPlayActivity_New extends BaseActivity implements OnPreparedListener
,View.OnClickListener{

    private Uri url;
    @InjectView(R.id.status_view)
    StatusView mStatusView;
    @InjectView(R.id.back)
    ImageView back;
    @Override
    protected int viewId() {
        return R.layout.video_play_new;
    }

    @Override
    protected void onView() {
        String url_content = (String) getIntent().getSerializableExtra("url");
        url = Uri.parse(url_content);
        setupVideoView();
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.GRAY);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.

        init_video_view ();
    }

    private void init_video_view() {

    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private VideoView videoView;

    private void setupVideoView() {
        // Make sure to use the correct VideoView import
        videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);

        //For now we just picked an arbitrary item to play
        videoView.setVideoURI(url);
        videoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                VideoPlayActivity_New.this.finish();
            }
        });
    }

    @Override
    public void onPrepared() {
        //Starts the video playback as soon as it is ready
        videoView.start();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                VideoPlayActivity_New.this.finish();
                break;
        }
    }





//class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        Toast.makeText( LocalVideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
//    }
//}
//
//
//class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        Toast.makeText( LocalVideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
//    }
//}
}

