package com.mtg.videoplay.view.activity;

import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;

public class SettingActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void addEvent() {
        findViewById(R.id.bt_back).setOnClickListener(view -> {
            onBackPressed();
        });
        findViewById(R.id.bt_rate).setOnClickListener(view -> {

        });
        findViewById(R.id.bt_feed_back).setOnClickListener(view -> {

        });
        findViewById(R.id.bt_pri).setOnClickListener(view -> {

        });
    }
}
