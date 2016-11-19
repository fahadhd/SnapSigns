package com.snapsigns.my_signs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.squareup.picasso.Picasso;

/**
 * Created by lauradally on 11/18/16.
 */

public class FullImageFragment extends Fragment {
    String imgURL;

    private final static String TAG = "Full_Image_Tag";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"in FullImageFragment onCreate");

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG,"in FullImageFragment onCreateView");
        View rootView = inflater.inflate(R.layout.full_sign,container);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.full_sign_view);
        imgURL = getArguments().getString("imgURL");
        if(imgURL != null && imageView != null) {
            Log.i(TAG,"imgURL not null");
            Picasso.with(getContext()).load(imgURL).into(imageView);
        }


        return rootView;


    }

    /*@Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.full_sign);

        Intent i = getIntent();
        String imgURL = i.getExtras().getString("imgURL");

        ImageView imageView = (ImageView) findViewById(R.id.full_sign_view);
        Picasso.with(this).load(imgURL).into(imageView);
    }*/
}
