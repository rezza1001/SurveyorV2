package com.wadaro.surveyor.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileProcessing {

    public static final int REQUEST_OPEN_CAMERA  = 1001;
    public static final int REQUEST_OPEN_GALLERY = 1002;
    public static final int RESULT_OK = -1;
    private OnSavedListener mOnSavedListener;

    public void processImage(Context mContext, Intent data, String path, String name){
        int request     = data.getIntExtra("REQ",0);
        int result      = data.getIntExtra("RES",0);
        Intent intent   = data.getSelector();
        Log.d("FileProcessing","Start processImage "+request+":"+result);
        if (request == REQUEST_OPEN_CAMERA && result == RESULT_OK){
            Bitmap thumbnail = (Bitmap) intent.getExtras().get("data");
            saveToTmp(mContext,thumbnail, path, name);
        }
        else if (request == REQUEST_OPEN_GALLERY && result == RESULT_OK){
            Uri contentURI = intent.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveToTmp(mContext,bitmap, path, name);
        }
    }
    public  void saveToTmp(Context context, Bitmap bitmap, String path, String name){
        Log.d("FileProcessing",path+name );
        String mediaPath = getMainPath(context).getAbsolutePath()+path+name;
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(getMainPath(context).getAbsolutePath(), "/");

            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create Zonatik Success");
                }
                else {
                    Log.d("FileProcessing","Create Zonatik Failed");
                }
            }

            file = new File(getMainPath(context), path);
            if(!file.exists())
            {
                if(file.mkdirs()){
                    Log.d("FileProcessing","Create "+path+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+path+" Failed");
                }
            }
            f = new File(file.getAbsolutePath()+"/"+name);
        }

        assert file != null;
        Log.d("FileProcessing",file.getAbsolutePath()+"/"+name);

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("PATH", path);
        message.what =1 ;
        message.setData(bundle);
        handler.sendMessageDelayed(message,500);
    }

    public static File getMainPath(Context context){
        File mediaPath = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mediaPath = context.getExternalFilesDir("");
        }
        return mediaPath;
    }

    public  void saveToTmpReal(Context context, Bitmap bitmap, String path, String name){
        Log.d("FileProcessing",path+name );
        String mediaPath = getMainPath(context).getAbsolutePath()+path+name;
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(getMainPath(context), "/");
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create Zonatik Success");
                }
                else {
                    Log.d("FileProcessing","Create Zonatik Failed");
                }
            }

            file = new File(getMainPath(context), path);
            if(!file.exists())
            {
                if(file.mkdirs()){
                    Log.d("FileProcessing","Create "+path+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+path+" Failed");
                }
            }
            f = new File(file.getAbsolutePath()+"/"+name);
        }

        assert file != null;
        Log.d("FileProcessing",file.getAbsolutePath()+"/"+name);

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("PATH", path);
        message.what =1 ;
        message.setData(bundle);
        handler.sendMessageDelayed(message,500);
    }

    public static boolean createFolder(Context context,String path){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(getMainPath(context), "/"+path);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                root = context.getExternalFilesDir("/"+path);
            }
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create root "+path+" Success");
                    return create(context,path);
                }
                else {
                    Log.d("FileProcessing","Create root "+path+" Failed");
                    return false;
                }
            }
            else {
                return create(context, path);
            }
        }
        else {
            Log.d("FileProcessing","MEDIA_MOUNTED NOT ACCESS");
            return false;
        }
    }

    private static boolean create(Context context,String path){
        File file;
        file = new File(getMainPath(context).getAbsolutePath(), path);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            file = context.getExternalFilesDir(path);
        }
        if(!file.exists()) {
            if(file.mkdirs()){
                Log.d("FileProcessing","createFolder "+path+" Success");
                return true;
            }
            else {
                Log.d("FileProcessing","createFolder "+path+" Failed");
                return false;
            }
        }
        else {
            Log.d("FileProcessing","Folder exist "+path+"");
            return true;
        }
    }

    public static Bitmap openImage(Context context, String path, String name){
        File sd = getMainPath(context);
        File image = new File(sd.getAbsolutePath()+path+name);
        Log.d("FileProcessing",sd.getAbsolutePath()+path+name);
        if (!image.exists()){
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }
    public static Bitmap openImage(Context context, String url){
        File sd = getMainPath(context);
        File image = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public int folderFilqQTY(Context context, String url){
        File sd = getMainPath(context);

        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!file.exists()){
            file.mkdirs();
        }

        return file.listFiles().length;
    }

    public File[] getAllfiles(Context context, String url){
        File sd = getMainPath(context);
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing","getAllfiles : " +file.listFiles().length);
        return file.listFiles();
    }

    public static Bitmap openImageWthPath(String url){
        File image = new File(url);
        Log.d("FileProcessing",url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public static Bitmap openImageReal(String url){
        File image = new File(url);
        Log.d("FileProcessing",url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    }
    public static boolean deleteImage(Context context, String path, String name){
        String mediaPath = getMainPath(context).getAbsolutePath()+path+name;
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
            return media.delete();
        }
        else {
            return false;
        }
    }
    public static boolean deleteImage(String mediaPath){
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
            return media.delete();
        }
        else {
            return false;
        }
    }
    public static void clearImage(Context context,String path){
        String mediaPath = getMainPath(context)+path;

        File media = new File(mediaPath);
        String[] children = media.list();
        if (children != null){
            for (int i = 0; i < children.length; i++) {
                new File(media, children[i]).delete();
            }
        }
    }

    public static void clearFolder(Context context,String path){
        File sd = FileProcessing.getMainPath(context);

        File dir = new File(sd.getAbsolutePath()+path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Uri getUriFormFile(Context context,File file){
        return FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
    }


    Handler handler = new Handler(msg -> {
        switch (msg.what){
            case 1:
                Log.d("FileProcessing","RESULT "+ msg.getData().getString("NAME")+""+msg.getData().getString("PATH") );
                if (mOnSavedListener != null){
                    mOnSavedListener.onSave(msg.getData().getString("PATH"),msg.getData().getString("NAME") );
                }
        }
        return false;
    } );


    public void setOnSavedListener(OnSavedListener onSavedListener){
        mOnSavedListener = onSavedListener;
    }
    public interface OnSavedListener{
        void onSave(String path, String name);
    }
}
