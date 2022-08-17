package com.mtg.videoplay.view.activity;

import android.app.Application;
import android.widget.VideoView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;

public class VideoPlayActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        String path = getIntent().getStringExtra("file");
        VideoView view = findViewById(R.id.videoView);
        view.setVideoPath(path);
        view.start();

    }

    @Override
    protected void addEvent() {

    }


}
