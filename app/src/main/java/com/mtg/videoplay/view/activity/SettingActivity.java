package com.mtg.videoplay.view.activity;

import static com.mtg.videoplay.view.dialog.DialogChange.context;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.common.control.interfaces.RateCallback;
import com.common.control.utils.CommonUtils;
import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseActivity;

public class SettingActivity extends BaseActivity {
    public static final String POLICY_URL = "https://firebasestorage.googleapis.com/v0/b/compass-app-df4f4.appspot.com/o/Privacy_Policy_ExcelReader.html?alt=media&token=fcb1f0bf-52ce-4b87-b845-b59f8ee0f9d8";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void addEvent() {
        findViewById(R.id.bt_back).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.bt_rate).setOnClickListener(view ->{
            com.common.control.dialog.RateAppDialog rateAppDialog = new com.common.control.dialog.RateAppDialog(this);
            rateAppDialog.setCallback(new RateCallback() {
                @Override
                public void onMaybeLater() {

                }

                @Override
                public void onSubmit(String review) {
                    com.mtg.videoplay.utils.SharePreferenceUtils.setRated(SettingActivity.this);
//                    Toast.makeText(SettingActivity.this, review, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRate() {
                    com.common.control.utils.CommonUtils.getInstance().rateApp(SettingActivity.this);
                    com.mtg.videoplay.utils.SharePreferenceUtils.setRated(SettingActivity.this);
                }
            });
            rateAppDialog.show();
        });
        findViewById(R.id.bt_feed_back).setOnClickListener(view -> CommonUtils.getInstance().support(this, com.mtg.videoplay.utils.CommonUtils.SUBJECT, com.mtg.videoplay.utils.CommonUtils.EMAIL));
        findViewById(R.id.bt_pri).setOnClickListener(view -> CommonUtils.getInstance().showPolicy(this, com.mtg.videoplay.utils.CommonUtils.POLICY_URL));
        findViewById(R.id.bt_share_app).setOnClickListener(view -> CommonUtils.getInstance().shareApp(this, com.mtg.videoplay.utils.CommonUtils.SUBJECT));

    }
    public void rateApp(Context context) {
        try {
            context.startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + context.getPackageName())
                    )
            );
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())
                    )
            );
        }
    }
}
