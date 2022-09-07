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

import java.io.File;

public class RenameDialog extends BaseDialog {
    EditText edit_name;
    final String pathName;
    public static String nameNew;
    public void setCallback(OnActionCallback callback) {
        this.callback = callback;
    }
    public RenameDialog(@NonNull Context context,String path) {
        super(context);
        this.pathName = path;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_rename);
    }
    private OnActionCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edit_name = findViewById(R.id.edt_name);
        edit_name.setText( new File(pathName).getName());
        findViewById(R.id.bt_save).setOnClickListener(v -> {
            if (!edit_name.getText().toString().trim().isEmpty()) {
                nameNew = edit_name.getText().toString();
                callback.callback("rename", edit_name.getText().toString());
            } else {
                edit_name.setError("not empty this field!");
            }
            dismiss();
        });
        findViewById(R.id.bt_cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.btn_Clear).setOnClickListener(view -> edit_name.setText(""));
    }
}
