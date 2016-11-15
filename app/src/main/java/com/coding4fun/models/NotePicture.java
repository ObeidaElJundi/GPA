package com.coding4fun.models;

/**
 * Created by coding4fun on 20-Oct-16.
 */

public class NotePicture extends Note {

    private String path;
    private long size;

    public NotePicture(String path, int size) {
        this.path = path;
        this.size = size;
        setType(Note.TYPE_PICTURE);
    }

    public NotePicture(int id, String title, String date, String path, long size) {
        super(id, title, date);
        this.path = path;
        this.size = size;
        setType(Note.TYPE_PICTURE);
    }

    public String getPath() {return path;}

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String getDetails() {
        String d = "Note Title: " + getTitle();
        d += "\nNote Type: " + getType();
        d += "\nCreation Date: " + getDate();
        d += "\nImage Size: " + ((getSize() > 1000*1000) ? (getSize()/1000d/1000)+" MB" : (getSize()/1000d)+" KB");
        d += "\nImage Path: " + getPath();
        return d;
    }
}