package com.mtg.videoplay.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.adapter.FolderAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.Folder;
import com.mtg.videoplay.view.activity.HomeActicity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FolderFragment extends BaseFragment {
    private FolderAdapter adapter;
    private Cursor csr;
    String[] allvidFile;
    LinearLayout lr_no_folder;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rvFolder;

    ArrayList<Folder> folderList = new ArrayList<>();
    ArrayList<String> allfolderpath = new ArrayList<>();
    ArrayList<String> folderPath = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_folder;
    }

    @Override
    protected void addEvent() {

    }

    @Override
    protected void initView() {
        rvFolder = requireActivity().findViewById(R.id.rv_folder);
        lr_no_folder = requireActivity().findViewById(R.id.no_folder);

    }
    public void setList(ArrayList<Folder> mlistFolder){
        rvFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayoutManager= new LinearLayoutManager(getContext());
        adapter = new FolderAdapter(getContext(),folderList);
        rvFolder.setLayoutManager(linearLayoutManager);
        rvFolder.setAdapter(adapter);
    }
    public ArrayList<Folder> getdataFolder() {
//        allvidFile = getAllVideoPath(getActivity());
//
//        for (int i = 0; i < allvidFile.length; i++) {
//            allfolderpath.add(allvidFile[i]);
//        }

        String[] proj = new String[]{
                MediaStore.Video.Media.DATA
        };
        csr = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        folderPath.clear();

        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = csr.getString(ind);
            String fpath = new File(path).getParent();
//            if (!allfolderpath.contains(fpath)) {
                allfolderpath.add(fpath);
//            }
//            allfolderpath.add(fpath);
            Collections.sort(allfolderpath);

            if (!folderPath.contains(fpath)) {
                folderPath.add(fpath);
            }
        }

        for (int i = 0; i < folderPath.size(); i++) {
            int occurrences = Collections.frequency(allfolderpath, folderPath.get(i));
            folderList.add(new Folder(folderPath.get(i),occurrences));
        }

        return folderList;
    }
    private String[] getAllVideoPath(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        ArrayList<String> pathArrList = new ArrayList<String>();
        if (cursor != null) {

            while (cursor.moveToNext()) {
                pathArrList.add(cursor.getString(0));
            }
            cursor.close();
        }

        return pathArrList.toArray(new String[pathArrList.size()]);
    }

    @Override
    public void onResume() {
        super.onResume();
        folderList.clear();
        allfolderpath.clear();
        folderList = getdataFolder();
        if(folderList.size() !=0){
            lr_no_folder.setVisibility(View.GONE);
            setList(folderList);
        }else{
            lr_no_folder.setVisibility(View.VISIBLE);
            setList(folderList);
        }
    }
}
