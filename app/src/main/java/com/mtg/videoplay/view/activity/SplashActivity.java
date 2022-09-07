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

    private InterstitialAd interAds = null;

    @Override
    protected void initView() {
        loadInter();
    }

    private void reques() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                runOnUiThread(() ->         showInter()

                );

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadInter() {
        AdmobManager.getInstance()
                .loadInterAds(this, BuildConfig.inter_open_app, new AdCallback() {

                    @Override
                    public void onResultInterstitialAd(InterstitialAd interstitialAd) {
                        super.onResultInterstitialAd(interstitialAd);
                        interAds = interstitialAd;
                        reques();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(LoadAdError errAd) {
                        super.onAdFailedToShowFullScreenContent(errAd);
                        reques();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        reques();

                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        reques();

                    }

                });
    }

    private void showInter() {
        AdmobManager.getInstance().showInterstitial(this, interAds, new AdCallback() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                showMain();

            }

            @Override
            public void onAdFailedToShowFullScreenContent(LoadAdError errAd) {
                super.onAdFailedToShowFullScreenContent(errAd);
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
