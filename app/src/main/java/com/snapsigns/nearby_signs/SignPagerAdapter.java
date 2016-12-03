package com.snapsigns.nearby_signs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
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

/**
 * Created by admin on 11/29/2016.
 */

public class SignPagerAdapter extends PagerAdapter {
    MainActivity mActivity;
    LayoutInflater mLayoutInflater;
    ArrayList<ImageSign> mNearbySigns;
    private int numViews;

    public SignPagerAdapter(MainActivity activity) {
        mActivity = activity;
        mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNearbySigns = ((SnapSigns) mActivity.getApplicationContext()).getNearbySigns();
        numViews = mNearbySigns.size();

        if(mNearbySigns == null) mNearbySigns = new ArrayList<>();
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.nearby_sign_pager_item, container, false);
        final Toolbar toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
        final ImageSign currentSign = mNearbySigns.get(position);
        final TextView messageView = (TextView) itemView.findViewById(R.id.message);
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_sign);
        final TextView title = (TextView) itemView.findViewById(R.id.nearby_signs_toolbar_title);

        final ImageButton gridButton = (ImageButton) itemView.findViewById(R.id.grid_activity_button);
        final ImageButton favoriteButton = (ImageButton) itemView.findViewById(R.id.favorite_button);

        title.setText(currentSign.locationName);

        /*************** Setting button Listeners ******************/
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity,NearbySignsGridActivity.class));
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(toolbar.getVisibility() == View.VISIBLE){
                    toolbar.setVisibility(View.GONE);
                    title.setVisibility(View.INVISIBLE);
                    gridButton.setVisibility(View.INVISIBLE);
                    messageView.setVisibility(View.INVISIBLE);
                    favoriteButton.setVisibility(View.INVISIBLE);
                    mActivity.startFullScreenViewPager();
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                else{
                    toolbar.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    gridButton.setVisibility(View.VISIBLE);
                    if(currentSign.message != null) messageView.setVisibility(View.VISIBLE);
                    favoriteButton.setVisibility(View.VISIBLE);
                    mActivity.restoreMainFromFullScreenViewPager();
                }

                return false;
            }
        });


        /**********************************************************/



        /************** Adding image to Image View ****************************/
        Glide.with(mActivity).load(currentSign.imgURL).
                placeholder(R.xml.progress_animation)
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
                    messageView.setVisibility(View.VISIBLE);
                    messageView.setText(currentSign.message);
                }
                return false;
            }

        }).into(imageView);

        container.addView(itemView);
        /*******************************************************************/

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
