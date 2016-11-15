package com.coding4fun.gpa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.coding4fun.models.Note;
import com.coding4fun.models.NoteText;
import com.coding4fun.others.NotesSQLiteHelper;
import com.coding4fun.others.Utils;

/**
 * Created by coding4fun on 26-Oct-16.
 */

public class TextNote extends AppCompatActivity {

    Toolbar tb;
    EditText title, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_note);

        initToolbar();
        title = (EditText) findViewById(R.id.note_text_title);
        note = (EditText) findViewById(R.id.note_text_note);
    }

    private void initToolbar(){
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Text Note");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.done:
                saveNote();
                return true;
            default:
                return false;
        }
    }

    void saveNote(){
        if(title.getText().toString().equals("") || note.getText().toString().equals("")){
            Toast.makeText(this, "Title & Note text can't be empty!", Toast.LENGTH_LONG).show();
            return;
        }
        //save note in DB
        Note n = new NoteText(0,title.getText().toString(), Utils.getCurrentDateANdTime(),note.getText().toString());
        NotesSQLiteHelper h = new NotesSQLiteHelper(this);
        boolean b = h.addNewNote(n);
        if(b) {
            Toast.makeText(this, "New text note has been created", Toast.LENGTH_LONG).show();
            Bundle conData = new Bundle();
            conData.putSerializable("noteObject", n);
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
        } else {
            Toast.makeText(this, "Error adding text note !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

}