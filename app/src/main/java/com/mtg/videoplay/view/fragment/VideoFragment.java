package com.mtg.videoplay.view.fragment;

import android.database.Cursor;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.view.activity.HomeActicity;

import java.util.ArrayList;


public class VideoFragment extends BaseFragment {
    private AllVideoAdapter adapter;
    private HomeActicity homeActicity;

    ArrayList<String> videoList = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void addEvent() {

    }

    @Override
    protected void initView() {
        homeActicity = (HomeActicity) getActivity();
//        getdata();
        RecyclerView rvAudio = requireActivity().findViewById(R.id.rv_video);
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
//        videoList.addAll(homeActicity.getdata());
        adapter = new AllVideoAdapter(getContext(),HomeActicity.videoList);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);

    }
}
