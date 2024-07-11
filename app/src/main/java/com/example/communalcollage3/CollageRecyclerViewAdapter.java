package com.example.communalcollage3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class CollageRecyclerViewAdapter  extends RecyclerView.Adapter<CollageRecyclerViewAdapter.CollageViewHolder> {


    private List<CollageCard> dataItems;//Stores the data items
    private Context context;
    private LayoutInflater layoutInflater;

    //Initialise the adapter with the context and dataItems
    public CollageRecyclerViewAdapter(Context context, List<CollageCard> dataItems) {
        this.dataItems = dataItems;
        this.context = context;
        this.layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Creates an instance of CollageViewHolder
    @Override
    public CollageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.collage_card_layout, parent, false);
        return new CollageViewHolder(itemView);
    }


    //Binds the data items ot the views inside the Viewholder
    @Override
    public void onBindViewHolder(CollageViewHolder holder, int position) {
        holder.bind(dataItems.get(position));
    }

    //Gets the number of items
    @Override
    public int getItemCount() {
        return dataItems.size();
    }




    class CollageViewHolder extends RecyclerView.ViewHolder {
        ImageView[] imageViews = new ImageView[4];
        int counter = 0;

        CollageViewHolder(View itemView)
        {
            super(itemView);
            imageViews[0] = itemView.findViewById(R.id.imageView1);
            imageViews[1]  = itemView.findViewById(R.id.imageView2);
            imageViews[2]  = itemView.findViewById(R.id.imageView3);
            imageViews[3]  = itemView.findViewById(R.id.imageView4);
        }
        void bind(final CollageCard collage)
        {
            String input = "/sdcard/Pictures/image";
            String input2 = ".jpg";
            for(int i=0 ;i<collage.numberOfImages;i++){
                File imgFile = new File(input + i + input2);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageViews[i].setImageBitmap(bitmap);
                }
                counter++;
            }
        }

    }
}
