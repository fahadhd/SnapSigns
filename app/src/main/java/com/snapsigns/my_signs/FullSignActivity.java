package com.snapsigns.my_signs;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.snapsigns.ImageSign;
import com.snapsigns.R;

/**
 * Created by lauradally on 11/30/16.
 */

public class FullSignActivity extends Activity {

    private final static String TAG = "My_Signs_Tag";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"in onCreate of FullImageActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_sign);



        ImageSign imageSign = (ImageSign) getIntent().getExtras().getSerializable(MySignsFragment.IMAGE_URL_KEY);
        ImageView imageView = (ImageView) findViewById(R.id.full_sign_view);



        Glide.with(this).load(imageSign.imgURL).
                placeholder(R.xml.progress_animation).into(imageView);


    }
}
