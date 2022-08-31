package com.mtg.videoplay.view.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Rational;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.FileVideo;
import com.skydoves.powermenu.PowerMenu;

import java.util.ArrayList;

public class VideoPlayActivity extends BaseActivity implements View.OnTouchListener {
    ImageView bt_play, bt_pre, bt_next, bt_speed, bt_screen, bt_lock, bt_back, bt_share, bt_out, bt_replay;
    TextView txt_name, txt_pstine, txt_maxtime;
    VideoView viewvideo;
    SeekBar pg_time;
    LinearLayout dh_bottom, dh_top;
    ArrayList<FileVideo> videoList;
    Boolean ck_Dh, ck_pause;
    ConstraintLayout videoPlay;
    FrameLayout fr_lock;
    CountDownTimer Timer;
    CountDownTimer Timer2;
    ActionBar actionBar;
    int stopPosition, position;
    String mRotation;

    public static float speeb = 1;
    public static String pathVideo;
    public static int pos;
    public static int keyPlay = 0;
    public static int keyShowDH = 0;

    private MediaPlayer mediaPlayer;
    private PictureInPictureParams.Builder picture;
    private PowerMenu powerMenu;

    protected int mGestureDownVolume;
    protected float mGestureDownBrightness;
    protected boolean mChangeVolume;
    protected boolean mChangeBrightness;
    protected AudioManager mAudioManager;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected float mDownX;
    protected float mDownY;
    protected boolean mTouchingProgressBar;

    boolean ck_lock = false;
    boolean ck_visible = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        mLink();


        position = getIntent().getIntExtra("file", 1);
        videoList = (ArrayList<FileVideo>) getIntent().getSerializableExtra("list");
        mRotation = getIntent().getStringExtra("rotation");
        mScreenWidth = this.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = this.getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        startVideo(position);


    }

    private void startVideo(int position) {
        viewvideo.setVideoPath(videoList.get(position).getPath());
        viewvideo.start();
    }

    private void mLink() {
        bt_play = findViewById(R.id.bt_play);
        bt_pre = findViewById(R.id.bt_prive_play);
        bt_next = findViewById(R.id.bt_next_play);
        bt_lock = findViewById(R.id.bt_lock_play);
        bt_screen = findViewById(R.id.bt_phone_screen);
        bt_speed = findViewById(R.id.bt_speed_play);
        bt_back = findViewById(R.id.bt_back_play);
        bt_out = findViewById(R.id.bt_zoom_out);
        bt_share = findViewById(R.id.bt_share_play);
        txt_maxtime = findViewById(R.id.txt_time_max);
        txt_pstine = findViewById(R.id.txt_time_position);
        txt_name = findViewById(R.id.txt_name_play);
        pg_time = findViewById(R.id.pg_time_load);
        dh_bottom = findViewById(R.id.dh_bottom);
        dh_top = findViewById(R.id.dh_top);
        viewvideo = findViewById(R.id.videoView);
        videoPlay = findViewById(R.id.videoPlay);
        fr_lock = findViewById(R.id.fr_lock);
        bt_replay = findViewById(R.id.bt_replay);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void addEvent() {
        bt_out.setOnClickListener(view -> {
            enterPipMode();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPipMode() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int heinght = point.y;
        Rational rational = new Rational(width,heinght);
        PictureInPictureParams.Builder pictureInPictureParams = new PictureInPictureParams.Builder();
        pictureInPictureParams.setAspectRatio(rational);
        hideUnnecessaryView();
        this.enterPictureInPictureMode(pictureInPictureParams.build());
    }

    private void hideUnnecessaryView() {

    }
    private void showView(){

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewvideo.stopPlayback();
        this.releaseInstance();
        startVideo(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if(isInPictureInPictureMode){

        }else{
            startVideo(position);
        }
    }
}
