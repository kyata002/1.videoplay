package com.mtg.videoplay.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.Utils;
import com.mtg.videoplay.view.activity.HomeActicity;
import com.mtg.videoplay.view.activity.VideoPlayActivity;
import com.mtg.videoplay.view.fragment.VideoFragment;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class AllVideoAdapter extends RecyclerView.Adapter<AllVideoAdapter.ListViewHolder> {

    public ArrayList<String> videoList;
    public Context context;
    int ITEM_TYPE = 0;


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


//        if (new File(mFilteredList.get(position)).getName().equals("0")) {
//            holder.filename.setText("Internal Storage");
//
//        } else {
        holder.filename.setText(new File(videoList.get(position)).getName());
//        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoPlayActivity.class);
            intent.putExtra("file",position);
            intent.putExtra("list",videoList);
            context.startActivity(intent);
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
        ImageView imgFile;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgFile = itemView.findViewById(R.id.imgFile);
        }
    }
}
