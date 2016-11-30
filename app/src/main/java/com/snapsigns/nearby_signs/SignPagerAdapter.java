package com.snapsigns.nearby_signs;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
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

import java.util.ArrayList;

/**
 * Created by admin on 11/29/2016.
 */

public class SignPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<ImageSign> mNearbySigns;
    private int mSignWidth;
    private int mSignHeight;

    public SignPagerAdapter(Context context){
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNearbySigns = ((SnapSigns)mContext.getApplicationContext()).getNearbySigns();

        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        mSignWidth = size.x;
        mSignHeight = size.y;
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

        ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_sign);
        final TextView messageView = (TextView) itemView.findViewById(R.id.message);

        final ImageSign currentSign = mNearbySigns.get(position);
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
            } /********************************************************************************/

        }).into(imageView);



        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
