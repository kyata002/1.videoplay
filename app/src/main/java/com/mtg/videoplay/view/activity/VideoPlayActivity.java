package com.mtg.videoplay.view.activity;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Rational;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.view.dialog.DialogChange;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.File;
import java.text.SimpleDateFormat;
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

        start(position);
        progessbar();
        videoPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                int id = view.getId();
                if (id == R.id.videoPlay) {
                    if (ck_lock == false) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:

                                mDownX = x;
                                mDownY = y;
                                mChangeVolume = false;
                                mChangeBrightness = false;
                                if (ck_Dh) {
                                    hideDH();
//                                    Timer.cancel();
                                } else {
                                    showDH();
                                    Timer = new CountDownTimer(5000, 1000) {
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        public void onFinish() {
                                            hideDH();
                                        }
                                    }.start();
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (ck_Dh) {
                                    Timer.cancel();
                                }
                                float deltaX = x - mDownX;
                                float deltaY = y - mDownY;
                                if (mDownX < mScreenWidth * 0.5f) {
                                    mChangeBrightness = true;
                                    WindowManager.LayoutParams lp = getWindow(VideoPlayActivity.this).getAttributes();
                                    if (lp.screenBrightness < 0) {
                                        try {
                                            mGestureDownBrightness = Settings.System.getInt(VideoPlayActivity.this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                        } catch (Settings.SettingNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        mGestureDownBrightness = lp.screenBrightness * 255;
                                    }
                                } else {
                                    mChangeVolume = true;
                                    mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                }
                                if (mChangeVolume) {
                                    deltaY = -deltaY;
                                    int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                    int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                                    //dialog
                                    int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
                                    DialogChange.showVolumeDialog(-deltaY, volumePercent, VideoPlayActivity.this);
                                }
                                if (mChangeBrightness) {
                                    deltaY = -deltaY;
                                    int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
                                    WindowManager.LayoutParams params = getWindow(VideoPlayActivity.this).getAttributes();
                                    if (((mGestureDownBrightness + deltaV) / 255) >= 1) {
                                        params.screenBrightness = 1;
                                    } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                                        params.screenBrightness = 0.01f;
                                    } else {
                                        params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
                                    }
                                    getWindow(VideoPlayActivity.this).setAttributes(params);
                                    //dialog
                                    int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
                                    DialogChange.showBrightnessDialog(brightnessPercent, VideoPlayActivity.this);
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                mTouchingProgressBar = false;
                                DialogChange.dismissVolumeDialog();
                                DialogChange.dismissBrightnessDialog();
                                if (ck_Dh) {
                                    Timer = new CountDownTimer(5000, 1000) {
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        public void onFinish() {
                                            hideDH();
                                        }
                                    }.start();
                                }
                                break;
                        }
                    } else {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (ck_visible == false) {
                                    fr_lock.setVisibility(View.VISIBLE);
                                    if (ck_lock == false) Timer2.cancel();
                                    Timer2 = new CountDownTimer(5000, 1000) {
                                        public void onTick(long millisUntilFinished) {

                                        }

                                        public void onFinish() {
                                            hideDH();
                                        }
                                    }.start();
                                    ck_visible = true;
                                } else {
                                    fr_lock.setVisibility(GONE);
                                    Timer2.cancel();
                                    ck_visible = false;
                                }
                                break;
                        }
                    }

                }
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            picture = new PictureInPictureParams.Builder();
        }

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
            pathVideo = videoList.get(position).getPath();
            pos = position;
            keyShowDH = 0;

        } else {
            position = pos;
        }
        viewvideo.setVideoPath(pathVideo);
        viewvideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                bt_replay.setVisibility(View.VISIBLE);
