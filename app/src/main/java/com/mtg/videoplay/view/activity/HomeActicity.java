package com.mtg.videoplay.view.activity;


import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.FileUtils;
import com.mtg.videoplay.adapter.ViewPagerAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.view.fragment.FolderFragment;
import com.mtg.videoplay.view.fragment.VideoFragment;


public class HomeActicity extends BaseActivity  {
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tab;
    ViewPager viewPager;
    LinearLayout lc_search,lc_main;;
    ImageView bt_search,bt_setting,bt_backs,bt_clear_search;
    EditText ed_Search;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    public static ActivityResultLauncher<IntentSenderRequest> launcher;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        bt_search = findViewById(R.id.bt_search);
        bt_setting = findViewById(R.id.bt_setting);
        bt_backs = findViewById(R.id.bt_BackS);
        lc_search = findViewById(R.id.lc_Search);
        lc_main = findViewById(R.id.lc_Main);
        ed_Search = findViewById(R.id.de_search);
        bt_clear_search = findViewById(R.id.bt_clears);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        tab=findViewById(R.id.tab_Layout);
        viewPager = findViewById(R.id.view_Pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragemnt(new VideoFragment(),"Videos");
        viewPagerAdapter.addfragemnt(new FolderFragment(),"Folders");
        viewPager.setAdapter(viewPagerAdapter);
        tab.setupWithViewPager(viewPager);
        launcher = FileUtils.requestLauncher(this, (key1, data) -> {
//            videoList.remove(position);
//            notifyItemRemoved(position);
        });
    }

    @Override
    protected void addEvent() {

        bt_search.setOnClickListener(view -> {
            lc_main.setVisibility(View.GONE);
            lc_search.setVisibility(View.VISIBLE);
            tab.setVisibility(View.GONE);
            viewPager.setClickable(false);
        });

        bt_setting.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingActivity.class);
            this.startActivity(intent);
        });

        bt_backs.setOnClickListener(view -> {
            lc_main.setVisibility(View.VISIBLE);
            lc_search.setVisibility(View.GONE);
            tab.setVisibility(View.VISIBLE);
            viewPager.setClickable(true);
            ed_Search.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ed_Search.getWindowToken(), 0);
        });
        bt_clear_search.setOnClickListener(view -> {
            ed_Search.setText("");
        });


    }


}
