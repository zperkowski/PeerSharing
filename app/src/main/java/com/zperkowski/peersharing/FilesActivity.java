package com.zperkowski.peersharing;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FilesActivity extends AppCompatActivity {

    private static final String TAG = "FilesActivity";
    private RecyclerView recyclerView;
    private static List<Files> filesList = new ArrayList<>();
    private Intent intent;

    public static void setFilesList(List<Files> filesList) {
        Log.d(TAG, "setFilesList");
        FilesActivity.filesList.clear();
        FilesActivity.filesList.addAll(filesList);
    }

    public static List<Files> getFilesList() {
        return filesList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("deviceIP"));

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "recyclerView.getAdapter().notifyDataSetChanged()");
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 300);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "recyclerView.getAdapter().notifyDataSetChanged()");
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 1000);

        recyclerView = findViewById(R.id.files_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FilesCardAdapter(intent.getStringExtra("deviceIP"), filesList, getApplicationContext()));
    }
}
