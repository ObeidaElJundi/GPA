package com.coding4fun.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coding4fun.gpa.R;
import com.coding4fun.models.Note;
import com.coding4fun.models.NoteAudio;
import com.coding4fun.models.NotePicture;
import com.coding4fun.models.NoteText;
import com.coding4fun.models.NoteVideo;
import com.coding4fun.others.NotesSQLiteHelper;
import com.coding4fun.others.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by coding4fun on 20-Oct-16.
 */

public class NotesRVadapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private List<Note> notes;
    Context context;
    NotesSQLiteHelper sqlHelper;
    private static final int LOADING_VIEW = 9;
    private static final int EMPTY_VIEW = 10;
    private static final int NOTE_VIEW = 11;

    public NotesRVadapter(Context context, List<Note> modelData, NotesSQLiteHelper sqlHelper) {
        this.context = context;
        notes = modelData;
        this.sqlHelper = sqlHelper;
    }

    //describes an item view, and
    //Contains references for all views that are filled by the data of the entry
    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        ImageView noteIcon, more;
        TextView noteTitle, noteDate;
        PopupMenu popup;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteIcon = (ImageView) itemView.findViewById(R.id.note_row_image);
            more = (ImageView) itemView.findViewById(R.id.note_row_more);
            noteTitle = (TextView) itemView.findViewById(R.id.note_row_title);
            noteDate = (TextView) itemView.findViewById(R.id.note_row_date);
            itemView.setOnClickListener(this);
            more.setOnClickListener(this);

            popup = new PopupMenu(context, more);
            popup.getMenuInflater().inflate(R.menu.note_item_menu,popup.getMenu());
            popup.setOnMenuItemClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.note_row_more){
                popup.show();
            } else {
                //int index = getAdapterPosition();
                //Toast.makeText(context, "From itemView: " + index + " " + notes.get(index).getTitle(), Toast.LENGTH_SHORT).show();
                open(notes.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int index = getAdapterPosition();
            switch (item.getItemId()){
                case R.id.note_menu_open:
                    open(notes.get(index));
                    return true;
                case R.id.note_menu_details:
                    new AlertDialog.Builder(context).setTitle("Note Details").setMessage(notes.get(index).getDetails()).show();
                    return true;
                case R.id.note_menu_edit:

                    return true;
                case R.id.note_menu_delete:
                    sqlHelper.deleteNote(notes.get(index).getId());
                    notes.remove(index);
                    notifyItemRemoved(index);
                    return true;
            }
            return false;
        }

        private void open(Note note){
            if(note.getType().equals(Note.TYPE_TEXT)) {
                Utils.showAlertWithNoButtons(context, ((NoteText) note).getTitle(), ((NoteText) note).getText());
                return;
            }
            String mime = null;
            String path = null;
            if(note.getType().equals(Note.TYPE_PICTURE)){
                mime = "image/*";
                path = ((NotePicture)note).getPath();
            } else if(note.getType().equals(Note.TYPE_AUDIO)){
                mime = "audio/*";
                path = ((NoteAudio)note).getPath();
            } else if(note.getType().equals(Note.TYPE_VIDEO)){
                mime = "video/*";
                path = ((NoteVideo)note).getPath();
            }
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), mime);
            context.startActivity(intent);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        android.webkit.WebView wv;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            wv = (android.webkit.WebView) itemView.findViewById(R.id.loading_wv);
        }
    }

    // Return the size of the items list
    @Override
    public int getItemCount() {
        return notes.size()>0 ? notes.size() : 1;	//otherwise, even empty layout won't appear
    }

    @Override
    public int getItemViewType(int position) {
        if (notes.size() == 0)
            return EMPTY_VIEW;
        else if (notes.get(position) == null)
            return LOADING_VIEW;
        else if (notes.get(position) instanceof Note)
            return NOTE_VIEW;
        return super.getItemViewType(position);
    }

    // inflate the item (row) layout and create the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == EMPTY_VIEW) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty, viewGroup, false);
            EmptyViewHolder evh = new EmptyViewHolder(v);
            return evh;
        }
        if (viewType == NOTE_VIEW) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_row, viewGroup, false);
            NoteViewHolder evh = new NoteViewHolder(v);
            return evh;
        }
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading, viewGroup, false);
        LoadingViewHolder vh = new LoadingViewHolder(v);
        return vh;
    }

    //display (update) the data at the specified position
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof NoteViewHolder) {
            NoteViewHolder vh = (NoteViewHolder) viewHolder;
            Note note = notes.get(position);
            if(note instanceof NoteText){ // or: note.getType().equals(Note.TYPE_TEXT)
                vh.noteIcon.setImageResource(R.drawable.note_text);
            } else if(note instanceof NotePicture){
                vh.noteIcon.setImageResource(R.drawable.note_picture);
            } else if(note instanceof NoteAudio){
                vh.noteIcon.setImageResource(R.drawable.note_audio);
            } else if(note instanceof NoteVideo){
                vh.noteIcon.setImageResource(R.drawable.note_video);
            }
            vh.noteTitle.setText(note.getTitle());
            vh.noteDate.setText(note.getDate());
        } else if(viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder vh = (LoadingViewHolder) viewHolder;
            vh.wv.setBackgroundColor(Color.TRANSPARENT);
            vh.wv.loadUrl("file:///android_asset/loading.html");
        }
    }

}