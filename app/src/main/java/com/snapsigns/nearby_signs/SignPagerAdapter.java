package com.snapsigns.nearby_signs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 11/29/2016.
 */

public class SignPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<ImageSign> mNearbySigns;

    public SignPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNearbySigns = ((SnapSigns) mContext.getApplicationContext()).getFilteredNearbySigns();
    }

    @Override
    public int getCount() {
        return mNearbySigns.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.nearby_sign_pager_item, container, false);
        final ImageSign currentSign = mNearbySigns.get(position);
        final TextView messageView = (TextView) itemView.findViewById(R.id.message);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_sign);
        TextView title = (TextView) itemView.findViewById(R.id.nearby_signs_toolbar_title);

        ImageButton gridButton = (ImageButton) itemView.findViewById(R.id.grid_activity_button);
        ImageButton favoriteButton = (ImageButton) itemView.findViewById(R.id.favorite_button);

        title.setText(currentSign.locationName);

        /*************** Setting button Listeners ******************/
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext,NearbySignsGridActivity.class));
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        /**********************************************************/



        /************** Adding image to Image View ****************************/
        Glide.with(mContext).load(currentSign.imgURL).
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
