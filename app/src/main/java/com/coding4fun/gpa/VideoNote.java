package com.coding4fun.gpa;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.coding4fun.models.NoteVideo;
import com.coding4fun.others.NotesSQLiteHelper;
import com.coding4fun.others.Utils;

import java.io.File;

/**
 * Created by coding4fun on 26-Oct-16.
 */

public class VideoNote extends AppCompatActivity implements View.OnClickListener {

    Toolbar tb;
    EditText title;
    ImageView image;
    Button fromCamera, fromDevice;
    VideoView videoView;
    LinearLayout videoViewLL;
    File file;
    boolean fromCameraFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_note);

        initToolbar();
        title = (EditText) findViewById(R.id.note_video_title);
        image = (ImageView) findViewById(R.id.imageView);
        fromCamera = (Button) findViewById(R.id.note_video_camera_button);
        fromDevice = (Button) findViewById(R.id.note_video_device_button);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoViewLL = (LinearLayout) findViewById(R.id.videoViewLL);

        fromCamera.setOnClickListener(this);
        fromDevice.setOnClickListener(this);
    }

    private void initToolbar(){
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Video Note");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.note_video_device_button){
            Utils.pickVideo(this,this,99);
        } else if(view.getId() == R.id.note_video_camera_button){
            //Utils.cameraVideoCapture(this,88);
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"GPA"+File.separator+"temp.mp4");
            Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
            startActivityForResult(i,88);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        file = null;
        if(resultCode == RESULT_OK){
            if(requestCode == 99) {
                Uri uri = data.getData();
                String path = Utils.getAbsolutePathFromUri(this, uri);
                file = new File(path);
                videoView.setVideoURI(uri);
                showVideoView();
                videoView.requestFocus();
                fromCameraFlag = false;
            } else if(requestCode == 88) {
                Log.e("GPA","video captured");
                fromCameraFlag = true;
                //Uri videoUri = data.getData();
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"GPA"+File.separator+"temp.mp4");
                videoView.setVideoURI(Uri.fromFile(file));
                showVideoView();
                videoView.requestFocus();
            }
        }
    }

    void showVideoView(){
        image.setVisibility(View.GONE);
        videoViewLL.setVisibility(View.VISIBLE);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);
        //mediaController.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*public void play(View v){
        if(videoView.isPlaying()){
            videoView.pause();
            ((Button)findViewById(R.id.play)).setText("PLAY");
        } else {
            videoView.start();
            ((Button)findViewById(R.id.play)).setText("STOP");
        }
    }*/

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

    void renameVideoFile(){
        File to = new File(file.getParent(),"V" + (System.currentTimeMillis()/1000) + ".mp4");
        file.renameTo(to);
        file = to;
    }

    void saveNote(){
        if(title.getText().toString().equals("")){
            Toast.makeText(this, "Title can't be empty!", Toast.LENGTH_LONG).show();
            return;
        }
        if(file == null){
            Toast.makeText(this, "You did not pick a video!", Toast.LENGTH_LONG).show();
            return;
        }
        if(fromCameraFlag) renameVideoFile();
        try {
            /*MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(file.getAbsolutePath());
            //int d = (int)TimeUnit.MILLISECONDS.toSeconds(mp.getDuration());
            int d = mp.getDuration();
            mp.release();*/
            Log.e("GPA","path = "+file.getAbsolutePath());
            int d = 0;
            if (Build.VERSION.SDK_INT >= 10) {
                MediaMetadataRetriever mr = new MediaMetadataRetriever();
                mr.setDataSource(file.getAbsolutePath());
                String time = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                d = Integer.parseInt(time) / 1000;
            }
            NoteVideo n = new NoteVideo(0, title.getText().toString(), Utils.getCurrentDateANdTime(), file.getAbsolutePath(), (int) file.length(), d);
            saveNewNote(n);
        } catch (Exception e){
            Toast.makeText(this, "Error!\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNewNote(NoteVideo n){
        NotesSQLiteHelper h = new NotesSQLiteHelper(this);
        boolean b = h.addNewNote(n);
        if (b) {
            Toast.makeText(this, "New video note has been created", Toast.LENGTH_LONG).show();
            Bundle conData = new Bundle();
            conData.putSerializable("noteObject", n);
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
        } else {
            Toast.makeText(this, "Error adding video note !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

}