package com.coding4fun.gpa;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coding4fun.models.NoteAudio;
import com.coding4fun.others.NotesSQLiteHelper;
import com.coding4fun.others.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by coding4fun on 24-Oct-16.
 */

public class RecordNote extends AppCompatActivity {

    Toolbar tb;
    EditText title;
    Button record;
    TextView time;
    boolean timerON,recorded;
    String parentPath, path;
    File parentDir, audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_note);

        parentPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"GPA";
        parentDir = new File(parentPath);
        if(!parentDir.exists()) parentDir.mkdirs();
        path = parentPath + File.separator + "temp.m4a";

        initToolbar();
        title = (EditText) findViewById(R.id.note_record_title);
        record = (Button) findViewById(R.id.note_record_button);
        time = (TextView) findViewById(R.id.timeTV);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!timerON) {
                    new Timer().execute();
                    record.setText("STOP");
                }
                else timerON = !timerON;
            }
        });
    }

    private void initToolbar(){
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Audio Note");
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            /*case R.id.done:
                saveNote();
                return true;*/
            default:
                return false;
        }
    }

    void renameAudioFileThenSave(int time){
        //File from = new File(parentPath,"temp.m4a");
        File to = new File(parentPath,"A" + (System.currentTimeMillis()/1000) + ".m4a");
        //if(from.exists()) from.renameTo(to);
        audioFile.renameTo(to);
        saveNote(to,time);
        finish();
        //File a = new File(path);
        //if(a.exists()) a.renameTo(new File(parentPath + File.separator + title.getText().toString()));
    }

    void saveNote(File f, int time){
        NoteAudio n = new NoteAudio(0, title.getText().toString(), Utils.getCurrentDateANdTime(), f.getAbsolutePath(), (int)f.length(), time);
        NotesSQLiteHelper h = new NotesSQLiteHelper(this);
        boolean b = h.addNewNote(n);
        if (b) {
            Toast.makeText(this, "New audio note has been created", Toast.LENGTH_LONG).show();
            Bundle conData = new Bundle();
            conData.putSerializable("noteObject", n);
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
        } else {
            Toast.makeText(this, "Error adding audio note !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }




    class Timer extends AsyncTask<Void,Void,Void>{

        int counter;
        MediaRecorder mr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            counter = 0;
            record.setBackgroundResource(R.drawable.stop_bg);
            timerON = true;
            recorded = false;
            time.setVisibility(View.VISIBLE);
            try {
                audioFile = File.createTempFile("A"+(System.currentTimeMillis()/1000),".m4a",parentDir);
            } catch (IOException e) {}
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startRecording();
            while (timerON){
                publishProgress();
                for(int i=0;i<5 && timerON;i++){
                    try {Thread.sleep(200);}
                    catch (InterruptedException e) {}
                }
                counter++;
            }
            stopRecording();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            String mins = String.valueOf(counter/60);
            if(mins.length() == 1) mins = "0" + mins;
            String secs = String.valueOf(counter%60);
            if(secs.length() == 1) secs = "0" + secs;
            time.setText(mins + " : " + secs);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            record.setBackgroundResource(R.drawable.start_bg);
            time.setVisibility(View.INVISIBLE);
            //stopRecording();
            recorded = true;
            //renameAudioFileThenSave(counter);
            saveNote(audioFile,counter);
        }

        void startRecording(){
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mr.setOutputFile(audioFile.getAbsolutePath());
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            try {
                mr.prepare();
            } catch (IOException e) {}
            mr.start();
        }

        void stopRecording(){
            if (mr != null){
                mr.stop();
                mr.release();
                mr = null;
            }
        }
    }

}