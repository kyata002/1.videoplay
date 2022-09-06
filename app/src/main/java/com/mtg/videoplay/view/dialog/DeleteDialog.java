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

public class DeleteDialog extends BaseDialog {
    private OnActionCallback callback;


    public void setCallback(OnActionCallback callback) {
        this.callback = callback;
    }
    public DeleteDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_delete);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.bt_yes).setOnClickListener(v -> {
            callback.callback("delete", null);
            dismiss();
        });
        findViewById(R.id.bt_no).setOnClickListener(v -> {
            callback.callback("no", null);
            dismiss();
        });
    }
}
