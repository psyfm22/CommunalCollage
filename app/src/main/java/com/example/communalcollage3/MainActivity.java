package com.example.communalcollage3;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
https://developer.android.com/training/permissions/requesting
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_MEDIA = 1;
    private ImageView[] imageViews = new ImageView[5];
    ArrayList<CollageCard> pictureSlide = new ArrayList<CollageCard>();;

    private static final String TAG = "COMP3018";
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the ideas for the array
//        imageViews[0] = findViewById(R.id.imageView);
//        imageViews[1] = findViewById(R.id.imageView2);
//        imageViews[2] = findViewById(R.id.imageView3);
//        imageViews[3] = findViewById(R.id.imageView4);
//        imageViews[4] = findViewById(R.id.imageView5);

        recyclerView = findViewById(R.id.recyclerView);

        //Can probably make this simplified once I know the SDK of the tablet
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                // If the permissions have already been granted
                loadImagesIntoRecyclerView();
            } else {
                // Request the permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_READ_MEDIA);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadImagesIntoRecyclerView();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_MEDIA);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CollageRecyclerViewAdapter adapter = new CollageRecyclerViewAdapter(this,catList);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Once the user has responded to the permission request, if it is granted then should load the images.
        if (requestCode == REQUEST_CODE_READ_MEDIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImagesIntoRecyclerView();
            }
        }
    }


    private void loadImagesIntoRecyclerView() {

        int numberOfFiles = findNumberOfImages();

        for(int i=0;i<numberOfFiles;i+=4){
            CollageCard collageCard = new CollageCard();
            int holderNumber = numberOfFiles - i;

            if(holderNumber >=4){
                collageCard.numberOfImages=4;
                collageCard.imageUrl[0] = "";
                collageCard.imageUrl[1] = "";
                collageCard.imageUrl[2] = "";
                collageCard.imageUrl[3] = "";

            }else{

            }



            catcatCard.resourceId = getResources().getIdentifier("@drawable/cat"+i,"drawable", getPackageName());
            catcatCard.catName = catNames[i];
            pictureSlide.add(collageCard);
        }
    }

    private int findNumberOfImages(){
        int numberOfFiles = 0;
        File testFile = new File( Environment.getExternalStorageDirectory() + "/Pictures/");
        File[] files = testFile.listFiles();
        if (files != null) {
            Pattern pattern = Pattern.compile("image(\\d+)\\.jpg");//Regex pattern
            for (File file : files) {
                Matcher matcher = pattern.matcher(file.getName());
                if(matcher.matches()){
                    numberOfFiles++;
                }
            }
        }

        Log.d(TAG, "The number of files=" +numberOfFiles);
        return numberOfFiles;
    }
}