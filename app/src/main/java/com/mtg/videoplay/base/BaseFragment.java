package com.mtg.videoplay.base;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.mtg.voicerecoder.presenter.BasePresenter;

abstract public class BaseFragment<T extends BasePresenter,V extends ViewDataBinding> extends Fragment {
    protected Context context;
    protected T mPresenter;
    protected V binding;


    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        addEvent();
        initPresenter();
    }

    protected abstract void initPresenter();

    protected abstract int getLayoutId();

    protected abstract void addEvent();


    protected abstract void initView();

    protected String[] getPermission() {
        return new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,/*Manifest.permission.READ_PHONE_STATE*/};
    }


}
