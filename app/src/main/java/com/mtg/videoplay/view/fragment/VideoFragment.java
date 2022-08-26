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


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.FileUtils;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.view.activity.HomeActicity;
import com.mtg.videoplay.view.dialog.InfoDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class VideoFragment extends BaseFragment {
    private AllVideoAdapter adapter;
    LinearLayout lr_No_File;
    private Cursor csr;
    EditText ed_Search;
    RecyclerView rvAudio;
    LinearLayout noVideo;
    static boolean isFromFolder;

    public PopupWindow popupWindow;


    ArrayList<FileVideo> videoList = new ArrayList<>();

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
                String textFilter = ed_Search.getText().toString();
                Filter(textFilter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void initView() {
        lr_No_File = requireActivity().findViewById(R.id.no_file);
        ed_Search = requireActivity().findViewById(R.id.de_search);
        rvAudio = requireActivity().findViewById(R.id.rv_video);
        noVideo = requireActivity().findViewById(R.id.no_video_search);
//        videoList=  adddate(getdata());
//        setList(videoList);

    }

    public void setList(ArrayList<FileVideo> videoList) {
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setHasFixedSize(true);
        rvAudio.setItemViewCacheSize(20);
        adapter = new AllVideoAdapter(getContext(), videoList);
//        adapter.setHasStableIds(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);
    }

    public ArrayList<FileVideo> getdata() {
        ArrayList<FileVideo> videoListPath = new ArrayList<>();
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA,MediaStore.Video.Media._ID
        };
        csr = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            int idCol = csr.getColumnIndex(MediaStore.Video.Media._ID);
            String path = csr.getString(ind);
            int id = csr.getInt(idCol);
            Log.d("pathofpath", path);
            if(new File(path).exists())
            videoListPath.add(new FileVideo(path,id));

        }
        return videoListPath;
    }

    public ArrayList<FileVideo> adddate(ArrayList<String> videoListPath) {
        ArrayList<FileVideo> mFile = new ArrayList<>();
        for (int i = 0; i < videoListPath.size(); i++) {
            FileVideo file = new FileVideo(videoListPath.get(i), i);
            mFile.add(file);
        }
        return mFile;
    }

    public void Filter(String text) {
        ArrayList<FileVideo> listNew = new ArrayList<>();

        for (int i = 0; i < videoList.size(); i++) {
            if (new File(String.valueOf(videoList.get(i).getPath())).getName().contains(text)) {
                listNew.add(videoList.get(i));
            }
        }

        if (listNew.size() != 0) {
            noVideo.setVisibility(View.GONE);
            setList(listNew);
        } else {
            noVideo.setVisibility(View.VISIBLE);
            setList(listNew);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        videoList.clear();
        videoList = getdata();
        if(videoList.size() !=0)
        {
            lr_No_File.setVisibility(View.GONE);
            setList(videoList);
        }else{
            lr_No_File.setVisibility(View.VISIBLE);
            setList(videoList);
        }
    }
}
