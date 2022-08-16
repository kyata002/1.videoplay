package com.mtg.videoplay.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.Utils;
import com.mtg.videoplay.view.fragment.VideoFragment;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class AllVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "AllVideoAdapter";
    public ArrayList<String> videoList;
    public ArrayList<String> mFilteredList;
    VideoFragment videoFragment;
    int ITEM_TYPE = 0;


    public AllVideoAdapter( VideoFragment videoFragment, ArrayList<String> videoList) {
        this.videoList = videoList;
        mFilteredList = videoList;
        this.videoFragment=videoFragment;

    }

    private static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == ITEM_TYPE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_file_video, parent, false);
            return new ListViewHolder(itemView);
        } else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return ITEM_TYPE;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListViewHolder) {
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mFilteredList.get(position));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();
            long seconds = Long.parseLong(time);
            String vidLength = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)),
                    TimeUnit.MILLISECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));

            File f = new File(mFilteredList.get(position));
            long length = f.length();
            SimpleDateFormat dateFile = new SimpleDateFormat("dd.MM.yyyy");
                    ((ListViewHolder) holder).txtDuration.setText(vidLength);
            ((ListViewHolder) holder).txtSize.setText(getFileSize(length));
            ((ListViewHolder) holder).txtTime.setText(dateFile.format(new Date(f.lastModified())));

            if (new File(mFilteredList.get(position)).getName().equals("0")) {
                listViewHolder.filename.setText("Internal Storage");
            } else {
                listViewHolder.filename.setText(new File(mFilteredList.get(position)).getName());
            }

        }
    }

    @Override
    public int getItemCount() {
        Utils.listSize = mFilteredList.size();
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = videoList;
                } else {

                    ArrayList<String> filteredList = new ArrayList<>();

                    for (String androidVersion : videoList) {

                        if (androidVersion.toLowerCase().contains(charString) || androidVersion.toLowerCase().contains(charString) || androidVersion.toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView filename;
        TextView txtDuration;
        TextView txtSize;
        TextView txtTime;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtSize=itemView.findViewById(R.id.txtSize);
            txtTime=itemView.findViewById(R.id.txtTime);
        }
    }
}
