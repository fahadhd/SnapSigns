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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Adapter is in charge of populating the listview with list item contents, in this case ImageSigns.
 */
public class NearbySignsGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageSign> nearbySigns;
    private int gridWidth;

    public NearbySignsGridAdapter(Context context) {
        mContext = context;
        nearbySigns = ((SnapSigns) mContext.getApplicationContext()).getFilteredNearbySigns();
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Used to display two ImageSigns per row
        this.gridWidth = size.x / 2;
    }

    @Override
    public int getCount() {
        return nearbySigns.size();
    }

    @Override
    public Object getItem(int position) {
        return nearbySigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //In charge of populating each grid item with a view.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View gridItem = convertView;

        if(gridItem == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridItem = inflater.inflate(R.layout.my_signs_grid_item, parent, false);

            viewHolder = new ViewHolder(gridItem);
            gridItem.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) gridItem.getTag();
        }

        viewHolder.loadingView.setVisibility(View.VISIBLE);

        Glide.with(mContext).load(nearbySigns.get(position).imgURL).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model,
                                           Target<GlideDrawable> target, boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                viewHolder.loadingView.setVisibility(View.INVISIBLE);
                return false;
            }

        }).into(viewHolder.gridImage);


        return gridItem;
    }

    public void updateDataSet() {
        nearbySigns.clear();
        nearbySigns.addAll(((SnapSigns) mContext.getApplicationContext()).getFilteredNearbySigns());
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView gridImage;
        GifImageView loadingView;

        ViewHolder(View gridItem) {
            gridImage = (ImageView) gridItem.findViewById(R.id.grid_image);
            loadingView = (GifImageView) gridItem.findViewById(R.id.loading_view);

            gridImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            gridImage.getLayoutParams().width = gridWidth;
            gridImage.getLayoutParams().height = gridWidth;

            loadingView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            loadingView.getLayoutParams().width = gridWidth;
            loadingView.getLayoutParams().height = gridWidth;
        }
    }
}
