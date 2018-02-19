package com.zperkowski.peersharing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class FilesCardAdapter extends RecyclerView.Adapter<FilesCardAdapter.CardViewHolder> {
    final static private String TAG = "FilesCardAdapter";

    private static List<File> fileList;
    private Context context;
    private static String deviceIp;

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final static private String TAG = "FilesCA.CardViewHolder";

        Context context;

        CardView cardView;
        TextView fileName;
        TextView fileSize;
        TextView filePath;

        public CardViewHolder(View itemView, Context context) {
            super(itemView);
            Log.d(TAG, "CardViewHolder()");
            itemView.setOnClickListener(this);
            this.context = context;
            cardView = itemView.findViewById(R.id.files_card_view);
            fileName = itemView.findViewById(R.id.row_file_name);
            fileSize = itemView.findViewById(R.id.row_file_size);
            filePath = itemView.findViewById(R.id.row_file_path);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick() " + String.valueOf(fileName.toString()));
            Intent intent = new Intent(itemView.getContext(), NetworkService.class);
            intent.setAction(NetworkService.ACTION_GETFILES);
            intent.putExtra(NetworkService.EXTRA_IP, deviceIp);
            intent.putExtra(NetworkService.EXTRA_PATH, filePath.getText());
            context.startService(intent);
            // TODO: Wait for the respond and reload the CardViewHolder
        }
    }

    FilesCardAdapter(String ip, List<File> filesList, Context context) {
        Log.d(TAG, "FilesCardAdapter(filesList)");
        this.deviceIp = ip;
        this.fileList = filesList;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.file_card_item, parent, false);
        return new CardViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder()");
        holder.fileName.setText(fileList.get(position).getName());
        holder.filePath.setText(fileList.get(position).getPath());
        double fileSize = fileList.get(position).length();
        while (fileSize > 1024.0)
            fileSize /= 1024;
        holder.fileSize.setText(String.valueOf(fileSize));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
