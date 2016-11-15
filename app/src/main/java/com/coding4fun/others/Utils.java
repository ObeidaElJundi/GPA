package com.coding4fun.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by coding4fun on 06-Nov-16.
 */

public class Utils {

    public static void showAlertWithNoButtons(Context context, String title, String msg){
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }

    public static void pickImage(Context context, Activity activity, int requestCode){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(i,"Pick a pic"),requestCode);
        Toast.makeText(context, "Pick a pic", Toast.LENGTH_SHORT).show();
    }

    public static void pickVideo(Context context, Activity activity, int requestCode){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        activity.startActivityForResult(Intent.createChooser(i,"Pick a video"),requestCode);
        Toast.makeText(context, "Pick a video", Toast.LENGTH_SHORT).show();
    }

    public static void cameraCapture(Activity activity, int requestCode){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        activity.startActivityForResult(cameraIntent, requestCode);
    }

    public static void cameraVideoCapture(Activity activity, int requestCode){
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        activity.startActivityForResult(cameraIntent, requestCode);
    }

    public static String getAbsolutePathFromUri(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static String getCurrentDateANdTime(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String date_time = df.format(Calendar.getInstance().getTime());
        return date_time;
    }

}