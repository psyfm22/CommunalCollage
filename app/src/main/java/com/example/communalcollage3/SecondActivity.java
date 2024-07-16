package com.example.communalcollage3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {


    StorageReference storageReference;

    //Learning how to use firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ImageView image = findViewById(R.id.imageView);
        StorageReference folderRef = FirebaseStorage.getInstance().getReference().child("images");

        folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int imageCount = 0;
                for (StorageReference item : listResult.getItems()) {
                    if (item.getName().endsWith(".jpg") || item.getName().endsWith(".jpeg") || item.getName().endsWith(".png")) {
                        imageCount++;
                    }
                }
                Log.d("COMP3018", "Total images  " + imageCount);
            }
        });


        storageReference = FirebaseStorage.getInstance().getReference("images/image10.jpg");
        try {
            File localFile = File.createTempFile("image10",".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    image.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("COMP3018", "failure");
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}