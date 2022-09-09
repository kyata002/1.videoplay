package com.mtg.videoplay.view.activity

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.util.Rational
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.mtg.videoplay.R
import com.mtg.videoplay.base.BaseActivity
import com.mtg.videoplay.model.FileVideo
import com.mtg.videoplay.view.dialog.DialogChange
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_play.*
import java.io.File
import java.net.URLConnection
import java.text.SimpleDateFormat
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class VideoPlayerActivity : BaseActivity() {
    private var ck_pause: Boolean = false
    private var videoIndex: Int = 0
    private var powerMenu: PowerMenu? = null
    private var ck_lock: Boolean = false
    private var mChangeBrightness: Boolean = false
    private var mGestureDownVolume = 0
    private var ck_Dh: Boolean = true
    private var mChangeVolume: Boolean = false
    private var ck_visible: Boolean = false
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mGestureDownBrightness = 0
    private var mTouchingProgressBar = false
    private var mediaPlayer: MediaPlayer? = null
    var stopPosition: Int = 0
    var videpList: ArrayList<FileVideo>? = ArrayList()
    var mDownX: Float = 0.0f
    var mDownY: Float = 0.0f
    var Timer2: CountDownTimer? = null
    var Timer: CountDownTimer? = null
    var ck_time: Boolean = false;
    lateinit var mAudioManager: AudioManager
    override fun getLayoutId(): Int {
        return R.layout.activity_play
    }

    override fun initView() {
        mScreenWidth = this.getResources().getDisplayMetrics().widthPixels
        mScreenHeight = this.getResources().getDisplayMetrics().heightPixels
        mAudioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        Timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                hideDH()
            }
        }.start()
        pop();
        prepareSource(intent)
        registerListeners()
        progessbar()
        videoPlay.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                val x = motionEvent.x
                val y = motionEvent.y
                val id: Int = view.id
                if (id == R.id.videoPlay) {
                    if (ck_lock === false) {
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {
                                mDownX = x
                                mDownY = y
                                mChangeVolume = false
                                mChangeBrightness = false

                                if (ck_Dh) {
                                    hideDH()
                                } else {
                                    showDH()
                                }

                            }
                            MotionEvent.ACTION_MOVE -> {

                                var deltaY: Float = y - mDownY
                                if (mDownX < mScreenWidth * 0.5f) {
                                    mChangeBrightness = true
                                    val lp = getWindow(this@VideoPlayerActivity)?.attributes
                                    if (lp?.screenBrightness!! < 0) {
                                        try {
                                            mGestureDownBrightness = Settings.System.getInt(
                                                this@VideoPlayerActivity.contentResolver,
                                                Settings.System.SCREEN_BRIGHTNESS
                                            )
                                        } catch (e: Settings.SettingNotFoundException) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        mGestureDownBrightness = (lp.screenBrightness * 255).toInt()
                                    }
                                } else {
                                    mChangeVolume = true
                                    mGestureDownVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
                                }
                                if (mChangeVolume) {
                                    deltaY = -deltaY
                                    val max: Int =
                                        mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                    val deltaV = (max * deltaY * 3 /(mScreenHeight*10))
                                    mAudioManager!!.setStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        (mGestureDownVolume + deltaV).roundToInt(),
                                        0
                                    )
                                    //dialog
                                    DialogChange.showVolumeDialog(
                                        ((mGestureDownVolume * 100 / max + deltaY * 3 * 100 / (mScreenHeight*10)).toInt()),
                                        this@VideoPlayerActivity
                                    )
                                }
                                if (mChangeBrightness) {
                                    deltaY = -deltaY
                                    val deltaV = (255 * deltaY * 3 / mScreenHeight)
                                    val params = getWindow(this@VideoPlayerActivity)?.attributes
                                    if ((mGestureDownBrightness + deltaV) / 255 >= 1) {
                                        params?.screenBrightness = 1f
                                    } else if ((mGestureDownBrightness + deltaV) / 255 <= 0) {
                                        params?.screenBrightness = 0.01f
                                    } else {
                                        params?.screenBrightness =
                                            ((mGestureDownBrightness + deltaV) / 255).toFloat()
                                    }
                                    getWindow(this@VideoPlayerActivity)?.attributes = params
                                    //dialog
                                    DialogChange.showBrightnessDialog(
                                        ((mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight).toInt()),
                                        this@VideoPlayerActivity
                                    )
                                }
                            }
                            MotionEvent.ACTION_UP -> {
                                mTouchingProgressBar = false
                                DialogChange.dismissVolumeDialog()
                                DialogChange.dismissBrightnessDialog()
                                if (ck_Dh) {
                                    Timer = object : CountDownTimer(5000, 1000) {
                                        override fun onTick(millisUntilFinished: Long) {}
                                        override fun onFinish() {
                                            hideDH()
                                        }
                                    }.start()
                                } else {
                                    Timer?.cancel()
                                }
                            }
                        }
                    } else {
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> if (ck_visible == false) {
                                fr_lock.visibility = View.VISIBLE
                                Timer2 = object : CountDownTimer(5000, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                    }

                                    override fun onFinish() {
                                        fr_lock.visibility = GONE
                                        ck_visible = false
                                    }
                                }.start()
                                ck_visible = true
                            } else {
                                Timer2?.cancel()
                                fr_lock.visibility = GONE
                                ck_visible = false
                            }
                        }
                    }
                }
                return true
            }
        })
    }

    private fun pop() {
        powerMenu =
            PowerMenu.Builder(this) //.addItemList(list) // list has "Novel", "Poerty", "Art"
                .addItem(PowerMenuItem("0.5x", false)) // add an item.
                .addItem(PowerMenuItem("0.75x", false))
                .addItem(PowerMenuItem("1x (Normal)", true)) // add an item.
                .addItem(PowerMenuItem("1.25x", false)) // add an item.
                .addItem(PowerMenuItem("1.5x", false)) // aad an item list.
                //                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(12f) //                    .setTextTypeface(ResourcesCompat.getFont(context, R.font.lexend_regular)!!)
                .setPadding(10) // sets the corner radius.
                .setSize(400, 760)
                .setMenuShadow(10f) // sets the shadow.
                //                        .setIconSize(28)
                .setTextSize(16) //                        .setIconPadding(2)
                .setMenuColor(0)
                .setBackgroundColor(Color.TRANSPARENT)
                .setOnBackgroundClickListener { view1: View? -> powerMenu?.dismiss() } //.setTextColor(ContextCompat.getColor(context, Color.parseColor("#3C3C3C")))
                .setTextGravity(Gravity.LEFT)
                .setTextTypeface(
                    Typeface.create(
                        "font/lexend_regular.ttf",
                        Typeface.NORMAL
                    )
                )
                .setMenuColor(ContextCompat.getColor(this, R.color.white))
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.white))
                .setOnMenuItemClickListener { posit, item ->

                    val title = item.title
                    when (posit) {
                        0 -> {
                            selectColor(powerMenu, item)
                        }
                        1 -> {
                            selectColor(powerMenu, item)
                        }
                        2 -> {
                            selectColor(powerMenu, item)
                        }
                        3 -> {
                            selectColor(powerMenu, item)
                        }
                        4 -> {
                            selectColor(powerMenu, item)
                        }
                    }
                    when (title) {
                        "0.5x" -> {
                            speed = 0.5f
                            setNewSpeed()
                            bg_replay.visibility = GONE
                            powerMenu?.dismiss()
                        }
                        "0.75x" -> {
                            speed = 0.75f
                            setNewSpeed()
                            bg_replay.visibility = GONE
                            powerMenu?.dismiss()
                        }
                        "1x (Normal)" -> {
                            speed = 1f
                            setNewSpeed()
                            bg_replay.visibility = GONE
                            powerMenu?.dismiss()
                        }
                        "1.25x" -> {
                            speed = 1.25f
                            setNewSpeed()
                            bg_replay.visibility = GONE
                            powerMenu?.dismiss()
                        }
                        "1.5x" -> {
                            speed = 1.5f
                            setNewSpeed()
                            bg_replay.visibility = GONE
                            powerMenu?.dismiss()
                        }

                    }
                }
                .setSelectedTextColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_purple
                    )
                )
                .build()
    }

    private fun selectColor(powerMenu: PowerMenu?, item: PowerMenuItem?) {
        powerMenu?.itemList?.forEach {
            it.setIsSelected(false)
        }
        item?.setIsSelected(true)
    }

    private fun showDH() {
        fr_lock.visibility = VISIBLE
        dh_bottom.visibility = VISIBLE
        dh_top.visibility = VISIBLE
        ck_Dh = true
    }

    private fun hideDH() {
        fr_lock.visibility = GONE
        dh_top.visibility = GONE
        dh_bottom.visibility = GONE
        ck_Dh = false
    }

    override fun addEvent() {
        var file: File
        file = File(videpList?.get(videoIndex)?.path)
        txt_name_play.setText(file.name)
        fr_lock.setOnClickListener {
//            Timer2?.cancel()
            if (ck_lock == false) {
                ck_lock = true
                bt_lock_play.setImageResource(R.drawable.ic_lock)
                ck_visible = false
                hideDH()
            } else {
                showDH()
                bt_lock_play.setImageResource(R.drawable.ic_unlock)
                ck_lock = false
            }
        }
        bt_replay.setOnClickListener {
            videoView.seekTo(0)
            videoView.start()
            bg_replay.visibility = GONE
            bt_play.setImageResource(R.drawable.ic_pause)
            ck_pause = false;
        }
        bt_back_play.setOnClickListener {
            onBackPressed()
        }
        bt_share_play.setOnClickListener {
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            val intentShareFile = Intent(Intent.ACTION_SEND)
            intentShareFile.type = URLConnection.guessContentTypeFromName(file.name)
            intentShareFile.putExtra(
                Intent.EXTRA_STREAM,
                Uri.fromFile(file)
            )
            this.startActivity(Intent.createChooser(intentShareFile, "Share File"))
        }

        // popup change speed
        bt_speed_play.setOnClickListener {
            if (powerMenu != null && powerMenu!!.isShowing == true) {
                return@setOnClickListener
            } else {
                powerMenu?.showAsDropDown(it);
            }
        }
        bt_play.setOnClickListener { view: View? ->
            if (ck_pause) {
                onResume()
            } else {
                onPause()
            }
        }
        bt_prive_play.setOnClickListener { view: View? ->

            if (videoIndex > 0 && videoIndex < videpList?.size!!) {
                if (keyPlay == 1) videoIndex = pos
                videoIndex--
                if (videpList!!.get(videoIndex) != null) {
                    var file: File
                    file = File(videpList?.get(videoIndex)?.path)
                    txt_name_play.setText(file.name)
                    speed = 1f
                    videoView.stopPlayback()
                    pathVideo = videpList!!.get(videoIndex).getPath()
                    videoView.apply {
                        setVideoPath(videoIndex?.let { videpList!!.get(it).path })
                        start()
                    }
                    pos = videoIndex
                } else {
                    videoIndex--
                    var file: File
                    file = File(videpList?.get(videoIndex)?.path)
                    txt_name_play.setText(file.name)
                    speed = 1f
                    videoView.stopPlayback()
                    pathVideo = videpList!!.get(videoIndex).getPath()
                    videoView.apply {
                        setVideoPath(videoIndex?.let { videpList!!.get(it).path })
                        start()
                    }
                    pos = videoIndex
                }
            }
            if (videoIndex == 0) {
                bt_next_play.setImageResource(R.drawable.ic_next)
                bt_prive_play.setImageResource(R.drawable.ic_no_previous)
            } else {
                bt_play.setImageResource(R.drawable.ic_pause)
                ck_pause = false;
                bt_next_play.setImageResource(R.drawable.ic_next)
                bt_prive_play.setImageResource(R.drawable.ic_previous)
            }

        }
        bt_next_play.setOnClickListener { view: View? ->

            if (videoIndex < videpList?.size!! - 1) {
                if (keyPlay == 1) videoIndex = pos
                videoIndex++
                if (videpList!!.get(videoIndex) != null) {
                    var file: File
                    file = File(videpList?.get(videoIndex)?.path)
                    txt_name_play.setText(file.name)
                    speed = 1f
                    videoView.stopPlayback()
                    pathVideo = videpList!!.get(videoIndex).getPath()
                    videoView.apply {
                        setVideoPath(videoIndex?.let { videpList!!.get(it).path })
                        start()
                    }
                    pos = videoIndex
                } else {
                    videoIndex++
                    var file: File
                    file = File(videpList?.get(videoIndex)?.path)
                    txt_name_play.setText(file.name)
                    speed = 1f
                    videoView.stopPlayback()
                    pathVideo = videpList!!.get(videoIndex).getPath()
                    videoView.apply {
                        setVideoPath(videoIndex?.let { videpList!!.get(it).path })
                        start()
                    }
                    pos = videoIndex
                }
            }
            if (videoIndex == videpList!!.size - 1) {
                bt_next_play.setImageResource(R.drawable.ic_no_next_play)
                bt_prive_play.setImageResource(R.drawable.ic_previous)
            } else {
                bt_play.setImageResource(R.drawable.ic_pause)
                ck_pause = false;
                bt_next_play.setImageResource(R.drawable.ic_next)
                bt_prive_play.setImageResource(R.drawable.ic_previous)
            }


        }
        bt_play.setOnClickListener {
            if (ck_pause) {
                onResume()
            } else {
                onPause()
            }
        }
        bt_phone_screen.setOnClickListener { view: View? ->
//            keyShow++;
            val orientation = resources.configuration.orientation
            requestedOrientation = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    private fun registerListeners() {
        bt_zoom_out.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPipMode()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPipMode() {
        val d: Display = windowManager.defaultDisplay
        val p = Point()
        d.getSize(p)
        val width: Int = p.x
        val height: Int = p.y
        val ratio = Rational(width, height)
        val pipBuilder = PictureInPictureParams.Builder()
        pipBuilder.setAspectRatio(ratio)
        hideUnnecessaryViews()
        this.enterPictureInPictureMode(pipBuilder.build())
    }

    private fun prepareSource(intent: Intent?) {
        videoIndex = intent?.getIntExtra("file", -1)!!
        videpList = intent?.getSerializableExtra("list") as ArrayList<FileVideo>
        if (videoIndex == 0) {
            bt_next_play.setImageResource(R.drawable.ic_next)
            bt_prive_play.setImageResource(R.drawable.ic_no_previous)
        } else if (videoIndex == videpList!!.size - 1) {
            bt_next_play.setImageResource(R.drawable.ic_next)
            bt_prive_play.setImageResource(R.drawable.ic_previous)
        }
        speed = 1f
        videoView.apply {
            setVideoPath(videoIndex?.let { videpList!!.get(it).path })
            start()
        }
        videoView.setOnCompletionListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when (isInPictureInPictureMode) {
                    true -> hideUnnecessaryViews()
                    false -> bg_replay.visibility = VISIBLE
                }
                bt_play.setImageResource(R.drawable.ic_play);
                ck_pause = true;
            } else {
                bg_replay.visibility = VISIBLE
                bt_play.setImageResource(R.drawable.ic_play);
                ck_pause = true;
            }
        }
        videoView.setOnPreparedListener(OnPreparedListener { mediaPlayer ->
            this@VideoPlayerActivity.mediaPlayer = mediaPlayer
            txt_time_max.setText(fomartMaxTime(mediaPlayer.duration))
            pg_time_load.setMax(mediaPlayer.duration)
            curentTime()
            setNewSpeed()
            bg_replay.visibility = GONE
        })
    }

    private fun progessbar() {
        pg_time_load.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                bg_replay.visibility = GONE
                bt_play.setImageResource(R.drawable.ic_pause)
                videoView.seekTo(pg_time_load.getProgress())
                videoView.start()
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        videoView.apply {
            stopPlayback()
            releaseInstance()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                prepareSource(intent)
            }
        }
    }

    private fun setNewSpeed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val myPlayBackParams = PlaybackParams()
            myPlayBackParams.speed = speed //you can set speed here
            this.mediaPlayer?.setPlaybackParams(myPlayBackParams)
        }
    }

    private fun fomartMaxTime(position: Int): String? {
        val timeMax = SimpleDateFormat("mm:ss")
        return timeMax.format(position)
    }

    private fun curentTime() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val timeMax = SimpleDateFormat("mm:ss")
                txt_time_position.text = timeMax.format(videoView.currentPosition)
                pg_time_load.progress = videoView.currentPosition
                handler.postDelayed(this, 10)
            }
        }, 10)
    }

    private fun hideUnnecessaryViews() {
        hideDH()
    }

    private fun showViews() {
        showDH()
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        when (isInPictureInPictureMode) {
            true -> hideUnnecessaryViews()
            false -> showViews()
        }
    }

    fun getWindow(context: Context?): Window? {
        return if (getAppCompActivity(context) != null) {
            getAppCompActivity(context)?.window
        } else {
            scanForActivity(context)?.window
        }
    }

    fun getAppCompActivity(context: Context?): AppCompatActivity? {
        if (context == null) return null
        if (context is AppCompatActivity) {
            return context
        } else if (context is ContextThemeWrapper) {
            return getAppCompActivity(context.baseContext)
        }
        return null
    }

    fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode()) {
                onResume();
            } else {
                bt_play.setImageResource(R.drawable.ic_play);
                stopPosition = videoView.getCurrentPosition(); //stopPosition is an int
                videoView.pause();
                ck_pause = true;
            }
        }else{
            bt_play.setImageResource(R.drawable.ic_play);
            stopPosition = videoView.getCurrentPosition(); //stopPosition is an int
            videoView.pause();
            ck_pause = true;
        }

    }

    // resume video
    override fun onResume() {
        super.onResume()
        bg_replay.visibility = GONE
        bt_play.setImageResource(R.drawable.ic_pause)
        ck_pause = false
        videoView.seekTo(stopPosition)
        videoView.start() //Or use resume() if it doesn't work. I'm not sure
    }

    companion object {
        var keyPlay = 0
        var speed = 1f
        var pos by Delegates.notNull<Int>()
        lateinit var pathVideo: String
    }
}