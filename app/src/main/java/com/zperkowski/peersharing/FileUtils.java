package com.zperkowski.peersharing;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static String getFilesList() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File downloadFolder = new File(path);
        Log.d(TAG, "Path: " + path + " Read: " + downloadFolder.canRead() + " Write: " + downloadFolder.canWrite());
        File[] files = downloadFolder.listFiles();
        StringBuilder list = new StringBuilder();
        list.append("[ ");
        for (File f : files) {
            if (f.isFile())
                list.append(f.getName() + " ");
            else
                list.append(f.getName() + "/ ");
        }
        list.append("]");
        Log.d(TAG, "getFilesList: " + list);
        return list.toString();
    }
}
