package com.snapsigns.favorite_signs;


import android.app.Activity;
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
import java.util.List;

public class FavoriteSignsAdapter extends BaseAdapter {
    private FavoriteSignsFragment mFragment;
    private Activity mActivity;
    private SnapSigns app;
    private List<ImageSign> favoriteSigns;
    private int gridWidth;

    public FavoriteSignsAdapter(FavoriteSignsFragment fragment) {
        mFragment = fragment;
        mActivity = mFragment.getActivity();
        app = (SnapSigns) mActivity.getApplication();

        favoriteSigns = new ArrayList<>(app.getFavoriteSigns());

        Display display = ((WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        gridWidth = size.x / 2;
    }

    @Override
    public int getCount() {
        return favoriteSigns.size();
    }

    @Override
    public Object getItem(int position) {
        return favoriteSigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View gridItem = convertView;

        if (gridItem == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridItem = inflater.inflate(R.layout.my_signs_grid_item, parent, false);
            
            gridItem.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFragment.showFullScreenImage(favoriteSigns.get(position));
                        }
                    }
            );

            viewHolder = new ViewHolder(gridItem);
            gridItem.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) gridItem.getTag();
        }

        Glide.with(mActivity).load(favoriteSigns.get(position).imgURL).
                placeholder(R.xml.progress_animation).into(viewHolder.gridImage);

        return gridItem;
    }

    public void updateDataSet() {
        favoriteSigns = new ArrayList<>(app.getFavoriteSigns());
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView gridImage;

        ViewHolder(View gridItem) {
            gridImage = (ImageView) gridItem.findViewById(R.id.grid_image);
            gridImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            gridImage.getLayoutParams().width = gridWidth;
            gridImage.getLayoutParams().height = gridWidth;
        }
    }
}
