package com.snapsigns.nearby_signs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Displays Nearby Signs
 * For now uses test images.
 * Right Left buttons to go through the images.
 */
public class NearbySignsFragment extends BaseFragment {
    ArrayList<ImageSign> mNearbySigns = null;
    ImageView mCurrentSignView;
    TextView mSignNumber;
    int mCurrentSignIndex;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_sign_single_view, container, false);



        return rootView;
    }

    public void loadIntoImageView(){
        Picasso.with(getContext()).
                load(mNearbySigns.get(mCurrentSignIndex).imgURL).
                resize(800,800).
                into(mCurrentSignView);
    }


}
