package com.mtg.videoplay.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.mtg.videoplay.OnActionCallback;
import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseDialog;

public class InfoDialog extends BaseDialog {
    public InfoDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.bt_ok).setOnClickListener(v -> {
            dismiss();
        });
//
    }
}
