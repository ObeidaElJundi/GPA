package com.coding4fun.models;

/**
 * Created by coding4fun on 21-Oct-16.
 */

public class NoteAudio extends Note {

    private String path;
    private int size, duration;

    public NoteAudio(String path, int size, int duration) {
        this.path = path;
        this.size = size;
        this.duration = duration;
        setType(Note.TYPE_AUDIO);
    }

    public NoteAudio(int id, String title, String date, String path, int size, int duration) {
        super(id, title, date);
        this.path = path;
        this.size = size;
        this.duration = duration;
        setType(Note.TYPE_AUDIO);
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
        d += "\nAudio Size: " + ((getSize() > 1000 * 1000) ? (getSize() / 1000d / 1000) + " MB" : (getSize() / 1000d) + " KB");
        d += "\nAudio Duration: " + getDuration();
        d += "\nAudio Path: " + getPath();
        return d;
    }
}