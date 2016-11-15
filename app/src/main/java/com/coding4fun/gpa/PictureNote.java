package com.coding4fun.gpa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.coding4fun.models.NotePicture;
import com.coding4fun.others.NotesSQLiteHelper;
import com.coding4fun.others.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by coding4fun on 25-Oct-16.
 */

public class PictureNote extends AppCompatActivity implements View.OnClickListener {

    Toolbar tb;
    EditText title;
    Button fromCamera, fromDevice;
    ImageView image;
    File file;
    Bitmap bitmap;
    boolean fromCameraFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_note);

        initToolbar();
        title = (EditText) findViewById(R.id.note_picture_title);
        fromCamera = (Button) findViewById(R.id.note_picture_camera_button);
        fromDevice = (Button) findViewById(R.id.note_picture_device_button);
        image = (ImageView) findViewById(R.id.note_picture_image);

        fromCamera.setOnClickListener(this);
        fromDevice.setOnClickListener(this);
    }

    private void initToolbar(){
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Picture Note");
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.note_picture_device_button){
            Utils.pickImage(this,this,99);
        } else if(view.getId() == R.id.note_picture_camera_button){
            Utils.cameraCapture(this,88);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 99) {
                Uri uri = data.getData();
                String path = Utils.getAbsolutePathFromUri(this, uri);
                file = new File(path);
                Bitmap b = BitmapFactory.decodeFile(path);
                image.setImageBitmap(b);
                fromCameraFlag = false;
            } else if(requestCode == 88) {
                Log.e("GPA","captured");
                fromCameraFlag = true;
                bitmap = (Bitmap) data.getExtras().get("data");
                image.setImageBitmap(bitmap);
            }
        }
    }

    void saveNote(){
        if(title.getText().toString().equals("")){
            Toast.makeText(this, "Title can't be empty!", Toast.LENGTH_LONG).show();
            return;
        }
        if(bitmap == null && file == null){
            Toast.makeText(this, "You did not pick an image!", Toast.LENGTH_LONG).show();
            return;
        }
        if(fromCameraFlag && bitmap != null){
            Log.e("GPA","saving captured");
            try {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GPA");
                if(!f.exists()) f.mkdirs();
                f = new File(f.getAbsolutePath() + File.separator + title.getText().toString() + ".png");
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG,80,fos);
                fos.flush();
                fos.close();
                NotePicture n = new NotePicture(0, title.getText().toString(), Utils.getCurrentDateANdTime(), f.getAbsolutePath(), f.length());
                saveNewNote(n);
            } catch (IOException e) {
                Toast.makeText(this, "Error!\n"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else if(file != null) { //save note in DB
            Log.e("GPA","saving stored");
            NotePicture n = new NotePicture(0, title.getText().toString(), Utils.getCurrentDateANdTime(), file.getAbsolutePath(), file.length());
            saveNewNote(n);
        }
        finish();
    }

    private void saveNewNote(NotePicture n){
        NotesSQLiteHelper h = new NotesSQLiteHelper(this);
        boolean b = h.addNewNote(n);
        if (b) {
            Toast.makeText(this, "New picture note has been created", Toast.LENGTH_LONG).show();
            Bundle conData = new Bundle();
            conData.putSerializable("noteObject", n);
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
        } else {
            Toast.makeText(this, "Error adding picture note !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        }
    }
}