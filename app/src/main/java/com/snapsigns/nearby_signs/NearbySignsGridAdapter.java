package com.snapsigns.nearby_signs;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import java.util.ArrayList;

/**
 * Adapter is in charge of populating the listview with list item contents, in this case ImageSigns.
 */
public class NearbySignsGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ImageSign> mNearbySigns;
    private int gridWidth;

    public NearbySignsGridAdapter(Context context){
        this.mContext = context;
        SnapSigns appContext = (SnapSigns) context.getApplicationContext();
        this.mNearbySigns = appContext.getNearbySigns();
        if(mNearbySigns == null){
            mNearbySigns = new ArrayList<>();

        }
        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Used to display two ImageSigns per row
        this.gridWidth = size.x/2;
    }

    @Override
    public int getCount() {
        return mNearbySigns.size();
    }

    @Override
    public Object getItem(int position) {
        return mNearbySigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //In charge of populating each grid item with a view.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View gridItem = convertView;

        if(gridItem == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridItem = inflater.inflate(R.layout.my_signs_grid_item, parent, false);

            viewHolder = new ViewHolder(gridItem);
            gridItem.setTag(viewHolder);
            if(mNearbySigns.isEmpty()) return gridItem;
        }
        else{
            viewHolder = (ViewHolder) gridItem.getTag();
        }

        Glide.with(mContext).load(mNearbySigns.get(position).imgURL).
                placeholder(R.xml.progress_animation).into(viewHolder.gridImage);

        return gridItem;
    }

    private class ViewHolder{
        ImageView gridImage;

        ViewHolder(View gridItem){
            gridImage = (ImageView) gridItem.findViewById(R.id.grid_image);
            gridImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            gridImage.getLayoutParams().width = gridWidth;
            gridImage.getLayoutParams().height = gridWidth;

        }

    }

}
