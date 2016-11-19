package com.snapsigns.my_signs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter is in charge of populating the listview with list item contents, in this case ImageSigns.
 */
public class MySignsAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<ImageSign> myImageSigns;

    public MySignsAdapter(Context mContext, ArrayList<ImageSign> myImageSigns){
        this.mContext = mContext;
        this.myImageSigns = myImageSigns;
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridItem = inflater.inflate(R.layout.my_signs_grid_item, parent, false);

            viewHolder = new ViewHolder(gridItem);
            gridItem.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) gridItem.getTag();
        }

        Picasso.with(mContext).load(myImageSigns.get(position).getImgURL()).into(viewHolder.gridImage);

        return gridItem;
    }

    private class ViewHolder{
        ImageView gridImage;

        ViewHolder(View gridItem){
            this.gridImage = (ImageView) gridItem.findViewById(R.id.grid_image);
            this.gridImage.setLayoutParams(new GridView.LayoutParams(160, 160));
            this.gridImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.gridImage.setPadding(4, 4, 4, 4);
        }

    }
}
