package com.mtg.videoplay.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.Utils;
import com.mtg.videoplay.view.activity.HomeActicity;
import com.mtg.videoplay.view.activity.VideoPlayActivity;
import com.mtg.videoplay.view.dialog.DeleteDialog;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.mtg.videoplay.OnActionCallback;
import com.mtg.videoplay.view.dialog.InfoDialog;
import com.mtg.videoplay.view.dialog.RenameDialog;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.concurrent.TimeUnit;


public class AllVideoAdapter extends RecyclerView.Adapter<AllVideoAdapter.ListViewHolder> {

    public ArrayList<String> videoList;
    public Context context;
    private PowerMenu powerMenu;
    int ITEM_TYPE = 0;
    public OnItemOptionClick onItemOptionClick ;


    public AllVideoAdapter(Context context, ArrayList<String> videoList) {
        this.videoList = videoList;
        this.context = context;

    }

    private static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_video,parent,false);
        return  new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        if(videoList == null) return;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_defaut);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(videoList.get(position))
                .into(holder.imgFile);


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoList.get(position));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release();
        long seconds = Long.valueOf( time );
        String vidLength = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)),
                TimeUnit.MILLISECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));
        holder.txtDuration.setText(vidLength);

        File f = new File(videoList.get(position));
        long length = f.length();
        holder.txtSize.setText(getFileSize(length));


        SimpleDateFormat dateFile = new SimpleDateFormat("dd.MM.yyyy");
        holder.txtTime.setText(dateFile.format(new Date(f.lastModified())));



        holder.filename.setText(new File(videoList.get(position)).getName());
//        }
        holder.itemView.setOnClickListener(view -> {
            VideoPlayActivity.keyPlay=0;
            Intent intent = new Intent(context, VideoPlayActivity.class);
            intent.putExtra("file",position);
            intent.putExtra("list",videoList);
            context.startActivity(intent);
        });
        holder.bt_more.setOnClickListener(view -> {

            if(powerMenu!=null&&powerMenu.isShowing()==true){

            }else {
                powerMenu = new PowerMenu.Builder(context)
                        //.addItemList(list) // list has "Novel", "Poerty", "Art"
                        .addItem(new PowerMenuItem(context.getString(R.string.share), R.drawable.ic_share_more, false)) // add an item.
                        .addItem(new PowerMenuItem(context.getString(R.string.rename), R.drawable.ic_rename_more, false))
                        .addItem(new PowerMenuItem(context.getString(R.string.delete), R.drawable.ic_delete_more, false)) // add an item.
                        .addItem(new PowerMenuItem(context.getString(R.string.detail), R.drawable.ic_detail_more, false)) // aad an item list.
//                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(36f)
//                    .setTextTypeface(ResourcesCompat.getFont(context, R.font.lexend_regular)!!)
                        .setSize(400, 550  )
//                    .setPadding(16)// sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setIconSize(32)
                        .setTextSize(16)
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
                            public void onItemClick(int position, PowerMenuItem item) {
                                CharSequence title = item.getTitle();
                                if ("Share".equals(title)) {
                                    share();
                                    powerMenu.dismiss();
                                } else if ("Rename".equals(title)) {
                                    dialogRename(videoList.get(position));
                                    powerMenu.dismiss();
                                } else if ("Delete".equals(title)) {
                                    dialogDelete(videoList.get(position));
                                    powerMenu.dismiss();
                                } else if ("Detail".equals(title)) {
                                    dialogInfo(videoList.get(position));
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

    public void shareShow() {
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//
//                String charString = charSequence.toString();
//
//                if (charString.isEmpty()) {
//
//                    mFilteredList = videoList;
//                } else {
//
//                    ArrayList<String> filteredList = new ArrayList<>();
//
//                    for (String androidVersion : videoList) {
//
//                        if (androidVersion.toLowerCase().contains(charString) || androidVersion.toLowerCase().contains(charString) || androidVersion.toLowerCase().contains(charString)) {
//
//                            filteredList.add(androidVersion);
//                        }
//                    }
//
//                    mFilteredList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mFilteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                mFilteredList = (ArrayList<String>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }


    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView filename;
        TextView txtDuration;
        TextView txtSize;
        TextView txtTime;
        ImageView imgFile,bt_more;

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

    public void dialogDelete(String path){
        DeleteDialog dialog = new DeleteDialog(context);
        dialog.setCallback((key, data) -> {
            if(key.equals("delete")){
//                if (new File(path).delete()) {
//                    videoList.remove(path);
//                    notifyItemRemoved(videoList.indexOf(path));
//                    dialog.dismiss();
//                }
                File file = new File(path);
                file.delete();
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file.exists()) {
                        context.deleteFile(file.getName());
                    }
                }
            }
            if(key.equals("no")){

            }
        });
//        dialog.setOnDismissListener(this);
        dialog.show();
    }
    public void dialogRename(String path){
        final  File file = new File(path);
        RenameDialog dialog = new RenameDialog(context);
        dialog.setCallback((key, data) -> {

            if(key.equals("rename")){
                String newName = (String) data;
                String onlyPath = file.getParent();
//                String ext = file.getAbsolutePath();
//                ext = ext.substring(ext.lastIndexOf("."));
                newName = newName+".mp4";
                String renamepath = onlyPath + "/" + newName;
                File from = new File(path);
                File to = new File(onlyPath + "/" + newName);


                from.renameTo(to);
                removeMedia(context,from);
                addMedia(context, to);
                if (to.exists()) {
                    Log.d("renamepath", renamepath);
                } else {
                    Log.d("renamepath", "not exist");
                }
                Toast.makeText(context, "Rename to " + newName, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void dialogInfo(String path){
        InfoDialog dialog = new InfoDialog(context, path);
        dialog.show();
    }
    private void share() {
    }
    public interface OnItemOptionClick{
        public void onMore(int pos, View view);
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


    public void Search(){

    }



}
