package com.mtg.videoplay.view.fragment;

import static com.mtg.videoplay.view.activity.HomeActicity.launcherDelete;
import static com.mtg.videoplay.view.activity.HomeActicity.launcherRename;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.utils.FileUtils;
import com.mtg.videoplay.view.activity.SettingActivity;
import com.mtg.videoplay.view.dialog.DeleteDialog;
import com.mtg.videoplay.view.dialog.RenameDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class VideoFragment extends BaseFragment implements AllVideoAdapter.OnClickOptionListener {
    private AllVideoAdapter adapter;
    EditText ed_Search;
    ImageView bt_backs;
    TabLayout tab;
    ViewPager viewPager;
    RecyclerView rvAudio;
    LinearLayout noVideo,lr_No_File;
    LinearLayout lc_search,lc_main;
    ImageView bt_search,bt_setting,bt_clear_search;
    int Load_Ads=0;
    public static int ck_delete = 0,lc_rename;
    public static String newName;
    public static ArrayList<FileVideo> RenameList;


    ArrayList<FileVideo> videoList = new ArrayList<>();
    ArrayList<FileVideo> videoListSearch = new ArrayList<>();

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
        bt_backs.setOnClickListener(view -> {
            lc_main.setVisibility(View.VISIBLE);
            lc_search.setVisibility(View.GONE);
            tab.setVisibility(View.VISIBLE);
            viewPager.setClickable(true);
            ed_Search.setText("");

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ed_Search.getWindowToken(), 0);
            rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAudio.setHasFixedSize(true);
            rvAudio.setItemViewCacheSize(20);
            adapter = new AllVideoAdapter(getContext(), videoList);
            adapter.setOnClickOptionListener(this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
            rvAudio.setLayoutManager(gridLayoutManager);
            rvAudio.setAdapter(adapter);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(adapter.getItemViewType(position) == 1 ){
                        return 2;
                    }else if(adapter.getItemViewType(position) == 0){
                        return 1;
                    }else return -1;
                }
            });
        });
        bt_search.setOnClickListener(view -> {
            lc_main.setVisibility(View.GONE);
            lc_search.setVisibility(View.VISIBLE);
            tab.setVisibility(View.GONE);
            viewPager.setClickable(false);
        });

        bt_setting.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SettingActivity.class);
            this.startActivity(intent);
        });


        bt_clear_search.setOnClickListener(view -> ed_Search.setText(""));

    }

    @Override
    protected void initView() {
        link();
        videoList = getdata();
        if(videoList.size()==0){
            lr_No_File.setVisibility(View.VISIBLE);
        }else{
            lr_No_File.setVisibility(View.GONE);
        }
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setHasFixedSize(true);
        rvAudio.setItemViewCacheSize(40);
        adapter = new AllVideoAdapter(getContext(), videoList);
        adapter.setOnClickOptionListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rvAudio.setLayoutManager(gridLayoutManager);
        rvAudio.setAdapter(adapter);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter.getItemViewType(position) == 1 ){
                    return 2;
                }else if(adapter.getItemViewType(position) == 0){
                    return 1;
                }else return -1;
            }
        });

    }

    private void link() {
        bt_search = requireActivity().findViewById(R.id.bt_search);
        bt_setting = requireActivity().findViewById(R.id.bt_setting);
        tab=requireActivity().findViewById(R.id.tab_Layout);
        viewPager =requireActivity(). findViewById(R.id.view_Pager);
        lc_search = requireActivity().findViewById(R.id.lc_Search);
        lc_main = requireActivity().findViewById(R.id.lc_Main);
        ed_Search = requireActivity().findViewById(R.id.de_search);
        bt_clear_search = requireActivity().findViewById(R.id.bt_clears);
        bt_backs = requireActivity().findViewById(R.id.bt_BackS);
        lr_No_File = requireActivity().findViewById(R.id.no_file);
        ed_Search = requireActivity().findViewById(R.id.de_search);
        rvAudio = requireActivity().findViewById(R.id.rv_video);
        noVideo = requireActivity().findViewById(R.id.no_video_search);
    }


    @SuppressLint("Recycle")
    public ArrayList<FileVideo> getdata() {
        Cursor csr;
        ArrayList<FileVideo> videoListPath = new ArrayList<>();
        videoListPath.clear();
        videoListSearch.clear();
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID
        };
        csr = requireActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            int idCol = csr.getColumnIndex(MediaStore.Video.Media._ID);
            String path = csr.getString(ind);
            int id = csr.getInt(idCol);
            if (new File(path).exists()&&path.endsWith(".mp4")&&new File(path).length()>1){
                videoListSearch.add(new FileVideo(path, id));
            }
        }
        Load_Ads=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            videoListSearch.sort(new Comparator<FileVideo>() {
                @Override
                public int compare(FileVideo fileVideo, FileVideo t1) {
                    return Long.compare(new File(t1.getPath()).lastModified(),new File(fileVideo.getPath()).lastModified());
                }
            });
        }
        for(int i=0;i<videoListSearch.size();i++){
//            Load_Ads++;
            if(i%6==5){
                videoListPath.add(videoListSearch.get(i));
                videoListPath.add(null);
            }else{
                videoListPath.add(videoListSearch.get(i));
            }
        }
        if(videoListSearch.size()%6==0&&videoListSearch.size()!=0){
            videoListPath.remove(videoListPath.size()-1);
        }
        return videoListPath;
    }



    public void Filter(String text) {
        ArrayList<FileVideo> listNew = new ArrayList<>();

        for (int i = 0; i < videoListSearch.size(); i++) {
            if (new File(String.valueOf(videoListSearch.get(i).getPath())).getName().toLowerCase().contains(text.toLowerCase())) {
                listNew.add(videoListSearch.get(i));
            }
        }

        if (listNew.size() != 0) {
            noVideo.setVisibility(View.GONE);
        } else {
            noVideo.setVisibility(View.VISIBLE);
        }
        setList(listNew);

    }

    private void setList(ArrayList<FileVideo> listNew) {
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudio.setHasFixedSize(true);
        rvAudio.setItemViewCacheSize(20);
        adapter = new AllVideoAdapter(getContext(), listNew);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        videoList.clear();
        videoList = getdata();
        adapter.update(videoList);
    }

    @Override
    public void onRename(int position) {
        RenameList = videoList;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                final File file = new File(videoList.get(position).getPath());
                RenameDialog dialog = new RenameDialog(context, videoList.get(position).getPath());
                dialog.setCallback((key, data) -> {
                     newName = (String) data;
                    if (key.equals("rename")) {
                        String onlyPath = file.getParent();
                        newName = newName + ".mp4";
                        lc_rename = position;
                        File from = new File(videoList.get(position).getPath());
                        File to = new File(onlyPath, newName);
                        try {
                            to.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (from.exists()) {
                            from.renameTo(to);
                            removeMedia(requireActivity(), from);
                            addMedia(getActivity(), to);
                            CountDownTimer Timer2 = new CountDownTimer(1100, 1000) {
                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    videoList.clear();
                                    videoList = getdata();
                                    adapter.update(videoList);

                                }
                            }.start();
                        }
                    }
                });
                dialog.show();
        }else{
            final File file = new File(videoList.get(position).getPath());
            RenameDialog dialog = new RenameDialog(context, videoList.get(position).getPath());
            dialog.setCallback((key, data) -> {
                String newName = (String) data;
                if (key.equals("rename")) {

                    FileUtils.rename(context,videoList.get(position), launcherRename);
                    File file2 = new File(videoList.get(position).getPath());
                    MediaScannerConnection.scanFile(context,
                            new String[]{file2.toString()},
                            null, null);
                    adapter.notifyDataSetChanged();
                    onResume();
                }
            });
            dialog.show();

        }
    }

    @Override
    public void onDelete(int position) {
        DeleteDialog dialog = new DeleteDialog(context);
//        this.position = position;

        dialog.setCallback((key, data) -> {
            if (key.equals("delete")) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    File file = new File(videoList.get(position).getPath());
                    file.delete();
                    MediaScannerConnection.scanFile(context,
                            new String[]{file.toString()},
                            null, null);
                    adapter.notifyDataSetChanged();
                    if (file.exists()) {
                        try {
                            file.getCanonicalFile().delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (file.exists()) {
                            context.deleteFile(file.getName());
                        }
                        videoList.remove(videoList.get(position));
//                    notifyItemRemoved(videoList.indexOf(videoList.get(position)));
                    } else {
                        videoList.remove(videoList.get(position));
//                    notifyItemRemoved(videoList.indexOf(videoList.get(position)));
                    }
                    adapter.notifyDataSetChanged();
                    onResume();
                } else {
                    FileUtils.deleteFileAndroid11((AppCompatActivity) context, videoList.get(position), launcherDelete);
                    File file2 = new File(videoList.get(position).getPath());
                    MediaScannerConnection.scanFile(context,
                            new String[]{file2.toString()},
                            null, null);
                    videoList.remove(videoList.get(position));
                    adapter.notifyDataSetChanged();
                    onResume();
                }
                ck_delete = 1;


            }
            if (key.equals("no")) {

            }
        });
        dialog.show();
    }



    private static void removeMedia(Context c, File f) {
        ContentResolver resolver = c.getContentResolver();
        resolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA + "=?", new String[]{f.getAbsolutePath()});
    }

    public static void addMedia(Context c, File f) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(f));
        c.sendBroadcast(intent);
    }
}
