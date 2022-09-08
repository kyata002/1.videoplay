package com.mtg.videoplay.view.activity;

import static com.mtg.videoplay.view.activity.HomeActicity.launcherDelete;
import static com.mtg.videoplay.view.dialog.DialogChange.context;

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
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common.control.manager.AdmobManager;
import com.mtg.videoplay.BuildConfig;
import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.FileFolderAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.utils.FileUtils;
import com.mtg.videoplay.view.dialog.DeleteDialog;
import com.mtg.videoplay.view.dialog.RenameDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VideoListActivity extends BaseActivity implements FileFolderAdapter.OnClickOption1Listener {
    Cursor cursor;
    ImageView bt_back;
    TextView txtNameFolder;
    private FileFolderAdapter adapter;
    ArrayList<FileVideo> mFile;
    int Load_Ads =0;
    public static int ck_delete=0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_videolist;
    }


    @Override
    protected void initView() {
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_detail_folder);
        bt_back = findViewById(R.id.bt_back);
        txtNameFolder = findViewById(R.id.folderName);
        mFile = getdata();
        RecyclerView rv_videol = findViewById(R.id.rv_videol);
        rv_videol.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        adapter = new FileFolderAdapter(this, mFile);
        adapter.setOnClickOption1Listener(this);
        rv_videol.setLayoutManager(gridLayoutManager);
        rv_videol.setAdapter(adapter);
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

    @Override
    protected void addEvent() {
        bt_back.setOnClickListener(view -> onBackPressed());

    }

    public ArrayList<FileVideo> getdata() {
        ArrayList<FileVideo> fileFolder = new ArrayList<>();
        String folder = getIntent().getStringExtra("folder");
        txtNameFolder.setText(new File(folder).getName());
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID
        };
        cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);

        while (cursor.moveToNext()) {
            int ind = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int idCol = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            String path = cursor.getString(ind);
            int id = cursor.getInt(idCol);
//            Log.d("vidpath", path);
            if (isFileExits(path)) {
                if (folder.equals(new File(path).getParent())) {
                    Load_Ads++;
                    FileVideo mFile = new FileVideo(path, id);
                    if(Load_Ads%6==0){
                        fileFolder.add(mFile);
                        fileFolder.add(null);
                    }else{
                        fileFolder.add(mFile);
                    }
                }
            }

        }

        Load_Ads=0;
        return fileFolder;
    }

    private boolean isFileExits(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRenameFolder(int position) {
//        showDialog(requireActivity(),videoList.get(position).getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                final File file = new File(mFile.get(position).getPath());
                RenameDialog dialog = new RenameDialog(this, mFile.get(position).getPath());
                dialog.setCallback((key, data) -> {
                    String newName = (String) data;
                    if (key.equals("rename")) {

                        String onlyPath = file.getParent();
                        newName = newName + ".mp4";
//                String renamepath = onlyPath + "/" + newName;
                        File from = new File(mFile.get(position).getPath());
                        File to = new File(onlyPath, newName);
                        try {
                            to.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (from.exists()) {
                            from.renameTo(to);
                            removeMedia(this, from);
                            addMedia(this, to);
                            CountDownTimer Timer2 = new CountDownTimer(1050, 1000) {
                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    mFile.clear();
                                    mFile = getdata();
                                    adapter.update(mFile);

                                }
                            }.start();
                        }
                    }
                });
                dialog.show();
            } else {
                showDialog(this);
            }
        } else {
            final File file = new File(mFile.get(position).getPath());
            RenameDialog dialog = new RenameDialog(context, mFile.get(position).getPath());
            dialog.setCallback((key, data) -> {
                String newName = (String) data;
                if (key.equals("rename")) {

                    String onlyPath = file.getParent();
                    newName = newName + ".mp4";
//                String renamepath = onlyPath + "/" + newName;
                    File from = new File(mFile.get(position).getPath());
                    File to = new File(onlyPath, newName);
                    try {
                        to.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (from.exists()) {
                        from.renameTo(to);
                        removeMedia(this, from);
                        addMedia(this, to);
                        CountDownTimer Timer2 = new CountDownTimer(1050, 1000) {
                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                mFile.clear();
                                mFile = getdata();
                                adapter.update(mFile);

                            }
                        }.start();
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onDeleteFolder(int position) {
        DeleteDialog dialog = new DeleteDialog(this);
//        this.position = position;

        dialog.setCallback((key, data) -> {
            if (key.equals("delete")) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    File file = new File(mFile.get(position).getPath());
                    file.delete();
                    MediaScannerConnection.scanFile(this,
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
                            this.deleteFile(file.getName());
                        }
                        mFile.remove(mFile.get(position));
//                    notifyItemRemoved(videoList.indexOf(videoList.get(position)));
                    } else {
                        mFile.remove(mFile.get(position));
//                    notifyItemRemoved(videoList.indexOf(videoList.get(position)));
                    }
                } else {
                    FileUtils.deleteFileAndroid11(this, mFile.get(position), launcherDelete);
                    File file2 = new File(mFile.get(position).getPath());
                    MediaScannerConnection.scanFile(this,
                            new String[]{file2.toString()},
                            null, null);
                    mFile.remove(mFile.get(position));
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

    @Override
    protected void onResume() {
        super.onResume();
        mFile.clear();
        mFile = getdata();
        adapter.update(mFile);
    }

    public void showDialog(Context activity) {
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
        deny.setOnClickListener(view -> dialog.dismiss());
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
