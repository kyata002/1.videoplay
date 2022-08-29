package com.mtg.videoplay.adapter;

import static com.mtg.videoplay.view.activity.HomeActicity.launcher;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.FileUtils;
import com.mtg.videoplay.Util.Utils;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.view.activity.VideoPlayActivity;
import com.mtg.videoplay.view.dialog.DeleteDialog;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.mtg.videoplay.view.dialog.InfoDialog;
import com.mtg.videoplay.view.dialog.RenameDialog;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.concurrent.TimeUnit;


public class AllVideoAdapter extends RecyclerView.Adapter<AllVideoAdapter.ListViewHolder> {
//    final ActivityResultLauncher<IntentSenderRequest> launcher ;

    public ArrayList<FileVideo> videoList;
    public Context context;
    private PowerMenu powerMenu;
    private OnClickOptionListener onClickOptionListener;

    public void setOnClickOptionListener(OnClickOptionListener onClickOptionListener) {
        this.onClickOptionListener = onClickOptionListener;
    }

    private static final int TYPE_ITEM = 1;
    Intent intent;
    String rotation;
    int ITEM_TYPE = 0;

    private int position;


    public AllVideoAdapter(Context context, ArrayList<FileVideo> videoList) {
        this.videoList = videoList;
        this.context = context;

    }

    private static String getFileSize(FileVideo mFile) {
        File mfile = new File(mFile.getPath());
        long length = mfile.length();
        if (length <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(length) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(length / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String timeFile(FileVideo mFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mFile.getPath());
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release();
        long seconds = Long.valueOf(time);
        String vidLength = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)),
                TimeUnit.MILLISECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));
        return vidLength;
    }

    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_video, parent, false);
        return new ListViewHolder(view);
    }

//    @Override
//    public long getItemId(int position) {
////        return super.getItemId(position);
//        return videoList.get(position).getId();
//    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (videoList.size() == 0) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_defaut);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(videoList.get(position).getPath())
                .into(holder.imgFile);

        holder.txtDuration.setText(timeFile(videoList.get(position)));

        holder.txtSize.setText(getFileSize(videoList.get(position)));

        SimpleDateFormat dateFile = new SimpleDateFormat("dd/MM/yyyy");
        holder.txtTime.setText(dateFile.format(new Date(new File(videoList.get(position).getPath()).lastModified())));

        holder.filename.setText(new File(videoList.get(position).getPath()).getName());


        holder.itemView.setOnClickListener(view -> {
            MediaMetadataRetriever m = new MediaMetadataRetriever();
            m.setDataSource(videoList.get(position).getPath());
            if (Build.VERSION.SDK_INT >= 17) {
                rotation = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            }
            VideoPlayActivity.keyPlay = 0;
            intent = new Intent(context, VideoPlayActivity.class);
            intent.putExtra("file", position);
            intent.putExtra("list", videoList);
            intent.putExtra("rotation", rotation);
            context.startActivity(intent);
        });
        holder.bt_more.setOnClickListener(view -> {
            if (powerMenu != null && powerMenu.isShowing() == true) {

            } else {
                powerMenu = new PowerMenu.Builder(context)
                        //.addItemList(list) // list has "Novel", "Poerty", "Art"
                        .addItem(new PowerMenuItem(context.getString(R.string.share), R.drawable.ic_share_more, false)) // add an item.
                        .addItem(new PowerMenuItem(context.getString(R.string.rename), R.drawable.ic_rename_more, false))
                        .addItem(new PowerMenuItem(context.getString(R.string.delete), R.drawable.ic_delete_more, false)) // add an item.
                        .addItem(new PowerMenuItem(context.getString(R.string.detail), R.drawable.ic_detail_more, false)) // aad an item list.
//                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(12f)
//                    .setTextTypeface(ResourcesCompat.getFont(context, R.font.lexend_regular)!!)
                        .setPadding(48)// sets the corner radius.
                        .setSize(LinearLayout.LayoutParams.WRAP_CONTENT, 630)
                        .setMenuShadow(10f) // sets the shadow.
                        .setIconSize(28)
                        .setTextSize(14)
                        .setIconPadding(2)
                        .setMenuColor(0)
                        .setBackgroundColor(Color.TRANSPARENT)
                        .setOnBackgroundClickListener(view1 -> {
                            powerMenu.dismiss();
                        })
                        //.setTextColor(ContextCompat.getColor(context, Color.parseColor("#3C3C3C")))
                        .setTextGravity(Gravity.LEFT)
                        .setTextTypeface(Typeface.create("font/lexend_regular.ttf", Typeface.NORMAL))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(context, R.color.black))
                        .setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                            @Override
                            public void onItemClick(int posit, PowerMenuItem item) {
                                CharSequence title = item.getTitle();
                                if ("Share".equals(title)) {
                                    share(videoList.get(position).getPath());
                                    powerMenu.dismiss();
                                } else if ("Rename".equals(title)) {
                                    onClickOptionListener.onRename(position);
                                    powerMenu.dismiss();
                                } else if ("Delete".equals(title)) {
                                    dialogDelete(position);
                                    powerMenu.dismiss();
                                } else if ("Info".equals(title)) {
                                    dialogInfo(videoList.get(position).getPath());
                                    powerMenu.dismiss();
                                }
                            }
                        }).build();
                powerMenu.showAsDropDown(view);
            }

        });
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE;
    }


    @Override
    public int getItemCount() {
        Utils.listSize = videoList.size();
        return videoList.size();
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView filename;
        TextView txtDuration;
        TextView txtSize;
        TextView txtTime;
        ImageView imgFile;
        View bt_more;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgFile = itemView.findViewById(R.id.imgFile);
            bt_more = itemView.findViewById(R.id.bt_more);
        }
    }

    public void dialogDelete(int position) {
        DeleteDialog dialog = new DeleteDialog(context);
        this.position = position;

        dialog.setCallback((key, data) -> {
            if (key.equals("delete")) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    File file = new File(videoList.get(position).getPath());
                    file.delete();
                    MediaScannerConnection.scanFile(context,
                            new String[]{file.toString()},
                            null, null);
                    notifyDataSetChanged();
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
                } else {
                    FileUtils.deleteFileAndroid11((AppCompatActivity) context, videoList.get(position), launcher);
                }


            }
            if (key.equals("no")) {

            }
        });
        dialog.show();
    }

    //    public void dialogRename(int position) {
