package com.example.communalcollage3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CollageRecyclerViewAdapter  extends RecyclerView.Adapter<CollageRecyclerViewAdapter.CollageViewHolder> {


    private final List<CollageCard> dataItems;//Stores the data items
    //private Context context; Not needed at this point
    private final LayoutInflater layoutInflater;

    private static final int VIEW_TYPE_ONE = 0;
    private static final int VIEW_TYPE_TWO = 1;

    //Initialise the adapter with the context and dataItems
    public CollageRecyclerViewAdapter(Context context, List<CollageCard> dataItems) {
        this.dataItems = dataItems;
    //    this.context = context;
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


    //Binds the data items ot the views inside the View holder
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
            //Assign all the imageViews
            imageViews[0] = itemView.findViewById(R.id.imageView1);
            imageViews[1]  = itemView.findViewById(R.id.imageView2);
            imageViews[2]  = itemView.findViewById(R.id.imageView3);
            imageViews[3]  = itemView.findViewById(R.id.imageView4);
        }
        void bind(final CollageCard collage)
        {
            // For each image if there is a bitmap then assign
            for(int i=0; i<collage.numberOfImages;i++){
                if(collage.bitmaps[i] != null){
                    imageViews[i].setImageBitmap(collage.bitmaps[i]);
                }
            }
        }

    }
}
