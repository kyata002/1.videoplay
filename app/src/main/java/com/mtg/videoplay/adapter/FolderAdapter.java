package com.mtg.videoplay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtg.videoplay.R;
import com.mtg.videoplay.Util.Utils;
import com.mtg.videoplay.model.Folder;

import java.io.File;
import java.util.ArrayList;


public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ListViewHolder> {
    public ArrayList<Folder> folderList;
    public Context context;

    public FolderAdapter(Context context, ArrayList<Folder> folderList) {
        this.folderList = folderList;
        this.context = context;

    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder,parent,false);
        return  new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        holder.txtfile.setText(folderList.get(position).getFoldersize()+"");
        holder.foldername.setText(new File(folderList.get(position).getFolderpath()).getName());
//        int occurrences = Collections.frequency(allfolderpath, folderPath.get(i));
    }

    @Override
    public int getItemCount() {
        Utils.listSize = folderList.size();
        return folderList.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView foldername;
        TextView txtfile;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            foldername = itemView.findViewById(R.id.foldername);
            txtfile = itemView.findViewById(R.id.txtfile);
        }
    }
}
