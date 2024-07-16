package com.example.communalcollage3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
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
import android.view.View;
import android.widget.ImageView;

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

    AnimationDrawable animationDrawable;
    private static final int REQUEST_CODE_READ_MEDIA = 1;
    ArrayList<CollageCard> pictureSlide = new ArrayList<CollageCard>();
    private static final String TAG = "COMP3018";
    RecyclerView recyclerView;
    CollageCard collageCard;
    ImageView loadingImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);//Assign the recycler view
        loadingImageView = findViewById(R.id.loadingImageView);
        animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();
        animationDrawable.start();

        //Can probably make this simplified once I know the SDK of the tablet, Or may not even need this code now
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

        Log.d(TAG, "Reference has been made, Going to list all elements");

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d(TAG, "Do we get in  on success listener");
                List<StorageReference> storageReferences = listResult.getItems();
                //We have listed all the images

                if (storageReferences.isEmpty()) {
                    //If there are no images uploaded then you simply return
                    return;
                }

                final int totalImages = storageReferences.size();// total number of images needed to download
                final int[] completedDownloads = {0};//If all the downloads are completed


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
                                    collageCard.bitmaps[collageCard.numberOfImages] = bitmap;//Set the collage card to have the bitmap
                                    collageCard.numberOfImages++; // number of images is increased

                                    if (collageCard.numberOfImages == 4) {
                                        pictureSlide.add(collageCard);// add the collage card to the list
                                        collageCard = new CollageCard();//need to do a new collage card
                                    }
                                }
                                completedDownloads[0]++;
                                checkIfFinished(completedDownloads[0], totalImages);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //If failure of getting image then should move onto the next one
                                completedDownloads[0]++;
                                checkIfFinished(completedDownloads[0], totalImages);
                                Log.d("COMP3018", "failure in adding file");
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("COMP3018", "failure to list files");
                //Failure to list all files
            }
        });

    }

    private String getFileExtension(String fileName) {
        //returns everything after . so gets the extension
        return fileName.substring(fileName.lastIndexOf('.'));
    }


    private void checkIfFinished(int downloadCounter, int totalDownloads){
        if (downloadCounter == totalDownloads){
            Log.d(TAG, "All downloads are complete");
            //Once all downloads are completed we load it and then set the recycler view
            if(collageCard.numberOfImages>0){
                pictureSlide.add(collageCard);
            }


            loadingImageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));// Set up the recyclerView
            CollageRecyclerViewAdapter adapter = new CollageRecyclerViewAdapter(this, pictureSlide); //initialise the adapter being used
            recyclerView.setAdapter(adapter);//Set the recycler view with the adapter
            animationDrawable.stop();

            loadingImageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }

    }
}
