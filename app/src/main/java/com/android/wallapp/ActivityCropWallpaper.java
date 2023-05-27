package com.android.wallapp;

import static com.android.wallapp.utlis.ConstantMethods.BOTH;
import static com.android.wallapp.utlis.ConstantMethods.HOME_SCREEN;
import static com.android.wallapp.utlis.ConstantMethods.LOCK_SCREEN;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.wallapp.utlis.ConstantMethods;
import com.android.wallapp.utlis.Tools;
import com.android.wallapp.utlis.WallpaperHelper;

import com.canhub.cropper.CropImageView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class ActivityCropWallpaper extends AppCompatActivity {

    private static final String TAG = "CropWallpaper";
    String image_url;
    Bitmap bitmap = null;
    CropImageView cropImageView;
    private String single_choice_selected;
    CoordinatorLayout parentView;

    ProgressDialog progressDialog;
    WallpaperHelper wallpaperHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_wallpaper);

        progressDialog = new ProgressDialog(this);

        wallpaperHelper = new WallpaperHelper(this);


        Intent intent = getIntent();
        image_url = intent.getStringExtra("image_url");

        cropImageView = findViewById(R.id.cropImageView);
        parentView = findViewById(R.id.coordinatorLayout);

        bitmap = ConstantMethods.bitmap;
        cropImageView.setImageBitmap(bitmap);
        findViewById(R.id.btn_set_wallpaper).setOnClickListener(view -> dialogOptionSetWallpaper());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void dialogOptionSetWallpaper() {
        String[] items = getResources().getStringArray(R.array.dialog_set_crop_wallpaper);
        single_choice_selected = items[0];
        int itemSelected = 0;
        bitmap = cropImageView.getCroppedImage();
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(ActivityCropWallpaper.this);

        alertDialog.setTitle(R.string.dialog_set_title)
                .setSingleChoiceItems(items, itemSelected, (dialogInterface, i) -> single_choice_selected = items[i])
                .setPositiveButton(R.string.dialog_option_ok, (dialogInterface, i) -> {
                    if (single_choice_selected.equals(getResources().getString(R.string.set_home_screen))) {
                        setWallpaper(bitmap, HOME_SCREEN);
                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_lock_screen))) {
                        setWallpaper(bitmap, LOCK_SCREEN);
                    } else if (single_choice_selected.equals(getResources().getString(R.string.set_both))) {
                        setWallpaper(bitmap, BOTH);
                    }
                })
                .setNegativeButton(R.string.dialog_option_cancel, null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setWallpaper(Bitmap bitmap, String setAs) {
        switch (setAs) {
            case HOME_SCREEN:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    wallpaperHelper.onWallpaperApplied(parentView, progressDialog, getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(parentView, getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                }
                break;

            case LOCK_SCREEN:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                    wallpaperHelper.onWallpaperApplied(parentView, progressDialog, getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(parentView, getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                }
                break;

            case BOTH:
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    wallpaperManager.setBitmap(bitmap);
                    wallpaperHelper.onWallpaperApplied(parentView, progressDialog, getString(R.string.msg_success_applied));
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(parentView, getString(R.string.snack_bar_failed), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
