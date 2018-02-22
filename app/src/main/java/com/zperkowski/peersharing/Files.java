package com.zperkowski.peersharing;

public class Files {
    private String name;
    private long size;
    private String path;
    private boolean file;

    public Files(String name, long size, String path) {
        this.name = name;
        this.size = size;
        this.path = path;
        if (name.substring(name.length()-1).equals("/"))
            this.file = false;
        else
            this.file = true;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public boolean isFile() {
        return file;
    }

    @Override
    public String toString() {
        return getName() + " " + getSize() + " " + getPath();
    }
}
