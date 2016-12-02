package com.snapsigns.nearby_signs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentContainer;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.snapsigns.utilities.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Displays Nearby Signs
 * For now uses test images.
 * Right Left buttons to go through the images.
 */
public class NearbySignsFragment extends BaseFragment {
    private final static String TAG = NearbySignsFragment.class.getSimpleName();
    private ViewPager mPager;
    private SignPagerAdapter mSignPageAdapter;


    @Override
    public void onStart() {
        registerImageSignReceiver();
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_sign_view_pager, container, false);
        mSignPageAdapter = new SignPagerAdapter(getActivity());

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mSignPageAdapter);

        return rootView;
    }

    /******** Broadcast Receiver in charge of notifying adapter when signs are downloaded *******/
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS)){
                Log.v(TAG,"Retrieved broadcast to update user signs");
                mSignPageAdapter.notifyDataSetChanged();
            }
        }
    };

    public void registerImageSignReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }
}
