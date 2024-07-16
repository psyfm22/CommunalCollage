package com.example.communalcollage3;
import android.graphics.Bitmap;


public class CollageCard {
    public int numberOfImages;
    Bitmap[] bitmaps = new Bitmap[4];

    public CollageCard() {
        this.numberOfImages = 0;
    }
}
