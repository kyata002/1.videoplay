package com.mtg.videoplay.view.activity;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.common.control.interfaces.AdCallback;
import com.common.control.manager.AdmobManager;
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

        reques();

    }

    private void reques() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadInter();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void loadInter() {
        AdmobManager.getInstance()
                .loadInterAds(this, BuildConfig.inter_open_app, new AdCallback() {
                    @Override
                    public void onResultInterstitialAd(InterstitialAd interstitialAd) {
                        super.onResultInterstitialAd(interstitialAd);
                        AdmobManager.getInstance().showInterstitial(SplashActivity.this, interstitialAd, this);
                        showMain();
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
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        showMain();
                    }

                });
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
