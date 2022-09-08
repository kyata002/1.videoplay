package com.mtg.videoplay.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.FolderAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FolderFragment extends BaseFragment {
    LinearLayout lr_no_folder;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rvFolder;
    FolderAdapter adapter;

    ArrayList<Folder> folderList = new ArrayList<>();
    final ArrayList<String> allfolderpath = new ArrayList<>();
    final ArrayList<String> folderPath = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_folder;
    }

    @Override
    protected void addEvent() {

    }

    @Override
    protected void initView() {
        folderList = getdataFolder();
        rvFolder = requireActivity().findViewById(R.id.rv_folder);
        lr_no_folder = requireActivity().findViewById(R.id.no_folder);
        if(folderList.size() !=0){
            lr_no_folder.setVisibility(View.GONE);
            rvFolder.setLayoutManager(new LinearLayoutManager(getContext()));
            linearLayoutManager= new LinearLayoutManager(getContext());
            adapter = new FolderAdapter(getContext(), folderList);
            rvFolder.setLayoutManager(linearLayoutManager);
            rvFolder.setAdapter(adapter);
        }else{
            lr_no_folder.setVisibility(View.VISIBLE);
        }


    }
    public void setList(ArrayList<Folder> mlistFolder){
        rvFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayoutManager= new LinearLayoutManager(getContext());
        adapter = new FolderAdapter(getContext(),folderList);
        rvFolder.setLayoutManager(linearLayoutManager);
        rvFolder.setAdapter(adapter);
    }
    public ArrayList<Folder> getdataFolder() {
        int ck_ads = 1;

        String[] proj = new String[]{
                MediaStore.Video.Media.DATA
        };
        Cursor csr = requireActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        folderPath.clear();

        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = csr.getString(ind);
            String fpath = new File(path).getParent();
                allfolderpath.add(fpath);
//            }
            Collections.sort(allfolderpath);

            if (!folderPath.contains(fpath)) {
                if(ck_ads%4!=0){
                    folderPath.add(fpath);
                }else{
                    folderPath.add(null);
                    folderPath.add(fpath);
                    ck_ads=0;
                }
                ck_ads++;
            }
        }
        ck_ads=0;
        for (int i = 0; i < folderPath.size(); i++) {
            if(folderPath.get(i)==null ){
                folderList.add(null);
            }else{
                int occurrences = Collections.frequency(allfolderpath, folderPath.get(i));
                folderList.add(new Folder(folderPath.get(i),occurrences));
            }
        }

        return folderList;
    }
// --Commented out by Inspection START (9/6/2022 9:02 AM):
//    private String[] getAllVideoPath(Context context) {
//        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        String[] projection = {MediaStore.Video.VideoColumns.DATA};
//        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//        ArrayList<String> pathArrList = new ArrayList<String>();
//        if (cursor != null) {
//
//            while (cursor.moveToNext()) {
//                pathArrList.add(cursor.getString(0));
//            }
//            cursor.close();
//        }
//
//        return pathArrList.toArray(new String[pathArrList.size()]);
//    }
// --Commented out by Inspection STOP (9/6/2022 9:02 AM)

    @Override
    public void onResume() {
        super.onResume();
        if(VideoFragment.ck_delete==1){
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
}
