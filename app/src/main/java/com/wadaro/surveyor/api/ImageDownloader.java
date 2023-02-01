package com.wadaro.surveyor.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wadaro.surveyor.module.FileProcessing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDownloader  {

    private Context mContext;
    private String name, mUrl, path;
    private boolean isClearCachce = false;

    public ImageDownloader(Context context, String url, String path, String name){
        mContext = context;
        this.path = path;
        this.name = name;
        this.mUrl = url;
        Log.d("ImageDownloader","ImageDownloader url: "+url);

    }

    public void clearCache(){
        isClearCachce = true;
    }

    public void execute(){
        if (isClearCachce){
            Glide.with(mContext).
                    load(mUrl).diskCacheStrategy(DiskCacheStrategy.NONE).
                    into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Log.d("ImageDownloader","onResourceReady");
                            Bitmap bitmap = drawableToBitmap(resource);
                            saveToTmp(bitmap,path,name);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d("ImageDownloader","onLoadCleared");
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (mOnDownloadListener != null){
                                mOnDownloadListener.onFinih(path, name);
                            }
                        }
                    });
        }
        else {
            Glide.with(mContext).
                    load(mUrl).
                    into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Log.d("ImageDownloader","onResourceReady");
                            Bitmap bitmap = drawableToBitmap(resource);
                            saveToTmp(bitmap,path,name);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d("ImageDownloader","onLoadCleared");
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (mOnDownloadListener != null){
                                mOnDownloadListener.onFinih(path, name);
                            }
                        }
                    });
        }

    }

    private  void saveToTmp(Bitmap bitmap, String path, String name){
        Log.d("ImageDownloader",path+name );
        String mediaPath = FileProcessing.getMainPath(mContext) +path+name;
        File media = new File(mediaPath);
        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            file = new File(FileProcessing.getMainPath(mContext).getAbsolutePath(), path);
            if(!file.exists()){
                Log.d("ImageDownloader",file.getAbsolutePath()+" is Not exists");
                file.mkdirs();
            }
            f = new File(file.getAbsolutePath()+"/"+name);
        }

        Log.d("ImageDownloader",file.getAbsolutePath()+"/"+name);
        if (mOnDownloadListener != null){
            mOnDownloadListener.onFinih(path, name);
        }
        try {
            FileOutputStream ostream = new FileOutputStream(f);
            if (name.contains(".png")){
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, ostream);
            }
            else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            }
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private OnDownloadListener mOnDownloadListener;
    public void setOnDownloadListener(OnDownloadListener pListener){
        mOnDownloadListener = pListener;
    }

    public interface OnDownloadListener{
        void onStart();
        void onFinih(String path, String id);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        return bitmap;
    }

}
