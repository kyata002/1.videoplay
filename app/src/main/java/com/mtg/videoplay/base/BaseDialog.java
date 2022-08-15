package com.mtg.videoplay.base;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

abstract public class BaseDialog extends Dialog {
    public static final int SUB_MONTH = 0;
    public static final int SUB_WEEK = 1;
    public static final int LIFE_TIME = 2;
    protected int selectPosition = SUB_WEEK;

    protected int time = 5;
    protected boolean isClose;

    public BaseDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

}
