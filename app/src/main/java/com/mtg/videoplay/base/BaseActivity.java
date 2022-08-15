package com.mtg.videoplay.base;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.mtg.voicerecoder.AdConstant;
import com.mtg.voicerecoder.StorageCommon;
import com.mtg.voicerecoder.edit.OnActionCallback;
import com.mtg.voicerecoder.presenter.BasePresenter;

public abstract class BaseActivity<T extends BasePresenter,
        V extends ViewDataBinding> extends AppCompatActivity implements OnActionCallback, AdConstant {
    protected T mPresenter;
    protected V binding;

    protected String[] getPermission() {
        return new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,/*Manifest.permission.READ_PHONE_STATE*/};
    }

    protected StorageCommon getStorageCommon() {
        return StorageCommon.getInstance();
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = DataBindingUtil.setContentView(this, getLayoutId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (binding == null) {
            return;
        }

        initPresenter();
        initView();
        addEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected abstract int getLayoutId();

    protected abstract void initPresenter();

    protected abstract void initView();

    protected abstract void addEvent();


    @Override
    public void callback(String key, Object data) {

    }
}
