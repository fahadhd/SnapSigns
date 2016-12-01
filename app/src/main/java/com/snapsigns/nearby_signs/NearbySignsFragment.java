package com.snapsigns.nearby_signs;

import android.os.Bundle;
import android.support.v4.app.FragmentContainer;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
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
    private ViewPager mPager;
    RelativeLayout.LayoutParams fragmentContainerLayoutParams;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_sign_view_pager, container, false);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(new SignPagerAdapter(getActivity()));
//
//        FrameLayout fragmentContainer = (FrameLayout) getActivity().findViewById(R.id.fragment_container);
//        fragmentContainerLayoutParams =
//                (RelativeLayout.LayoutParams)fragmentContainer.getLayoutParams();
//        fragmentContainerLayoutParams.removeRule(RelativeLayout.ABOVE);

        return rootView;
    }

    @Override
    public void onDestroy() {
       // fragmentContainerLayoutParams.addRule(RelativeLayout.ABOVE,R.id.bottomBar);
        super.onDestroy();
    }
}
