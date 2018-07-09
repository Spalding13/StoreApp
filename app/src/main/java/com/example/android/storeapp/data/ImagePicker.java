package com.example.android.storeapp.data;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.storeapp.EditorActivity;
import com.example.android.storeapp.MainActivity;

import java.io.IOException;

import static android.content.Intent.createChooser;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static java.security.AccessController.getContext;

public class ImagePicker {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 0;


    public static void tryToOpenImageSelector(Context context,Activity activity) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector(activity);
    }

    public static void openImageSelector(Activity activity) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        Intent.createChooser(intent,"Select Picture");
        startActivityForResult(activity,intent, PICK_IMAGE_REQUEST,null);
    }


}
