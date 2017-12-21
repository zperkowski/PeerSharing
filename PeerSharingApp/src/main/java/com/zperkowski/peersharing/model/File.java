package com.zperkowski.peersharing.model;

import java.util.Date;

public class File {
    private java.io.File content;
    private String name;
    private int size;
    private Date creationDate;
    private Date modificationDate;

    public File(java.io.File content) {
        this.content = content;
        this.name = content.getName();
    }

    @Override
    public String toString() {
        return super.toString() + "@" + name;
    }
}
