package com.zperkowski.peersharing;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /*
    getStringOfFiles returns a string that contains list of files.
    Every file contains information if it's a folder (slash on the end of the name)
    After name of a file is its size
    If the file is a folder then the size is 0
    Magic character separates name and size or size and path or path of n-th file and name of n-th+1 file
    Example:
    "File1.txt~100~/path/1/~File2.txt~200~/path/1/~Folder/~0~/path/1/~"
     */
    public static String getStringOfFiles() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File downloadFolder = new File(path);
        Log.d(TAG, "Path: " + path + " Read: " + downloadFolder.canRead() + " Write: " + downloadFolder.canWrite());
        File[] files = downloadFolder.listFiles();
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                list.append(files[i].getName());
                list.append(NetworkService.MAGIC_CHAR);
                list.append(files[i].length());
            } else {
                list.append(files[i].getName() + "/");
                list.append(NetworkService.MAGIC_CHAR);
                list.append(0);
            }
            list.append(NetworkService.MAGIC_CHAR);
            list.append(files[i].getAbsolutePath());
            list.append(NetworkService.MAGIC_CHAR);
        }
        Log.d(TAG, "getStringOfFiles: " + list);
        return list.toString();
    }

    public static List<Files> getListOfFiles(String stringOfFiles) {
        Log.d(TAG, "getListOfFiles");
        List<Files> files = new ArrayList<>();
        String[] splitString = stringOfFiles.split(NetworkService.MAGIC_CHAR);
        // Very first element is "...LISTOFFILES"
        for (int i = 1; i < splitString.length; i+=3) {
            files.add(new Files(splitString[i], Integer.valueOf(splitString[i+1]), splitString[i+2]));
            Log.d(TAG, files.get(files.size()-1).toString());
        }


        return files;
    }
}
