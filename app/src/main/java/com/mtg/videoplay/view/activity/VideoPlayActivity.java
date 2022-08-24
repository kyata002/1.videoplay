package com.mtg.videoplay.view.activity;

import static android.view.View.GONE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class VideoPlayActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    ImageView bt_play, bt_pre, bt_next, bt_speed, bt_screen, bt_lock, bt_back, bt_share, bt_out;
    TextView txt_name, txt_pstine, txt_maxtime;
    VideoView viewvideo;
    SeekBar pg_time;
    LinearLayout dh_bottom, dh_top;
    ArrayList<String> videoList;
    Boolean ck_Dh, ck_pause;
    ConstraintLayout videoPlay;
    int stopPosition, position;
    public static float speeb = 1;
    public static String pathVideo;
    public static int pos;
    CountDownTimer Timer;
    public static int keyPlay = 0;
    private MediaPlayer mediaPlayer;
    protected long mSeekTimePosition;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
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
        position = getIntent().getIntExtra("file", 1);
        videoList = getIntent().getStringArrayListExtra("list");
        start(position);
        progessbar();

    }

    private void progessbar() {
        pg_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewvideo.seekTo(pg_time.getProgress());
                viewvideo.start();
            }
        });
    }

    private void start(int position) {

        if (keyPlay == 0) {
            pathVideo = videoList.get(position);
            pos = position;
            keyPlay = 1;
        } else {
            position = pos;
        }
        viewvideo.setVideoPath(pathVideo);
        viewvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                VideoPlayActivity.this.mediaPlayer = mediaPlayer;
                txt_maxtime.setText(fomartMaxTime(mediaPlayer.getDuration()));
                pg_time.setMax(mediaPlayer.getDuration());
                curentTime();
                setNewSpeed();
            }
        });
        viewvideo.start();
        videoPlay.isFocusable();
        bt_play.setImageResource(R.drawable.ic_pause);
        txt_name.setText(new File(videoList.get(position)).getName());
//        txt_pstine.setText(curentTime(viewvideo.getCurrentPosition()));
        showDH();
    }

    private void setNewSpeed() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams myPlayBackParams = new PlaybackParams();
            myPlayBackParams.setSpeed(speeb); //you can set speed here
            VideoPlayActivity.this.mediaPlayer.setPlaybackParams(myPlayBackParams);
        }
    }

    private void showDH() {

        dh_top.setVisibility(View.VISIBLE);
        dh_bottom.setVisibility(View.VISIBLE);
        bt_lock.setVisibility(View.VISIBLE);
        ck_Dh = true;
        Timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                hideDH();
            }
        }.start();
    }

    private void hideDH() {
        dh_top.setVisibility(GONE);
        dh_bottom.setVisibility(GONE);
        bt_lock.setVisibility(GONE);
        ck_Dh = false;
    }


    @Override
    protected void addEvent() {
        videoPlay.setOnClickListener(view -> {
            if (ck_Dh) {
                hideDH();
                Timer.cancel();
            } else {
                showDH();
            }
        });
        bt_speed.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, bt_speed);
            popupMenu.getMenuInflater().inflate(R.menu.poppup_speed, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.sp05:
                            speeb = 0.5F;
                            break;
                        case R.id.sp75:
                            speeb = 0.75F;
                            break;
                        case R.id.sp1:
                            speeb = 1F;
                            break;
                        case R.id.sp15:
                            speeb = 1.5F;
                            break;
                        case R.id.sp175:
                            speeb = 1.75F;
                            break;
                        case R.id.sp2:
                            speeb = 2F;
                            break;
                    }
