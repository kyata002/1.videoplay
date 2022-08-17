package com.mtg.videoplay.view.fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.adapter.FolderAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.Folder;
import com.mtg.videoplay.view.activity.HomeActicity;

import java.util.ArrayList;

public class FolderFragment extends BaseFragment {
    private FolderAdapter adapter;
    private HomeActicity homeActicity;
    ArrayList<Folder> folderList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_folder;
    }

    @Override
    protected void addEvent() {

    }

    @Override
    protected void initView() {
        homeActicity = (HomeActicity) getActivity();

        RecyclerView rvFolder = requireActivity().findViewById(R.id.rv_folder);
        rvFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        folderList.addAll(homeActicity.getdataFolder());
        adapter = new FolderAdapter(getContext(),HomeActicity.folders);
        rvFolder.setLayoutManager(linearLayoutManager);
        rvFolder.setAdapter(adapter);

    }
}
