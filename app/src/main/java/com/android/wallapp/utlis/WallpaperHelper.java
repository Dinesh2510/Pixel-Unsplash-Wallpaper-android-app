package com.android.wallapp.utlis;

import static com.android.wallapp.utlis.ConstantMethods.BOTH;
import static com.android.wallapp.utlis.ConstantMethods.DELAY_SET;
import static com.android.wallapp.utlis.ConstantMethods.DOWNLOAD;
import static com.android.wallapp.utlis.ConstantMethods.HOME_SCREEN;
import static com.android.wallapp.utlis.ConstantMethods.LOCK_SCREEN;
import static com.android.wallapp.utlis.ConstantMethods.SET_GIF;
import static com.android.wallapp.utlis.ConstantMethods.SET_WITH;
import static com.android.wallapp.utlis.ConstantMethods.SHARE;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.wallapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class WallpaperHelper {

    private static final String TAG = "WallpaperHelper";
    AppCompatActivity activity;

    public WallpaperHelper(AppCompatActivity activity) {
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setWallpaper(View view, ProgressDialog progressDialog, Bitmap bitmap, String setAs) {
        switch (setAs) {
            case HOME_SCREEN:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    onWallpaperApplied(view,progressDialog,  activity.getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                break;

            case LOCK_SCREEN:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                    onWallpaperApplied(view, progressDialog, activity.getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                break;

            case BOTH:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                    wallpaperManager.setBitmap(bitmap);
                    onWallpaperApplied(view, progressDialog, activity.getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                break;
        }
    }

    //Set wallpaper below Android Nougat
    public void setWallpaper(View view, ProgressDialog progressDialog, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.msg_preparing_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .load(imageURL.replace(" ", "%20"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        try {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
                            wallpaperManager.setBitmap(bitmap);
                            onWallpaperApplied(view, progressDialog, activity.getString(R.string.msg_success_applied));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Snackbar.make(view, activity.getString(R.string.snack_bar_error), Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }), DELAY_SET);
    }

    public void onWallpaperApplied(View view, ProgressDialog progressDialog, String message) {
        showSuccessDialog(message,view);
        progressDialog.dismiss();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d(TAG, "show interstitial ad");
        }, DELAY_SET);
    }

    public void showSuccessDialog(String message, View view) {
        Log.e(TAG, "showSuccessDialog: "+message );
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public void setGif(View view, ProgressDialog progressDialog, String imageName, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.msg_preparing_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".gif"), SET_GIF);
                            progressDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Snackbar.make(view, activity.getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);
    }

    public void setWallpaperFromOtherApp(String imageURL) {

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageURL.replace("%20", "_")), SET_WITH);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);

    }

    public void downloadWallpaper(View view,ProgressDialog progressDialog, String imageName, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.snack_bar_saving));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            InputStream is = new BufferedInputStream(new FileInputStream(resource));
                            String mimeType = URLConnection.guessContentTypeFromStream(is);

                            if (mimeType.equals("image/gif")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".gif"), DOWNLOAD);
                            } else if (mimeType.equals("image/png")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".png"), DOWNLOAD);
                            } else {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".jpg"), DOWNLOAD);
                            }

                            onWallpaperApplied(view, progressDialog, activity.getString(R.string.msg_success_saved));
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);

    }

    public void shareWallpaper(ProgressDialog progressDialog, String imageName, String imageURL) {

        progressDialog.setMessage(activity.getString(R.string.msg_preparing_wallpaper));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(() -> Glide.with(activity)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {

                            InputStream is = new BufferedInputStream(new FileInputStream(resource));
                            String mimeType = URLConnection.guessContentTypeFromStream(is);

                            if (mimeType.equals("image/gif")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".gif"), SHARE);
                            } else if (mimeType.equals("image/png")) {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".png"), SHARE);
                            } else {
                                Tools.setAction(activity, Tools.getBytesFromFile(resource), Tools.createName(imageName + ".jpg"), SHARE);
                            }

                            progressDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        return true;
                    }
                })
                .submit(), DELAY_SET);
    }

}