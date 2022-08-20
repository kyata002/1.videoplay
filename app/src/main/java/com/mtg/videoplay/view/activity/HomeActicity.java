package com.mtg.videoplay.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.ViewPagerAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HomeActicity extends BaseActivity {
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
//    ArrayList<String> folderName = new ArrayList<>();
//    ArrayList<String> folderThumb = new ArrayList<>();
//    ArrayList<String> folderp = new ArrayList<>();
    public static  ArrayList<Folder> folders = new ArrayList<>();

    public static ArrayList<String> videoList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }



    @Override
    protected void initView() {
        bt_search = findViewById(R.id.bt_search);
        bt_setting = findViewById(R.id.bt_setting);

        getdata();
        getdataFolder();
        tab=findViewById(R.id.tab_Layout);
        viewPager = findViewById(R.id.view_Pager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
//            Log.d("pathofpath", path);
//
//            imageList.add(path);
            String fpath = new File(path).getParent();
            String fname = new File(fpath).getName();
            allfolderpath.add(fpath);
            Collections.sort(allfolderpath);
            if (!folderPath.contains(fpath)) {
                folderPath.add(fpath);
            }
        }

//        for (int i = 0; i < folderPath.size(); i++) {
//            for (int j = 0; j < imageList.size(); j++) {
//                if (folderPath.get(i).equals(new File(imageList.get(j)).getParent())) {
//                    if (!folderThumb.contains(imageList.get(j)))
//                        folderThumb.add(imageList.get(j));
//                    break;
//                }
//            }
//        }
//        for (int i = 0; i < folderPath.size(); i++) {
//            folderName.add(new File(folderPath.get(i)).getName());
//        }
        // Collections.sort(folderName);

//        for (int i = 0; i < folderName.size(); i++) {
//            for (int j = 0; j < folderPath.size(); j++) {
//                if (folderName.get(i).equals(new File(folderPath.get(j)).getName())) {
//                    folderp.add(i, folderPath.get(j));
//                    break;
//                }
//            }
//        }

        for (int i = 0; i < folderPath.size(); i++) {
            int occurrences = Collections.frequency(allfolderpath, folderPath.get(i));
//            videoCount.add(occurrences);
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
}
