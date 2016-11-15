package com.coding4fun.models;

import android.os.Build;

import java.util.concurrent.TimeUnit;

/**
 * Created by coding4fun on 29-Oct-16.
 */

public class NoteVideo extends Note {

    private String path;
    private int size, duration;

    public NoteVideo(String path, int size, int duration) {
        this.path = path;
        this.size = size;
        this.duration = duration;
        setType(Note.TYPE_VIDEO);
    }

    public NoteVideo(int id, String title, String date, String path, int size, int duration) {
        super(id, title, date);
        this.path = path;
        this.size = size;
        this.duration = duration;
        setType(Note.TYPE_VIDEO);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String getDetails() {
        String d = "Note Title: " + getTitle();
        d += "\nNote Type: " + getType();
        d += "\nCreation Date: " + getDate();
        d += "\nVideo Size: " + ((getSize() > 1000*1000) ? (getSize()/1000d/1000)+" MB" : (getSize()/1000d)+" KB");
        d += "\nVideo Duration: " + ((getDuration() > 60) ? (getDuration()/60)+" mins "+(getDuration()%60)+" secs" : getDuration()+" secs");
        d += "\nVideo Path: " + getPath();
        return d;
    }

    //not accurate!
    String getDuration(int d){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        } else return "0";
    }
}