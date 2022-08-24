package com.mtg.videoplay.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.ViewPagerAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.Folder;
import com.mtg.videoplay.view.fragment.FolderFragment;
import com.mtg.videoplay.view.fragment.VideoFragment;


public class HomeActicity extends BaseActivity implements View.OnTouchListener {
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tab;
    ViewPager viewPager;
    LinearLayout lc_search,lc_main;
    ImageView bt_search,bt_setting,bt_backs;
    EditText ed_Search;





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
        bt_backs = findViewById(R.id.bt_BackS);
        lc_search = findViewById(R.id.lc_Search);
        lc_main = findViewById(R.id.lc_Main);
        ed_Search = findViewById(R.id.de_search);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        tab=findViewById(R.id.tab_Layout);
        viewPager = findViewById(R.id.view_Pager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragemnt(new VideoFragment(),"Videos");
        viewPagerAdapter.addfragemnt(new FolderFragment(),"Folders");
        viewPager.setAdapter(viewPagerAdapter);
        tab.setupWithViewPager(viewPager);
    }

    @Override
    protected void addEvent() {
        bt_search.setOnClickListener(view -> {
            lc_main.setVisibility(View.GONE);
            lc_search.setVisibility(View.VISIBLE);
        });
        bt_setting.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingActivity.class);
            this.startActivity(intent);
        });
        bt_backs.setOnClickListener(view -> {
            lc_main.setVisibility(View.VISIBLE);
            lc_search.setVisibility(View.GONE);
            ed_Search.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ed_Search.getWindowToken(), 0);
        });

        ed_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
