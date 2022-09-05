package com.mtg.videoplay.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.common.control.manager.AdmobManager;
import com.mtg.videoplay.BuildConfig;
import com.mtg.videoplay.R;

public class AdsHolderFile extends RecyclerView.ViewHolder{


    public AdsHolderFile(@NonNull View itemView) {
        super(itemView);
        AdmobManager.getInstance().loadNative(itemView.getContext(),BuildConfig.native_list_file,itemView.findViewById(R.id.fakeAdsNative),com.common.control.R.layout.custom_native);

    }
}
