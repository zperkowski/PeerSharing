package com.zperkowski.peersharing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class FilesActivity extends AppCompatActivity {

    private static final String TAG = "FilesActivity";
    private RecyclerView recyclerView;
    private ArrayList<File> filesList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("name"));

//        recyclerView = findViewById(R.id.files_list);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new SimpleCardAdapter(filesList, getApplicationContext()));
    }
}
