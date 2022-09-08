package com.mtg.videoplay.view.activity;

import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.common.control.interfaces.AdCallback;
import com.common.control.manager.AdmobManager;
import com.common.control.manager.AppOpenManager;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.mtg.videoplay.BuildConfig;
import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        request();
        AppOpenManager.getInstance().disableAppResume();
    }

    private void request() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AdmobManager.getInstance()
                        .loadInterAds(SplashActivity.this, BuildConfig.inter_open_app, new AdCallback() {
                            @Override
                            public void onResultInterstitialAd(InterstitialAd interstitialAd) {
                                super.onResultInterstitialAd(interstitialAd);
                                AdmobManager.getInstance().showInterstitial(SplashActivity.this, interstitialAd, this);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(LoadAdError errAd) {
                                super.onAdFailedToShowFullScreenContent(errAd);
                                showMain();
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError i) {
                                super.onAdFailedToLoad(i);
                                showMain();
                            }

                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                                showMain();
                            }
                        });
            }
        }, 2000);
    }

    private void showMain() {
        Intent i = new Intent(SplashActivity.this, HomeActicity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void addEvent() {

    }
}
