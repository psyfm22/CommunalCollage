package com.example.communalcollage3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

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

/*
https://developer.android.com/training/permissions/requesting
 */

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_MEDIA = 1;
    ArrayList<CollageCard> pictureSlide = new ArrayList<CollageCard>();

    private static final String TAG = "COMP3018";
    RecyclerView recyclerView;
    CollageCard collageCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);//Assign the recycler view

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
        collageCard = new CollageCard();//Create a new collage card

        Log.d(TAG, "Reference has been made");

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d(TAG, "Do we get in  on success listener");

                List<StorageReference> storageReferences = listResult.getItems();
                //We have listed all the images

                if (storageReferences.isEmpty()) {
                    return;
                }
                final int totalImages = storageReferences.size();
                final int[] completedDownloads = {0};


                for (StorageReference fileReference : storageReferences) {
                    try {
                        //For each of the file references we load them into a file and try to get that file
                        File localFile = File.createTempFile("tempImage", getFileExtension(fileReference.getName()));
                        fileReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //On success of that file it means we now can load the bitmap
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                synchronized (collageCard) {
                                    collageCard.bitmaps[collageCard.numberOfImages] = bitmap;
                                    collageCard.numberOfImages++;

                                    if (collageCard.numberOfImages == 4) {
                                        pictureSlide.add(collageCard);
                                        collageCard = new CollageCard();
                                    }
                                }
                                completedDownloads[0]++;
                                checkIfFinished(completedDownloads[0], totalImages);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                completedDownloads[0]++;
                                checkIfFinished(completedDownloads[0], totalImages);
                                Log.d("COMP3018", "failure in adding file");
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("COMP3018", "am i  before the actually  check");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("COMP3018", "failure to list files");
            }
        });

    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    private void checkIfFinished(int downloadCounter, int totalDownloads){
        if (downloadCounter == totalDownloads){
            Log.d(TAG, "All downloads are complete");
            if(collageCard.numberOfImages>0){
                pictureSlide.add(collageCard);
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CollageRecyclerViewAdapter adapter = new CollageRecyclerViewAdapter(this, pictureSlide);
        recyclerView.setAdapter(adapter);
    }
}
