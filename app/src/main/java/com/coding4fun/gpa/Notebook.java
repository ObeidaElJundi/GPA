package com.coding4fun.gpa;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.coding4fun.adapters.NotesRVadapter;
import com.coding4fun.models.Note;
import com.coding4fun.models.NoteAudio;
import com.coding4fun.models.NotePicture;
import com.coding4fun.models.NoteText;
import com.coding4fun.models.NoteVideo;
import com.coding4fun.others.NotesSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class Notebook extends Fragment implements View.OnClickListener, View.OnTouchListener, GestureDetector.OnGestureListener {

    RecyclerView rv;
    NotesRVadapter adapter;
    List<Note> notesList;
    View addLL, rvLL;
    GestureDetectorCompat gd;
    ImageView pic,vid,txt,aud;
    boolean flingUP=true, shown = true;
    NotesSQLiteHelper sqlHelper;
    Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notebook, container, false);
        //define views here
        rv = (RecyclerView) v.findViewById(R.id.notes_rv);
        addLL = v.findViewById(R.id.addLL);
        //rvLL = v.findViewById(R.id.rvLL);
        pic = (ImageView) v.findViewById(R.id.add_note_picture);
        vid = (ImageView) v.findViewById(R.id.add_note_video);
        aud = (ImageView) v.findViewById(R.id.add_note_audio);
        txt = (ImageView) v.findViewById(R.id.add_note_text);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        setHasOptionsMenu(true);	//assign menu only for this fragment
        gd = new GestureDetectorCompat(getContext(),this);
        sqlHelper = new NotesSQLiteHelper(getContext());
        addLL.setOnTouchListener(this);
        aud.setOnClickListener(this);
        pic.setOnClickListener(this);
        vid.setOnClickListener(this);
        txt.setOnClickListener(this);
        initRV();
        notesList = new ArrayList<>();
        /*notesList.add(new NoteText("TEXT NOTE 1","20/10/2016","blaaaaaaaa blaaaaaaa blaaa"));
        notesList.add(new NotePicture("PICTURE NOTE 1","22/10/2016","NO PATH - TEST",0));
        notesList.add(new NoteAudio("AUDIO NOTE","25/10/2016","",0,0));
        notesList.add(new NoteText("TEXT NOTE 2","25/10/2016","blaaaaaaaa aaaa blaaa"));
        notesList.add(new NoteText("TEXT NOTE 2","25/10/2016","blaaaaaaaa aaaa blaaa"));*/
        adapter = new NotesRVadapter(getContext(), notesList, sqlHelper);
        rv.setAdapter(adapter);
        new GetNotesTask().execute();
    }

    private void initRV(){
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        DefaultItemAnimator anim = new DefaultItemAnimator();
        anim.setAddDuration(500);
        anim.setRemoveDuration(500);
        rv.setItemAnimator(anim);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.notes_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_note:
                if(shown) hide(333);
                else show(333);
                return true;
            default:
                return false;
        }
    }

    void hide(int duration){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && flingUP) {
            float h = addLL.getHeight();
            float y = addLL.getY();
            ObjectAnimator oa = ObjectAnimator.ofFloat(addLL, "y", y + (h-30));
            oa.setDuration(duration);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.start();
            flingUP = false;
            shown = false;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.menu_add));
        }
    }

    void show(int duration){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !flingUP) {
            float h = addLL.getHeight();
            float y = addLL.getY();
            ObjectAnimator oa = ObjectAnimator.ofFloat(addLL, "y", y - (h-30));
            oa.setDuration(duration);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.start();
            flingUP = true;
            shown = true;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.minus));
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if (motionEvent1.getY() > motionEvent.getY()) hide(333);
        else if(motionEvent1.getY() < motionEvent.getY()) show(333);
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gd.onTouchEvent(motionEvent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_note_audio:
                startActivityForResult(new Intent(getActivity(),RecordNote.class),77);
                break;
            case R.id.add_note_picture:
                //getContext().startActivity(new Intent(getActivity(),PictureNote.class));
                startActivityForResult(new Intent(getActivity(),PictureNote.class),88);
                break;
            case R.id.add_note_video:
                startActivityForResult(new Intent(getActivity(),VideoNote.class),66);
                break;
            case R.id.add_note_text:
                //getContext().startActivity(new Intent(getActivity(),TextNote.class));
                startActivityForResult(new Intent(getActivity(),TextNote.class),99);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getContext(), "onActivityResult", Toast.LENGTH_SHORT).show();
        if(resultCode == RESULT_OK){
            //Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
            Note n = null;
            if(requestCode == 99){
                n = (NoteText) data.getSerializableExtra("noteObject");
            } else if(requestCode == 88){
                n = (NotePicture) data.getSerializableExtra("noteObject");
            } else if(requestCode == 77){
                n = (NoteAudio) data.getSerializableExtra("noteObject");
            } else if(requestCode == 66){
                n = (NoteVideo) data.getSerializableExtra("noteObject");
            }
            if(n != null) {
                notesList.add(n);
                if(notesList.size() == 1) adapter.notifyItemChanged(0);
                else {
                    adapter.notifyItemInserted(notesList.size() - 1);
                    rv.scrollToPosition(notesList.size() - 1);
                }
            }
            /*switch (requestCode){
                case 99:
                    n = (NoteText) data.getSerializableExtra("noteObject");
                    notesList.add(n);
                    adapter.notifyItemInserted(notesList.size()-1);
                    rv.scrollToPosition(notesList.size()-1);
                    break;
                default:
                    break;
            }*/
        }
    }

    class GetNotesTask extends AsyncTask<Void,Void,Void> {

        List<Note> notes = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notesList.add(null);
            adapter.notifyItemInserted(0);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notes = sqlHelper.getAllNotes();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notesList.remove(0);
            notesList.addAll(notes);
            adapter.notifyDataSetChanged();
        }
    }

}