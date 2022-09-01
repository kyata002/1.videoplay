package com.mtg.videoplay.view.fragment;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseFragment;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.view.dialog.RenameDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class VideoFragment extends BaseFragment implements AllVideoAdapter.OnClickOptionListener {
    private AllVideoAdapter adapter;
    LinearLayout lr_No_File;

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
        adapter.setOnClickOptionListener(this);
//        adapter.setHasStableIds(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rvAudio.setLayoutManager(linearLayoutManager);
        rvAudio.setAdapter(adapter);
    }

    public ArrayList<FileVideo> getdata() {
        Cursor csr;
        ArrayList<FileVideo> videoListPath = new ArrayList<>();
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID
        };
        csr = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        while (csr.moveToNext()) {
            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
            int idCol = csr.getColumnIndex(MediaStore.Video.Media._ID);
            String path = csr.getString(ind);
            int id = csr.getInt(idCol);
//            Log.d("pathofpath", path);
            if (new File(path).exists()&&path.endsWith(".mp4"))
                videoListPath.add(new FileVideo(path, id));

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
        if (videoList.size() != 0) {
            lr_No_File.setVisibility(View.GONE);
            setList(videoList);
        } else {
            lr_No_File.setVisibility(View.VISIBLE);
            setList(videoList);
        }
    }

    @Override
    public void onRename(int position) {
//        showDialog(requireActivity(),videoList.get(position).getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
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
                            removeMedia(getActivity(), from);
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
                        removeMedia(getActivity(), from);
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
        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                    } else { //request for the permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", "com.mtg.videoplay", null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private ArrayList<String> FetchFolder(String path) {

        ArrayList<String> filenames = new ArrayList<String>();

        File directory = new File(path);
        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++) {

            String file_name = files[i].getName();
            // you can store name to arraylist and use it later
            filenames.add(file_name);
        }
        return filenames;
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
