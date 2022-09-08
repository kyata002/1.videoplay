package com.mtg.videoplay.view.activity;


import static com.mtg.videoplay.utils.FileUtils.requestLauncher;
import static com.mtg.videoplay.view.dialog.DialogChange.context;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.common.control.interfaces.RateCallback;
import com.common.control.manager.AdmobManager;
import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.BuildConfig;
import com.mtg.videoplay.R;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.utils.SharePreferenceUtils;
import com.mtg.videoplay.utils.Utils;
import com.mtg.videoplay.adapter.ViewPagerAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.view.fragment.FolderFragment;
import com.mtg.videoplay.view.fragment.VideoFragment;

import java.io.File;


public class HomeActicity extends BaseActivity  {
    ViewPagerAdapter viewPagerAdapter;
    LinearLayout per_Deny;
    TabLayout tab;
    ViewPager viewPager;
    TextView rq_Permission;
    private boolean ck_request = false;
    int ck_quit=0;

    final DisplayMetrics displayMetrics = new DisplayMetrics();
    public static ActivityResultLauncher<IntentSenderRequest> launcherDelete;
    public static ActivityResultLauncher<IntentSenderRequest> launcherRename;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        rq_Permission = findViewById(R.id.rq_Permission);
        per_Deny = findViewById(R.id.per_Deny);
        viewPager = findViewById(R.id.view_Pager);
        if(ck_request==false){
            checkPermission();
        }
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_main);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        tab=findViewById(R.id.tab_Layout);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragemnt(new VideoFragment(),"Videos");
        viewPagerAdapter.addfragemnt(new FolderFragment(),"Folders");
        viewPager.setAdapter(viewPagerAdapter);
        tab.setupWithViewPager(viewPager);
        launcherDelete = requestLauncher(this, (key1,data) -> {

        });
        launcherRename = requestLauncher(this, (key1,data) -> {
            if(key1.equals("key1")){
                FileVideo media = VideoFragment.RenameList.get(VideoFragment.lc_rename);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, VideoFragment.newName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.getContentResolver().update(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId()), contentValues, null);
                }
            }
        });
    }

    @Override
    protected void addEvent() {
        rq_Permission.setOnClickListener(view -> {
            checkPermission();
        });
    }
    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                viewPager.setVisibility(View.GONE);
                per_Deny.setVisibility(View.VISIBLE);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permission2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = this.checkCallingOrSelfPermission(permission);
        int res2 = this.checkCallingOrSelfPermission(permission2);
        if (res == PackageManager.PERMISSION_GRANTED && res2 == PackageManager.PERMISSION_GRANTED) {
            String path = Environment.getExternalStorageDirectory()
                    + File.separator + Utils.FOLDER_NAME;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)||!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    viewPager.setVisibility(View.VISIBLE);
                    per_Deny.setVisibility(View.GONE);
                    ck_request=true;

                } else {

//                Toast.makeText(this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
//                finish();
                    viewPager.setVisibility(View.GONE);
                    per_Deny.setVisibility(View.VISIBLE);
                }
                return;
            }
        }else{

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!SharePreferenceUtils.isRated(this)){
            showExitDialog();
        }else{

            ck_quit++;
            if(ck_quit==1){
                Toast.makeText(HomeActicity.this, "Press again to exit the app", Toast.LENGTH_SHORT).show();
            }else{
                QuitApp();
            }
        }
    }
    private void showExitDialog() {
        com.common.control.dialog.RateAppDialog rateAppDialog = new com.common.control.dialog.RateAppDialog(this);
        rateAppDialog.setCallback(new RateCallback() {
            @Override
            public void onMaybeLater() {
                QuitApp();
            }

            @Override
            public void onSubmit(String review) {
                com.mtg.videoplay.utils.SharePreferenceUtils.setRated(HomeActicity.this);
//                    Toast.makeText(MainActivity.this, review, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRate() {
                com.common.control.utils.CommonUtils.getInstance().rateApp(HomeActicity.this);
                com.mtg.videoplay.utils.SharePreferenceUtils.setRated(HomeActicity.this);
            }
        });
        rateAppDialog.show();

//            QuitApp();

    }
    public void QuitApp() {
        HomeActicity.this.finish();
        System.exit(0);
    }
}
