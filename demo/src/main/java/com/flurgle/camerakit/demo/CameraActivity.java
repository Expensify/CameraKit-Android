package com.flurgle.camerakit.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by andrew on 9/6/17.
 */

public class CameraActivity extends AppCompatActivity {

    CameraView camera;
    ImageButton button;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        ImageUtils.isStoragePermissionGranted(activity);
        setContentView(R.layout.camera_view);

        camera = (CameraView) findViewById(R.id.camera);
        button = (ImageButton) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage();
            }
        });

        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                CameraActivity.this.savePhoto(jpeg);
            }
        });
    }

    private void savePhoto(final byte[] jpeg) {
        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                File destinationFile = new File(ImageUtils.getPhotoDirectory(activity),
                        String.valueOf(System.currentTimeMillis()) + ImageUtils.DEFAULT_IMAGE_EXTENSION);
                ImageUtils.writeBytesToFile(jpeg, destinationFile);
                return destinationFile.getAbsolutePath();
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                String pictureFilename = task.getResult();
                Toast.makeText(activity, "Camera picture saved to " + pictureFilename, Toast.LENGTH_SHORT).show();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        camera.stop();
        super.onPause();
    }

}
