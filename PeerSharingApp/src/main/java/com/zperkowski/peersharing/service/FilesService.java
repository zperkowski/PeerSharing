package com.zperkowski.peersharing.service;

import com.zperkowski.peersharing.model.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FilesService {

    public ObservableList<File> getListOfFiles(String path) {
        ObservableList<File> listOfFiles = FXCollections.observableArrayList();
        java.io.File folder = new java.io.File(path);
        for (java.io.File file :
                folder.listFiles()) {
            listOfFiles.add(new File(file));
            if (file.isFile()) {
                System.out.println("File " + file.getName());

            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName());
            }
        }
        return listOfFiles;
    }
}
