package com.mtg.videoplay.view.activity;

import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.adapter.AllVideoAdapter;
import com.mtg.videoplay.base.BaseActivity;
import com.mtg.videoplay.model.FileVideo;

import java.io.File;
import java.util.ArrayList;

public class VideoListActivity extends BaseActivity {
    Cursor cursor;
    ImageView bt_back;
    TextView txtNameFolder;
    private AllVideoAdapter adapter;
    ArrayList<FileVideo> mFile;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_videolist;
    }



    @Override
    protected void initView() {
        bt_back = findViewById(R.id.bt_back);
        txtNameFolder = findViewById(R.id.folderName);
        mFile=getdata();
        RecyclerView rv_videol = findViewById(R.id.rv_videol);
        rv_videol.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
//        videoList.addAll(homeActicity.getdata());
        adapter = new AllVideoAdapter(this,mFile);
        rv_videol.setLayoutManager(linearLayoutManager);
        rv_videol.setAdapter(adapter);

    }

    @Override
    protected void addEvent() {
        bt_back.setOnClickListener(view -> {
            onBackPressed();
        });

    }
    public ArrayList<FileVideo> getdata() {
        ArrayList<FileVideo> fileFolder = new ArrayList<>();
        int id =0;
        String folder = getIntent().getStringExtra("folder");
        txtNameFolder.setText(new File(folder).getName());
        String[] proj = new String[]{
                MediaStore.Video.Media.DATA
        };
        cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);

        while (cursor.moveToNext()) {
            int ind = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            String path = cursor.getString(ind);
//            Log.d("vidpath", path);
            if (isFileExits(path)) {
                if (folder.equals(new File(path).getParent())) {

                    FileVideo mFile = new FileVideo(path, id);
                    fileFolder.add(mFile);
                    id++;
                }
            }

        }
//        Log.v("pathmain", String.valueOf(vidname));
//        layoutManager = new LinearLayoutManager(this);
//        rviewVideoList = (RecyclerView) findViewById(R.id.recycle);
//        rviewVideoList.setLayoutManager(layoutManager);
//        Collections.sort(fileFolder);

        return fileFolder;
    }
    private boolean isFileExits(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