//        final File file = new File(videoList.get(position).getPath());
//        RenameDialog dialog = new RenameDialog(context, videoList.get(position).getPath());
//        dialog.setCallback((key, data) -> {
//
//            if (key.equals("rename")) {
//                String newName = (String) data;
//                String onlyPath = file.getParent();
//                newName = newName + ".mp4";
//                String renamepath = onlyPath + "/" + newName;
//                File from = new File(videoList.get(position).getPath());
//                File to = new File(onlyPath, newName);
//                try {
//                    to.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (from.exists())
//                if (from.renameTo(to)) {
//                    removeMedia(context, from);
//                    addMedia(context, to);
//                    MediaScannerConnection.scanFile(context,
//                            new String[]{from.toString()},
//                            null, null);
//                    MediaScannerConnection.scanFile(context,
//                            new String[]{to.toString()},
//                            null, null);
//                }
//                notifyDataSetChanged();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
//    public ArrayList<FileVideo> getdata() {
//        Cursor csr;
//        ArrayList<FileVideo> videoListPath = new ArrayList<>();
//        String[] proj = new String[]{
//                MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID
//        };
//        csr = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
//        while (csr.moveToNext()) {
//            int ind = csr.getColumnIndex(MediaStore.Video.Media.DATA);
//            int idCol = csr.getColumnIndex(MediaStore.Video.Media._ID);
//            String path = csr.getString(ind);
//            int id = csr.getInt(idCol);
//            Log.d("pathofpath", path);
//            if (new File(path).exists())
//                videoListPath.add(new FileVideo(path, id));
//
//        }
//        return videoListPath;
//    }

    public  void update(ArrayList<FileVideo> mList){
        this.videoList = mList;
        notifyDataSetChanged();
    }
    public void dialogInfo(String path) {
        InfoDialog dialog = new InfoDialog(context, path);
        dialog.show();
    }

    private void share(String Path) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Path));
        context.startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }



    public interface OnClickOptionListener {
        void onRename(int position);
    }
}
