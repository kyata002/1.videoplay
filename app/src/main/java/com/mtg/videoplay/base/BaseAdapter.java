package com.mtg.videoplay.base;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.voicerecoder.StorageCommon;
import com.mtg.voicerecoder.edit.OnActionCallback;

import java.util.List;

abstract public class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected OnActionCallback mCallback;
    protected List<T> mList;
    protected Context context;

    public BaseAdapter(List<T> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    public void setmCallback(OnActionCallback mCallback) {
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolder(parent, viewType);
    }

    protected abstract RecyclerView.ViewHolder viewHolder(ViewGroup parent, int viewType);


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        onBindView(viewHolder, position);
    }

    protected abstract void onBindView(RecyclerView.ViewHolder viewHolder, int position);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    protected StorageCommon getStorageCommon() {
        return StorageCommon.getInstance();
    }

}