//                    onPause();
//                    onResume();
                    setNewSpeed();
                    return true;
                }
            });
            popupMenu.show();
        });

        dhAdmin();
        dhTop();
        changeScreen();

    }

    private void dhTop() {
        bt_back.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void dhAdmin() {
        bt_play.setOnClickListener(view -> {
            if (ck_pause) {
                onResume();
            } else {
                onPause();
            }
        });
        bt_pre.setOnClickListener(view -> {
            if (videoList.size() > 1 && (position < videoList.size() || position > 0)) {
                if (keyPlay == 1) position = pos;
                position--;
                speeb = 1F;
                viewvideo.stopPlayback();
                pathVideo = videoList.get(position);
                start(position);
                pos = position;
            }

        });
        bt_next.setOnClickListener(view -> {
            if (videoList.size() > 1 && (position < videoList.size() || position == 0)) {
                if (keyPlay == 1) position = pos;
                position++;
                speeb = 1F;
                viewvideo.stopPlayback();
                pathVideo = videoList.get(position);
                start(position);
                pos = position;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        bt_play.setImageResource(R.drawable.ic_play);
        stopPosition = viewvideo.getCurrentPosition(); //stopPosition is an int
        viewvideo.pause();
        ck_pause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        bt_play.setImageResource(R.drawable.ic_pause);
        ck_pause = false;
        viewvideo.seekTo(stopPosition);
        viewvideo.start(); //Or use resume() if it doesn't work. I'm not sure
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private String fomartMaxTime(int position) {
        SimpleDateFormat timeMax = new SimpleDateFormat("mm:ss");
        return timeMax.format(position);
    }

    private void changeScreen() {
        bt_screen.setOnClickListener(view -> {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });

    }

    private void curentTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat timeMax = new SimpleDateFormat("mm:ss");
                txt_pstine.setText(timeMax.format(viewvideo.getCurrentPosition()));
                pg_time.setProgress(viewvideo.getCurrentPosition());
                handler.postDelayed(this, 50);
            }
        }, 100);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int id = view.getId();
        if (id == R.id.videoView)
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Toast.makeText(this, "Video Status Saved", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "onTouch surfaceContainer actionDown [" + this.hashCode() + "] ");
                    mTouchingProgressBar = true;
                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangeBrightness = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if (mDownX < mScreenWidth * 0.5f) {
                        mChangeBrightness = true;
                        WindowManager.LayoutParams lp = getWindow(this).getAttributes();
                        if (lp.screenBrightness < 0) {
                            try {
                                mGestureDownBrightness = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//                    Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mGestureDownBrightness = lp.screenBrightness * 255;
//                Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                        }
                    } else {
                        mChangeVolume = true;
                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    }
                    if (mChangeBrightness) {
                        deltaY = -deltaY;
                        int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
                        WindowManager.LayoutParams params = getWindow(this).getAttributes();
                        if (((mGestureDownBrightness + deltaV) / 255) >= 1) {
                            params.screenBrightness = 1;
                        } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                            params.screenBrightness = 0.01f;
                        } else {
                            params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
                        }
                        getWindow(this).setAttributes(params);
                        //dialog
                        int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
//                        showBrightnessDialog(brightnessPercent);
//                        mDownY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                        Log.i(TAG, "onTouch surfaceContainer actionUp [" + this.hashCode() + "] ");
                    mTouchingProgressBar = false;
//                        dismissProgressDialog();
//                        dismissVolumeDialog();
//                        dismissBrightnessDialog();
//                        if (mChangeVolume) {
//                            onTouchEvent(UserActionInterface.ON_TOUCH_SCREEN_SEEK_VOLUME);
//                        }
//                        startProgressTimer();
                    break;
            }
        return true;
    }



    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }
    public static Window getWindow(Context context) {
        if (getAppCompActivity(context) != null) {
            return getAppCompActivity(context).getWindow();
        } else {
            return scanForActivity(context).getWindow();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

//    public void startProgressTimer() {
////        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
//        cancelProgressTimer();
//        UPDATE_PROGRESS_TIMER = new Timer();
//        mProgressTimerTask = new ProgressTimerTask();
//        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
//    }
//
//    public void cancelProgressTimer() {
//        if (UPDATE_PROGRESS_TIMER != null) {
//            UPDATE_PROGRESS_TIMER.cancel();
//        }
//        if (mProgressTimerTask != null) {
//            mProgressTimerTask.cancel();
//        }
//    }
//
//    public void onProgress(int progress, long position, long duration) {
//        if (!mTouchingProgressBar) {
//            if (seekToManulPosition != -1) {
//                if (seekToManulPosition > progress) {
//                    return;
//                } else {
//                    seekToManulPosition = -1;
//                }
//            } else {
//                if (progress != 0) progressBar.setProgress(progress);
//            }
//        }
//        if (position != 0) currentTimeTextView.setText(VideoUtils.stringForTime(position));
//        totalTimeTextView.setText(VideoUtils.stringForTime(duration));
//    }
}
