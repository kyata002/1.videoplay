package com.mtg.videoplay;


import com.common.control.MyApplication;

public class App extends MyApplication {
    @Override
    protected void onApplicationCreate() {

    }

    @Override
    protected boolean hasAds() {
        return true;
    }

    @Override
    protected boolean isShowDialogLoadingAd() {
        return false;
    }

    @Override
    protected boolean isShowAdsTest() {
        return true;
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public String getOpenAppAdId() {
        return BuildConfig.open_app;
    }
}
