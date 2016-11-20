package com.snapsigns.my_signs;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter is in charge of populating the listview with list item contents, in this case ImageSigns.
 */
public class MySignsAdapter extends BaseAdapter {
    MainActivity mActivity;
    ArrayList<ImageSign> myImageSigns;
    int width;

    public MySignsAdapter(MainActivity activity, ArrayList<ImageSign> myImageSigns){
        this.mActivity = activity;
        this.myImageSigns = myImageSigns;

        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Used to display two ImageSigns per row
        width = size.x/2;

    }

    @Override
    public int getCount() {
        return myImageSigns.size();
    }

    @Override
    public Object getItem(int position) {
        return myImageSigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //In charge of populating each list item with a view.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View gridItem = convertView;

        if(gridItem == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridItem = inflater.inflate(R.layout.my_signs_grid_item, parent, false);

            viewHolder = new ViewHolder(gridItem);
            gridItem.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) gridItem.getTag();
        }

        Picasso.with(mActivity).load(myImageSigns.get(position).getImgURL()).resize(width,width).into(viewHolder.gridImage);

        return gridItem;
    }

    private class ViewHolder{
        ImageView gridImage;

        ViewHolder(View gridItem){
            this.gridImage = (ImageView) gridItem.findViewById(R.id.grid_image);
            this.gridImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

    }
}
