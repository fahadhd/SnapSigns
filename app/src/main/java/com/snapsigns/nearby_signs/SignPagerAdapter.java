package com.snapsigns.nearby_signs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by admin on 11/29/2016.
 */

public class SignPagerAdapter extends PagerAdapter {
    MainActivity mActivity;
    LayoutInflater mLayoutInflater;
    List<ImageSign> mNearbySigns;
    ViewPager mPager;
    boolean isFullScreen;
    private int numViews;

    public SignPagerAdapter(MainActivity activity, ViewPager pager) {
        mActivity = activity;
        mPager = pager;
        SnapSigns appContex = (SnapSigns) mActivity.getApplicationContext();

        mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNearbySigns = ((SnapSigns) mActivity.getApplicationContext()).getFilteredNearbySigns();
        numViews = mNearbySigns.size();
        isFullScreen = NearbySignsFragment.isFullScreen;
    }


    public void updateSize(){
        numViews = mNearbySigns.size();
    }

    @Override
    public int getCount() {
        return numViews;
    }





    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    private class ViewHolder{
        Toolbar toolbar;
        TextView messageView;
        ImageView imageView;
        TextView title;
        ImageButton gridButton;
        ImageButton favoriteButton;
        GifImageView loadingView;

        public ViewHolder(View itemView){
            toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
            messageView = (TextView) itemView.findViewById(R.id.message);
            imageView = (ImageView) itemView.findViewById(R.id.pager_sign);
            title = (TextView) itemView.findViewById(R.id.nearby_signs_toolbar_title);
            gridButton = (ImageButton) itemView.findViewById(R.id.grid_activity_button);
            favoriteButton = (ImageButton) itemView.findViewById(R.id.favorite_button);
            loadingView = (GifImageView) itemView.findViewById(R.id.loading_view);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.nearby_sign_pager_item, container, false);
        Log.v("SignPagerAdapter","Is Full Screen option true?: "+isFullScreen);

        final ViewHolder viewHolder = new ViewHolder(itemView);
        final ImageSign currentSign = mNearbySigns.get(position);

        if(isFullScreen) setUpFullScreen(viewHolder);
        else cancelFullScreen(viewHolder,currentSign);

        viewHolder.title.setText(currentSign.locationName);

        /*************** Setting button Listeners ******************/
        viewHolder.gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SnapSigns)mActivity.getApplicationContext()).populateAllTags();
                mActivity.startActivity(new Intent(mActivity,NearbySignsGridActivity.class));
            }
        });
        viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Finish favorite button
            }
        });

        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!isFullScreen){
                    setUpFullScreen(viewHolder);
                    mPager.setAdapter(SignPagerAdapter.this);
                    mPager.setCurrentItem(position);
                }
                else{
                    cancelFullScreen(viewHolder,currentSign);
                    mPager.setAdapter(SignPagerAdapter.this);
                    mPager.setCurrentItem(position);
                }
                return false;
            }
        });

        /************** Adding image to Image View ****************************/
        viewHolder.loadingView.setVisibility(View.VISIBLE);
        Glide.with(mActivity).load(currentSign.imgURL)
                /*********** Listener  used to display textview when image is done loading *****/
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        if(currentSign.message != null){
                            viewHolder.messageView.setVisibility(View.VISIBLE);
                            viewHolder.messageView.setText(currentSign.message);
                        }
                        viewHolder.loadingView.setVisibility(View.INVISIBLE);
                        return false;
                    }

                }).into(viewHolder.imageView);

        container.addView(itemView);
        /*******************************************************************/

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }


    private void setUpFullScreen(ViewHolder viewHolder){
        viewHolder.toolbar.setVisibility(View.GONE);
        viewHolder.title.setVisibility(View.INVISIBLE);
        viewHolder.gridButton.setVisibility(View.INVISIBLE);
        viewHolder.messageView.setVisibility(View.INVISIBLE);
        viewHolder.favoriteButton.setVisibility(View.INVISIBLE);
        viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mActivity.startFullScreenViewPager();
        isFullScreen = true;
    }

    private void cancelFullScreen(ViewHolder viewHolder, ImageSign currentSign){
        viewHolder.toolbar.setVisibility(View.VISIBLE);
        viewHolder.title.setVisibility(View.VISIBLE);
        viewHolder.gridButton.setVisibility(View.VISIBLE);
        viewHolder.favoriteButton.setVisibility(View.VISIBLE);
        mActivity.restoreMainFromFullScreenViewPager();
        isFullScreen = false;
    }

}