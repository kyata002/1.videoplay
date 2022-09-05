package com.mtg.videoplay.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.common.control.manager.AdmobManager;
import com.mtg.videoplay.BuildConfig;
import com.mtg.videoplay.R;

public class AdsHolderFolder extends RecyclerView.ViewHolder{


    public AdsHolderFolder(@NonNull View itemView) {
        super(itemView);
        AdmobManager.getInstance().loadNative(itemView.getContext(),BuildConfig.native_folder,itemView.findViewById(R.id.fakeAdsNative),com.common.control.R.layout.custom_native);

    }
}
