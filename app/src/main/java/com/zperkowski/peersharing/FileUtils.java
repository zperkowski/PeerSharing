package com.zperkowski.peersharing;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static List<File> getFilesList() {
        String path = Environment.getRootDirectory().getPath();
        Log.d(TAG, "Path: " + path);
        File[] files = new File(path).listFiles();
        String list = "[ ";
        for (File f : files) {
            if (f.isFile())
                list += f.getName() + " ";
        }
        list += "]";
        Log.d(TAG, "getFilesList: " + list);
        return Arrays.asList(files);
    }
}
