package com.snapsigns.nearby_signs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.my_signs.MySignsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Displays Nearby Signs
 * For now uses test images.
 * Right Left buttons to go through the images.
 */
public class NearbySignsFragment extends BaseFragment {
    ArrayList<ImageSign> nearbySigns = null;
    ImageView currentSignView;
    TextView num;
    int currentSignIndex;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_sign_single_view, container, false);
        currentSignView = (ImageView) rootView.findViewById(R.id.singleNearby);
        TextView num = (TextView) rootView.findViewById(R.id.nearbyNumber);

        nearbySigns = MainActivity.mNearbySigns;

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

        if (nearbySigns != null && nearbySigns.size() > 0) {

            Picasso.with(getContext()).load(Integer.parseInt(nearbySigns.get(currentSignIndex).getImgURL())).
                    into(currentSignView);
            num.setText(String.valueOf(currentSignIndex + 1));

        }
        return rootView;
    }

    public void incrementRight(){
        if (nearbySigns != null){
            if(currentSignIndex < nearbySigns.size()-1){
                currentSignIndex++;

                Picasso.with(getContext()).load(Integer.parseInt(nearbySigns.get(currentSignIndex).getImgURL())).
                        into(currentSignView);
                num.setText(String.valueOf(currentSignIndex+1));

            }
        }
    }

    public void incrementLeft(){
        if (nearbySigns != null){
            if(currentSignIndex > 0){
                currentSignIndex--;
                Picasso.with(getContext()).load(Integer.parseInt(nearbySigns.get(currentSignIndex).getImgURL())).
                        into(currentSignView);
                num.setText(String.valueOf(currentSignIndex+1));
            }
        }
    }


}
