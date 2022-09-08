package com.mtg.videoplay;


import com.common.control.MyApplication;

public class App extends MyApplication {
    private static App instance;

    public static App getInstance() {
        if (instance == null)
            instance = new App();
        return instance;
    }

    private static void setInstance(App instance) {
        App.instance = instance;
    }

    @Override
    protected void onApplicationCreate() {
        if (instance == null)
            setInstance(App.this);
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
        return BuildConfig.TEST_AD || BuildConfig.DEBUG;
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
