package com.mtg.videoplay.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.view.activity.SettingActivity;
import com.mtg.videoplay.view.dialog.RenameDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


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



    ArrayList<FileVideo> videoList = new ArrayList<>();
    final ArrayList<FileVideo> videoListSearch = new ArrayList<>();

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
//            key_Search=1;
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
        });

        bt_setting.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SettingActivity.class);
            this.startActivity(intent);
        });


        bt_clear_search.setOnClickListener(view -> ed_Search.setText(""));

    }

    @Override
    protected void initView() {

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
        videoList = getdata();
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

    }



    @SuppressLint("Recycle")
    public ArrayList<FileVideo> getdata() {
        Cursor csr;
        ArrayList<FileVideo> videoListPath = new ArrayList<>();
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID
        };
        csr = Objects.requireNonNull(getActivity()).getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            int idCol = csr.getColumnIndex(MediaStore.Video.Media._ID);
            String path = csr.getString(ind);
            int id = csr.getInt(idCol);
            if (new File(path).exists()&&path.endsWith(".mp4")&&new File(path).length()>1){
                Load_Ads++;
                if(Load_Ads%6==0){
                    videoListPath.add(new FileVideo(path, id));
                    videoListPath.add(null);
                }else{
                    videoListPath.add(new FileVideo(path, id));
                }
                videoListSearch.add(new FileVideo(path, id));
            }


        }
        Load_Ads=0;
        return videoListPath;
    }



    public void Filter(String text) {
        ArrayList<FileVideo> listNew = new ArrayList<>();

        for (int i = 0; i < videoListSearch.size(); i++) {
            if (new File(String.valueOf(videoListSearch.get(i).getPath())).getName().contains(text)) {
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
//        adapter.setHasStableIds(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRename(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                final File file = new File(videoList.get(position).getPath());
                RenameDialog dialog = new RenameDialog(context, videoList.get(position).getPath());
                dialog.setCallback((key, data) -> {
                    String newName = (String) data;
                    if (key.equals("rename")) {

                        String onlyPath = file.getParent();
                        newName = newName + ".mp4";
                        File from = new File(videoList.get(position).getPath());
                        File to = new File(onlyPath, newName);
                        try {
                            to.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (from.exists()) {
                            from.renameTo(to);
                            removeMedia(Objects.requireNonNull(getActivity()), from);
                            addMedia(getActivity(), to);
                            CountDownTimer Timer2 = new CountDownTimer(1050, 1000) {
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
            } else {
                showDialog(requireActivity());
            }
        }else{
            final File file = new File(videoList.get(position).getPath());
            RenameDialog dialog = new RenameDialog(context, videoList.get(position).getPath());
            dialog.setCallback((key, data) -> {
                String newName = (String) data;
                if (key.equals("rename")) {

                    String onlyPath = file.getParent();
                    newName = newName + ".mp4";
//                String renamepath = onlyPath + "/" + newName;
                    File from = new File(videoList.get(position).getPath());
                    File to = new File(onlyPath, newName);
                    try {
                        to.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (from.exists()) {
                        from.renameTo(to);
                        removeMedia(Objects.requireNonNull(getActivity()), from);
                        addMedia(getActivity(), to);
                        CountDownTimer Timer2 = new CountDownTimer(1050, 1000) {
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
        }
    }

    public void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_request_allfile);
        TextView allow = (TextView) dialog.findViewById(R.id.bt_allow);
        TextView deny = (TextView) dialog.findViewById(R.id.bt_deny);
        allow.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                } else { //request for the permission
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", "com.mtg.videoplay", null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        deny.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnDismissListener(dialog1 -> dialog1.dismiss());

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
