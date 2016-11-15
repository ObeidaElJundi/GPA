package com.coding4fun.others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coding4fun.models.Note;
import com.coding4fun.models.NoteAudio;
import com.coding4fun.models.NotePicture;
import com.coding4fun.models.NoteText;
import com.coding4fun.models.NoteVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coding4fun on 29-Oct-16.
 */

public class NotesSQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes.db";
    public static final String TABLE_NAME = "notes";
    // Table Columns names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_SIZE = "size"; // in bytes
    private static final String COLUMN_DURATION = "duration"; // in seconds
    // Database creation sql statement
    private final String DATABASE_CREATE_TABLE = "create table "
            + TABLE_NAME + "( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, " + COLUMN_TYPE + " text not null, "
            + COLUMN_DATE + " text not null, " + COLUMN_TEXT + " text, " + COLUMN_PATH + " text, "
            + COLUMN_SIZE + " integer, " + COLUMN_DURATION + " integer);";
    private final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public NotesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DATABASE_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }


    /*************** All CRUD(Create, Read, Update, Delete) Operations ***************/

    public List<Note> getAllNotes(){
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        if(c.getCount() == 0) return notes;
        c.moveToFirst();
        do {
            Note note = getNoteByType(c);
            notes.add(note);
        } while(c.moveToNext());
        db.close();
        return notes;
    }

    public boolean addNewNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE,note.getTitle());
        values.put(COLUMN_DATE,note.getDate());
        values.put(COLUMN_TYPE,note.getType());
        if(note.getType().equals(Note.TYPE_TEXT)){
            values.put(COLUMN_TEXT,((NoteText)note).getText());
        } else if(note.getType().equals(Note.TYPE_PICTURE)){
            values.put(COLUMN_PATH,((NotePicture)note).getPath());
            values.put(COLUMN_SIZE,((NotePicture)note).getSize());
        } else if(note.getType().equals(Note.TYPE_AUDIO)){
            values.put(COLUMN_PATH,((NoteAudio)note).getPath());
            values.put(COLUMN_SIZE,((NoteAudio)note).getSize());
            values.put(COLUMN_DURATION,((NoteAudio)note).getDuration());
        } else if(note.getType().equals(Note.TYPE_VIDEO)){
            values.put(COLUMN_PATH,((NoteVideo)note).getPath());
            values.put(COLUMN_SIZE,((NoteVideo)note).getSize());
            values.put(COLUMN_DURATION,((NoteVideo)note).getDuration());
        }
        long id = db.insert(TABLE_NAME,null,values);
        db.close();
        if(id == -1) return false;
        else {
            note.setId((int)id);
            return true;
        }
    }

    public void deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        /*String where = "id = ?";
        String[] whereArgs = { id+"" };
        db.delete()*/
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id=" + id;
        db.execSQL(query);
        db.close();
    }

    private Note getNoteByType(Cursor c){
        //Note note = new Note(0,"null","null");
        Note note = null;
        String type = c.getString(2);
        if(type.equals(Note.TYPE_TEXT)){
            note = new NoteText(c.getInt(0),c.getString(1),c.getString(3),c.getString(4));
        } else if(type.equals(Note.TYPE_AUDIO)){
            note = new NoteAudio(c.getInt(0),c.getString(1),c.getString(3),c.getString(5),c.getInt(6),c.getInt(7));
        } else if(type.equals(Note.TYPE_PICTURE)){
            note = new NotePicture(c.getInt(0),c.getString(1),c.getString(3),c.getString(5),c.getInt(6));
        } else if(type.equals(Note.TYPE_VIDEO)){
            note = new NoteVideo(c.getInt(0),c.getString(1),c.getString(3),c.getString(5),c.getInt(6),c.getInt(7));
        }
        return note;
    }

}