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

    private static final int VIEW_TYPE_ONE = 0;
    private static final int VIEW_TYPE_TWO = 1;
    private static final int VIEW_TYPE_THREE = 2;

    //Initialise the adapter with the context and dataItems
    public CollageRecyclerViewAdapter(Context context, List<CollageCard> dataItems) {
        this.dataItems = dataItems;
        this.context = context;
        this.layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getItemViewType(int position) {
        // Alternate between three view types
        return position % 3;
    }

    //Creates an instance of CollageViewHolder
    @Override
    public CollageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_ONE) {
            itemView = layoutInflater.inflate(R.layout.collage_card_layout, parent, false);
        }else if(viewType == VIEW_TYPE_TWO) {
            itemView = layoutInflater.inflate(R.layout.collage_card_layout2, parent, false);
        }else{
            itemView = layoutInflater.inflate(R.layout.collage_card_layout3, parent, false);
        }

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
            for(int i=0; i<collage.numberOfImages;i++){
                if(collage.bitmaps[i] != null){
                    imageViews[i].setImageBitmap(collage.bitmaps[i]);
                }
            }



//            for(int i=0 ;i<collage.numberOfImages;i++){
//                File imgFile = new File(collage.imageUrl[i]);
//                if (imgFile.exists()) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imageViews[i].setImageBitmap(bitmap);
//                }
//                counter++;
//            }
        }

    }
}
