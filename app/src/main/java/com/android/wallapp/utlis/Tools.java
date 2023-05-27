package com.android.wallapp.utlis;

import static com.android.wallapp.utlis.ConstantMethods.DOWNLOAD;
import static com.android.wallapp.utlis.ConstantMethods.SET_GIF;
import static com.android.wallapp.utlis.ConstantMethods.SET_WITH;
import static com.android.wallapp.utlis.ConstantMethods.SHARE;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.wallapp.R;

import com.android.wallapp.services.SetGIFAsWallpaperService;
import com.facebook.shimmer.BuildConfig;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("deprecation")
public class Tools {

    Activity activity;
    MenuItem prevMenuItem;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";

    public Tools(Activity activity) {
        this.activity = activity;
    }







    public static String parseHtml(String htmlData) {
        if (htmlData != null && !htmlData.trim().equals("")) {
            return htmlData.replace("", "");
        } else {
            return "";
        }
    }



    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c", count / Math.pow(1000, exp), "KMGTPE".charAt(exp - 1));
    }

    public static long timeStringtoMilis(String time) {
        long milis = 0;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sd.parse(time);
            milis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return milis;
    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static CharSequence getTimeAgo(String dateStr) {
        if (dateStr != null && !dateStr.trim().equals("")) {
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getTimeZone("CET"));
            try {
                long time = sdf.parse(dateStr).getTime();
                long now = System.currentTimeMillis();
                return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getFormatedDate(String dateStr) {
        if (dateStr != null && !dateStr.trim().equals("")) {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            oldFormat.setTimeZone(TimeZone.getTimeZone("CET"));
            SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
            try {
                String newStr = newFormat.format(oldFormat.parse(dateStr));
                return newStr;
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }


    public static String createName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    public static void shareContent(Activity activity, String title) {
        String content = Html.fromHtml(activity.getResources().getString(R.string.share_text)).toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + content + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public static void shareArticle(Activity activity, String postTitle, String postUrl) {
        String content = Html.fromHtml(activity.getResources().getString(R.string.share_text)).toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, postTitle + "\n" + postUrl + "\n\n" + content + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public static void downloadImage(Activity activity, String filename, String downloadUrlOfImage, String mimeType) {
        try {
            DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType(mimeType) // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Snackbar.make(activity.findViewById(android.R.id.content), "Image download started.", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Snackbar.make(activity.findViewById(android.R.id.content), "Image download failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    public Boolean verifyPermissions() {
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity, STORAGE_PERMISSIONS, 1);
            return false;
        }
        return true;
    }

    public static void setAction(Context context, byte[] bytes, String imgName, String action) {
        try {
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getString(R.string.app_name));
            } else {
                dir = new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name));
            }
            boolean success = true;
            if (!dir.exists()) {
                success = dir.mkdirs();
            }
            if (success) {
                File imageFile = new File(dir, imgName);
                FileOutputStream fileWriter = new FileOutputStream(imageFile);
                fileWriter.write(bytes);
                fileWriter.flush();
                fileWriter.close();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = new File(imageFile.getAbsolutePath());
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                switch (action) {
                    case DOWNLOAD:
                        //do nothing
                        break;

                    case SHARE:
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        share.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_text) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imageFile.getAbsolutePath()));
                        context.startActivity(Intent.createChooser(share, "Share Image"));
                        break;

                    case SET_WITH:
                        Intent setWith = new Intent(Intent.ACTION_ATTACH_DATA);
                        setWith.addCategory(Intent.CATEGORY_DEFAULT);
                        setWith.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "image/*");
                        setWith.putExtra("mimeType", "image/*");
                        context.startActivity(Intent.createChooser(setWith, "Set as:"));
                        break;

                    case SET_GIF:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            ConstantMethods.GIF_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getString(R.string.app_name);
                        } else {
                            ConstantMethods.GIF_PATH = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
                        }
                        ConstantMethods.GIF_NAME = file.getName();


                        try {
                            WallpaperManager.getInstance(context).clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent setGif = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        setGif.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, SetGIFAsWallpaperService.class));
                        context.startActivity(setGif);

                        Log.d("GIF_PATH", ConstantMethods.GIF_PATH);
                        Log.d("GIF_NAME", ConstantMethods.GIF_NAME);
                        break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMargins(Context context, View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            final float scale = context.getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l = (int) (left * scale + 0.5f);
            int r = (int) (right * scale + 0.5f);
            int t = (int) (top * scale + 0.5f);
            int b = (int) (bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

}
