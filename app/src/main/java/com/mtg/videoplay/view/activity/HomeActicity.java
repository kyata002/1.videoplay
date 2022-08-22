package com.mtg.videoplay.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.ViewPagerAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.Folder;
import com.mtg.videoplay.view.fragment.FolderFragment;
import com.mtg.videoplay.view.fragment.VideoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HomeActicity extends BaseActivity implements View.OnTouchListener {
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tab;
    ViewPager viewPager;
    static boolean isFromFolder;

    ImageView bt_search,bt_setting;
    private  Cursor csr;
    String[] allvidFile;


    ArrayList<String> allfolderpath = new ArrayList<>();
    ArrayList<String> imageList = new ArrayList<>();
    ArrayList<String> folderPath = new ArrayList<>();
    public static  ArrayList<Folder> folders = new ArrayList<>();
    public static ArrayList<String> videoList = new ArrayList<>();
    public static float x,y;
    public static int width,height;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }



    @Override
    protected void initView() {
        bt_search = findViewById(R.id.bt_search);
        bt_setting = findViewById(R.id.bt_setting);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        getdata();
        getdataFolder();
        tab=findViewById(R.id.tab_Layout);
        viewPager = findViewById(R.id.view_Pager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragemnt(new VideoFragment(),"Video");
        viewPagerAdapter.addfragemnt(new FolderFragment(),"Folder");
        viewPager.setAdapter(viewPagerAdapter);
        tab.setupWithViewPager(viewPager);
    }

    @Override
    protected void addEvent() {
        bt_search.setOnClickListener(view -> {

        });
        bt_setting.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingActivity.class);
            this.startActivity(intent);
        });


    }
    public Collection<? extends String> getdata() {
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA
        };
        csr = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = csr.getString(ind);
            Log.d("pathofpath", path);
            if (isFromFolder) {
                if (new File(path).getParent().equals(this.getIntent().getStringExtra("foldername"))) {
                    if (!videoList.contains(path)) {
                        videoList.add(path);
                    }
                }
            } else {
                videoList.add(path);
            }
        }
        Collections.sort(videoList);
        return videoList;
    }


    public ArrayList<Folder> getdataFolder() {
        allvidFile = getAllVideoPath(this);

        for (int i = 0; i < allvidFile.length; i++) {
            allfolderpath.add(allvidFile[i]);
        }

        String[] proj = new String[]{
                MediaStore.Video.Media.DATA
        };
        csr = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);

        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = csr.getString(ind);
            String fpath = new File(path).getParent();
            allfolderpath.add(fpath);
            Collections.sort(allfolderpath);

            if (!folderPath.contains(fpath)) {
                folderPath.add(fpath);
            }
        }

        for (int i = 0; i < folderPath.size(); i++) {
            int occurrences = Collections.frequency(allfolderpath, folderPath.get(i));
            folders.add(new Folder(folderPath.get(i),occurrences));
        }

        return folders;
    }
    private String[] getAllVideoPath(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        ArrayList<String> pathArrList = new ArrayList<String>();
        if (cursor != null) {

            while (cursor.moveToNext()) {
                pathArrList.add(cursor.getString(0));
            }
            cursor.close();
        }

        return pathArrList.toArray(new String[pathArrList.size()]);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            x= view.getX();
            y=view.getY();
        }
        return false;
    }
}
