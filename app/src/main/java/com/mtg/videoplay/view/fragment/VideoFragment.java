package com.mtg.videoplay.view.fragment;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class VideoFragment extends BaseFragment {
    private AllVideoAdapter adapter;
    private Cursor csr;
    boolean isFromFolder;
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
        getdata();
        RecyclerView rvAudio = requireActivity().findViewById(R.id.rv_video);
        rvAudio.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AllVideoAdapter(new VideoFragment(),videoList);
        rvAudio.setAdapter(adapter);

    }
    void getdata() {
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
    }
}
