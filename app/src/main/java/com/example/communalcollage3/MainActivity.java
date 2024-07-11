package com.example.communalcollage3;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;

import java.io.File;

/*
https://developer.android.com/training/permissions/requesting
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_MEDIA = 1;
    private ImageView[] imageViews = new ImageView[5];

    private static final String TAG = "COMP3018";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the ideas for the array
        imageViews[0] = findViewById(R.id.imageView);
        imageViews[1] = findViewById(R.id.imageView2);
        imageViews[2] = findViewById(R.id.imageView3);
        imageViews[3] = findViewById(R.id.imageView4);
        imageViews[4] = findViewById(R.id.imageView5);


        //Can probably make this simplified once I know the SDK of the tablet
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                // If the permissions have already been granted
                loadImageFromSDCard();
            } else {
                // Request the permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_READ_MEDIA);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadImageFromSDCard();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_MEDIA);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Once the user has responded to the permission request, if it is granted then should load the images.
        if (requestCode == REQUEST_CODE_READ_MEDIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImageFromSDCard();
            }
        }
    }




    private void loadImageFromSDCard() {
        // Change this path to the actual path of your image on the SD card
        String input = "/sdcard/Pictures/image";
        String input2 = ".jpg";

        for(int i=1;i<6;i++){
            File imgFile = new File(input + i + input2);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViews[i-1].setImageBitmap(bitmap);
            }
        }
    }
}