//                pg_time.setProgress(0);
            }
        });
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
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (mRotation.equals("0") || mRotation.equals("180")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        };
        thread.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode()) {
                picture.notify();
            } else {

            }
        }
        viewvideo.start();
        videoPlay.isFocusable();
        bt_play.setImageResource(R.drawable.ic_pause);
        txt_name.setText(new File(videoList.get(position).getPath()).getName());
        showDH();
    }

    // set speed
    private void setNewSpeed() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams myPlayBackParams = new PlaybackParams();
            myPlayBackParams.setSpeed(speeb); //you can set speed here
            VideoPlayActivity.this.mediaPlayer.setPlaybackParams(myPlayBackParams);
        }
    }


    // show controller
    private void showDH() {

        dh_top.setVisibility(View.VISIBLE);
        dh_bottom.setVisibility(View.VISIBLE);
        fr_lock.setVisibility(View.VISIBLE);
        Timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                keyPlay = 1;
            }

            public void onFinish() {
                hideDH();
                keyPlay = 0;
            }
        }.start();
        ck_Dh = true;
    }

    // hide controller
    private void hideDH() {
        dh_top.setVisibility(GONE);
        dh_bottom.setVisibility(GONE);
        fr_lock.setVisibility(GONE);
        ck_Dh = false;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void addEvent() {
        findViewById(R.id.fr_lock).setOnClickListener(view -> {
            if (ck_lock == false) {
                ck_lock = true;
                bt_lock.setImageResource(R.drawable.ic_lock);
                ck_visible = false;
                hideDH();
            } else {
                showDH();
                bt_lock.setImageResource(R.drawable.ic_unlock);
                ck_lock = false;
            }
        });
        findViewById(R.id.bt_zoom_out).setOnClickListener(view -> {
            hideDH();
            pictrueInpictureMode();
        });
        findViewById(R.id.dh_top).setOnClickListener(view -> {
            showDH();
        });
        findViewById(R.id.dh_bottom).setOnClickListener(view -> {
            showDH();
        });
        findViewById(R.id.bt_share_play).setOnClickListener(view -> {
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoList.get(position).getPath()));
            this.startActivity(Intent.createChooser(shareIntent, "Share image using"));
        });

        // popup change speed
        bt_speed.setOnClickListener(view -> {
            if (powerMenu != null && powerMenu.isShowing() == true) {

            } else {
                powerMenu = new PowerMenu.Builder(this)
                        //.addItemList(list) // list has "Novel", "Poerty", "Art"
                        .addItem(new PowerMenuItem("0.5x", false)) // add an item.
                        .addItem(new PowerMenuItem("0.75x", false))
                        .addItem(new PowerMenuItem("1x (Normal)", false)) // add an item.
                        .addItem(new PowerMenuItem("1.25x", false)) // add an item.
                        .addItem(new PowerMenuItem("1.5x", false)) // aad an item list.
//                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(12f)
//                    .setTextTypeface(ResourcesCompat.getFont(context, R.font.lexend_regular)!!)
                        .setPadding(10)// sets the corner radius.
                        .setSize(380, 680)
                        .setMenuShadow(10f) // sets the shadow.
//                        .setIconSize(28)
                        .setTextSize(16)
//                        .setIconPadding(2)
                        .setMenuColor(0)
                        .setBackgroundColor(Color.TRANSPARENT)
                        .setOnBackgroundClickListener(view1 -> {
                            powerMenu.dismiss();
                        })
                        //.setTextColor(ContextCompat.getColor(context, Color.parseColor("#3C3C3C")))
                        .setTextGravity(Gravity.LEFT)
                        .setTextTypeface(Typeface.create("font/lexend_regular.ttf", Typeface.NORMAL))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(this, R.color.black))
                        .setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                            @Override
                            public void onItemClick(int posit, PowerMenuItem item) {
                                CharSequence title = item.getTitle();
                                if ("0.5x".equals(title)) {
                                    speeb = 0.5f;
                                    setNewSpeed();
                                    powerMenu.dismiss();
                                } else if ("0.75x".equals(title)) {
                                    speeb = 0.75f;
                                    setNewSpeed();
                                    powerMenu.dismiss();
                                } else if ("1x (Normal)".equals(title)) {
                                    speeb = 1f;
                                    setNewSpeed();
                                    powerMenu.dismiss();
                                } else if ("1.25x".equals(title)) {
                                    speeb = 1.25f;
                                    setNewSpeed();
                                    powerMenu.dismiss();
                                } else if ("1.5x".equals(title)) {
                                    speeb = 1.5f;
                                    setNewSpeed();
                                    powerMenu.dismiss();
//                                }else if("2x".equals(title)){
//                                    speeb=2f;
//                                    setNewSpeed();
//                                    powerMenu.dismiss();
                                }
                            }
                        }).build();
                powerMenu.showAsDropDown(view);
            }
        });
        bt_replay.setOnClickListener(view -> {
            viewvideo.seekTo(0);
            viewvideo.start();
            bt_replay.setVisibility(GONE);
        });
        dhAdmin();
        dhTop();
        changeScreen();

    }


    //Controler top
    private void dhTop() {
        bt_back.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    // controls start, continue, rewind video
    @RequiresApi(api = Build.VERSION_CODES.N)
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
                pathVideo = videoList.get(position).getPath();
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
                pathVideo = videoList.get(position).getPath();
                start(position);
                pos = position;
            }
        });
    }


    // pause video
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();
        if (isInPictureInPictureMode()) {
            onResume();
        } else {
            bt_play.setImageResource(R.drawable.ic_play);
            stopPosition = viewvideo.getCurrentPosition(); //stopPosition is an int
            viewvideo.pause();
            ck_pause = true;
        }
    }

    // resume video
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

    // Video time max
    private String fomartMaxTime(int position) {
        SimpleDateFormat timeMax = new SimpleDateFormat("mm:ss");
        return timeMax.format(position);
    }

    // set up sreen
    private void changeScreen() {
        bt_screen.setOnClickListener(view -> {
//            keyShow++;
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }

    // Time Running
    private void curentTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat timeMax = new SimpleDateFormat("mm:ss");
                txt_pstine.setText(timeMax.format(viewvideo.getCurrentPosition()));
                pg_time.setProgress(viewvideo.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }, 1000);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
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

    private void pictrueInpictureMode() {
        hideDH();
        bt_replay.setVisibility(GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspect = new Rational(viewvideo.getWidth(), viewvideo.getHeight());
            picture.setAspectRatio(aspect).build();
            enterPictureInPictureMode(picture.build());
        } else {

        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isInPictureInPictureMode()) {
                pictrueInpictureMode();
            } else {

            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
//            bt_out.setVisibility(GONE);
        } else {
//            bt_out.setVisibility(View.VISIBLE);
        }
    }

}
