package com.flurgle.camerakit.demo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by andrew on 9/6/17.
 */

public class ImageUtils {

    public static final String DEFAULT_IMAGE_EXTENSION = ".jpg";

    static public void writeBytesToFile(byte[] contents, File destinationFile) {
        OutputStream fos;
        try {
            fos = new FileOutputStream(destinationFile);
            fos.write(contents);
            fos.close();
        } catch (Exception e) {
            Log.e("CameraDemo", "Failed to write to '" + destinationFile.getAbsolutePath() + "': " + e.getMessage());
        }
    }

    public static File getPhotoDirectory(Activity activity) {
        File photoDirectory = isExternalStorageAvailable()
                ? new File(Environment.getExternalStorageDirectory(), "CameraDemo")
                : new File(activity.getFilesDir().getAbsolutePath(), "CameraDemo");
        if (!photoDirectory.exists()) {
            photoDirectory.mkdir();
        }
        return photoDirectory;
    }

    public static boolean isExternalStorageAvailable() {
        // Make sure the media storage is mounted (MEDIA_MOUNTED implies writable)
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.w("CameraDemo", "External storage requested but not available");
            return false;
        }
        return true;
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i("CameraDemo", "Permission is granted");
                return true;
            } else {
                Log.i("CameraDemo", "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            Log.i("CameraDemo", "Permission is granted");
            return true;
        }
    }
}
