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

        mCurrentSignView = (ImageView) rootView.findViewById(R.id.singleNearby);
        mSignNumber = (TextView) rootView.findViewById(R.id.nearbyNumber);
        mNearbySigns = ((SnapSigns)(getActivity().getApplicationContext())).getMyImageSigns();

        ImageButton left = (ImageButton) rootView.findViewById(R.id.left_nearby);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementLeft();
            }
        });

        ImageButton right = (ImageButton) rootView.findViewById(R.id.right_nearby);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementRight();
            }
        });

        if (mNearbySigns != null && mNearbySigns.size() > 0) {

            Picasso.with(getContext()).load(mNearbySigns.get(mCurrentSignIndex).getImgURL()).
                    into(mCurrentSignView);
            mSignNumber.setText(String.valueOf(mCurrentSignIndex + 1));

        }
        return rootView;
    }

    public void incrementRight(){
        if (mNearbySigns != null  && mNearbySigns.size() > 0 && (mCurrentSignIndex < mNearbySigns.size()-1)){
                mCurrentSignIndex++;

                Picasso.with(getContext()).load(mNearbySigns.get(mCurrentSignIndex).getImgURL()).
                        into(mCurrentSignView);
                mSignNumber.setText(Integer.toString(mCurrentSignIndex +1));

        }
    }

    public void incrementLeft(){
        if (mNearbySigns != null  && mNearbySigns.size() > 0 && mCurrentSignIndex > 0){
                mCurrentSignIndex--;
                Picasso.with(getContext()).load(mNearbySigns.get(mCurrentSignIndex).getImgURL()).
                        into(mCurrentSignView);
            mSignNumber.setText(Integer.toString(mCurrentSignIndex +1));
            }
    }


}
