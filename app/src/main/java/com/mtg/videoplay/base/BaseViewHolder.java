package com.mtg.videoplay.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


abstract public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View rootView;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.rootView = itemView;
        initView();
    }

    protected abstract void loadData(Object tag);

    protected abstract void initView();

}
