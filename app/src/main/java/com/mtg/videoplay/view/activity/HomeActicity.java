package com.mtg.videoplay.view.activity;

import android.app.ActionBar;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.ViewPagerAdapter;
import com.mtg.videoplay.base.BaseActivity;

public class HomeActicity extends BaseActivity {
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tab;
    ViewPager viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }



    @Override
    protected void initView() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        tab=findViewById(R.id.tab_Layout);
        viewPager = findViewById(R.id.view_Pager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        tab.setupWithViewPager(viewPager);
    }

    @Override
    protected void addEvent() {

    }
}
