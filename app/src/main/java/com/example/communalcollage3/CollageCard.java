package com.example.communalcollage3;
import android.graphics.Bitmap;


public class CollageCard {
    public int numberOfImages;//Number of images for this card
    Bitmap[] bitmaps = new Bitmap[4];//Array of 4 bitmaps


    public CollageCard() {
        this.numberOfImages = 0;
        // Should initially be zero
    }
}
