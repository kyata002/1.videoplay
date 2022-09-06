package com.mtg.videoplay.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mtg.videoplay.R;
import com.mtg.videoplay.base.BaseDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class InfoDialog extends BaseDialog {
    TextView location,filename,length,size,date,resolution,title;
    private final String pathInfo;
    public InfoDialog(@NonNull Context context, String pathInfo) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_info);
        this.pathInfo = pathInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = findViewById(R.id.txttitle);
        filename=findViewById(R.id.filename);
        location=findViewById(R.id.location);
        length = findViewById(R.id.length);
        size = findViewById(R.id.size);
        date = findViewById(R.id.date);
        resolution = findViewById(R.id.resolution);

        title.setText("Info");
        filename.setText(new File(pathInfo).getName());

        location.setText(pathInfo);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(pathInfo);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release();
        long seconds = Long.parseLong( time );
        String vidLength = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)),
                TimeUnit.MILLISECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));
        length.setText(vidLength);

        size.setText(getFileSize(new File(pathInfo).length()));

        SimpleDateFormat dateFile = new SimpleDateFormat("dd.MM.yyyy");
        date.setText(dateFile.format(new Date(new File(pathInfo).lastModified())));

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(pathInfo);
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        resolution.setText(height+"x"+width);

        findViewById(R.id.bt_ok).setOnClickListener(v -> dismiss());

//
    }

    private static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
