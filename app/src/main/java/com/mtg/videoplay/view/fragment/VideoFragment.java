package com.mtg.videoplay.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.view.activity.HomeActicity;
import com.mtg.videoplay.view.dialog.InfoDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class VideoFragment extends BaseFragment  {
    private AllVideoAdapter adapter;
    private Cursor csr;
    EditText ed_Search;
    static boolean isFromFolder;

    public PopupWindow popupWindow;

    ArrayList<String> videoList = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void addEvent() {
        ed_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void initView() {
        ed_Search = requireActivity().findViewById(R.id.de_search);
        RecyclerView rvAudio = requireActivity().findViewById(R.id.rv_video);
        videoList=  getdata();
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        setList(videoList);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);


    }
    public void setList(ArrayList<String> videoList){
        adapter = new AllVideoAdapter(getContext(),videoList);
    }
    public ArrayList<String> getdata() {
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA
        };
        csr = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = csr.getString(ind);
            Log.d("pathofpath", path);
            if (isFromFolder) {
                if (new File(path).getParent().equals(getActivity().getIntent().getStringExtra("foldername"))) {
                    if (!videoList.contains(path)) {
                        videoList.add(path);
                    }
                }
            } else {
                videoList.add(path);
            }
        }
        Collections.sort(videoList);
        return videoList;
    }



}
