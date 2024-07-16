package com.example.communalcollage3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
https://developer.android.com/training/permissions/requesting
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_MEDIA = 1;
    ArrayList<CollageCard> pictureSlide = new ArrayList<CollageCard>();
    ;
    ActivityResultLauncher<Intent> resultLauncher;
    private static final String TAG = "COMP3018";
    RecyclerView recyclerView;
    CollageCard collageCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        CollageRecyclerViewAdapter adapter = new CollageRecyclerViewAdapter(this, pictureSlide);
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

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
        collageCard = new CollageCard();
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> storageReferences = listResult.getItems();
                Log.d("COMP3018", "Hello I am here");
                Log.d("COMP3018", "the number of images" + collageCard.numberOfImages);

                for (StorageReference fileReference : storageReferences) {
                    try {
                        File localFile = File.createTempFile("tempImage", getFileExtension(fileReference.getName()));
                        fileReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                Log.d("COMP3018", "The index number "+ collageCard.numberOfImages);
                                collageCard.bitmaps[collageCard.numberOfImages] = bitmap;
                                collageCard.numberOfImages++;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("COMP3018", "failure in adding file");
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("COMP3018", "am i  before the actualoly  cecker");

                    if (collageCard.numberOfImages == 3) {
                        Log.d("COMP3018", "Do I ever actually get in there");
                        pictureSlide.add(collageCard);
                        collageCard = new CollageCard();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("COMP3018", "failure to list files");
            }
        });
        if(collageCard.numberOfImages!=0){
            pictureSlide.add(collageCard);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

}

//        int numberOfFiles = findNumberOfImages();
//
//        //1,2,3,4,5
//        for(int i=0;i<(numberOfFiles);i+=4){
//            CollageCard collageCard = new CollageCard();
//            int holderNumber = numberOfFiles - i;
//
//            if(holderNumber >=4){
//                Log.d(TAG,"In greater or equal than 4");
//                collageCard.numberOfImages=4;
//                collageCard.imageUrl[0] = "/sdcard/Pictures/image"+i+".jpg";
//                collageCard.imageUrl[1] = "/sdcard/Pictures/image"+(i+1)+".jpg";
//                collageCard.imageUrl[2] = "/sdcard/Pictures/image"+(i+2)+".jpg";
//                collageCard.imageUrl[3] = "/sdcard/Pictures/image"+(i+3)+".jpg";
//
//            }else{
//                Log.d(TAG,"In less than 4");
//                collageCard.numberOfImages = holderNumber;
//                for(int j=0; j<holderNumber;j++){
//                    collageCard.imageUrl[j] =  "/sdcard/Pictures/image"+(i+j)+".jpg";
//                }
//                for (int j = holderNumber; j < 4; j++) {
//                    collageCard.imageUrl[j] = null;
//                }
//
//            }
//            pictureSlide.add(collageCard);
//        }

//
//    private int findNumberOfImages(){
//        int numberOfFiles = 0;
//        File testFile = new File( Environment.getExternalStorageDirectory() + "/Pictures/");
//        File[] files = testFile.listFiles();
//        if (files != null) {
//            Pattern pattern = Pattern.compile("image(\\d+)\\.jpg");//Regex pattern
//            for (File file : files) {
//                Matcher matcher = pattern.matcher(file.getName());
//                if(matcher.matches()){
//                    numberOfFiles++;
//                }
//            }
//        }
//
//        Log.d(TAG, "The number of files=" +numberOfFiles);
//        return numberOfFiles;
//    }


