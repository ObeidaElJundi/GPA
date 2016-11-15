package com.coding4fun.models;

import java.io.Serializable;

/**
 * Created by coding4fun on 20-Oct-16.
 */

public abstract class Note implements Serializable {

    private int id;
    private String title, date, type;
    public static final String TYPE_VIDEO = "VIDEO";
    public static final String TYPE_AUDIO = "AUDIO";
    public static final String TYPE_PICTURE = "PICTURE";
    public static final String TYPE_TEXT = "TEXT";

    public Note() {
    }

    public Note(int id, String title, String date) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract String getDetails();

}