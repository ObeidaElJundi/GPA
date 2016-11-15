package com.coding4fun.models;

/**
 * Created by coding4fun on 20-Oct-16.
 */

public class NoteText extends Note {

    private String text;

    public NoteText(String text) {
        this.text = text;
        setType(Note.TYPE_TEXT);
    }

    public NoteText(int id, String title, String date, String text) {
        super(id, title, date);
        this.text = text;
        setType(Note.TYPE_TEXT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getDetails() {
        String d = "Note Title: " + getTitle();
        d += "\nNote Type: " + getType();
        d += "\nCreation Date: " + getDate();
        return d;
    }
}