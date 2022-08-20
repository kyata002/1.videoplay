package com.mtg.videoplay.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.mtg.videoplay.OnActionCallback;
import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseDialog;

public class RenameDialog extends BaseDialog {
    public void setCallback(OnActionCallback callback) {
        this.callback = callback;
    }
    public RenameDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_rename);
    }
    private OnActionCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText edit_name;
        edit_name = findViewById(R.id.edt_name);
        findViewById(R.id.bt_save).setOnClickListener(v -> {
            if (!edit_name.getText().toString().trim().isEmpty()) {
                callback.callback("rename", edit_name.getText().toString());
            } else {
                edit_name.setError("not empty this field!");
            }
            dismiss();
        });
        findViewById(R.id.bt_cancel).setOnClickListener(v -> {
            dismiss();
        });
//        Admod.getInstance().loadNative((Activity) mContext
//                , findViewById(com.ads.control.R.id.fl_adplaceholder)
//                , findViewById(com.ads.control.R.id.shimmer_container),
//                mContext.getString(R.string.admod_native));
    }
}
