package com.snapsigns.favorite_signs;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.utilities.FireBaseUtility;

public class FavoriteSignsFragment extends BaseFragment {
    private static final String TAG = "FavoriteSignsFragment";
    private FavoriteSignsAdapter mAdapter;
    private MainActivity mActivity;
    private GridView gridView;
    private ImageView fullScreenContainer;
    private TextView imageMessage;
    private ImageButton exitFullscreenBtn;
    private ImageButton unfavoriteBtn;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "in onCreateView of FavoriteSignsFragment");
        mActivity = (MainActivity) getActivity();

        View root = inflater.inflate(R.layout.favorite_signs_fragment, container, false);
        gridView = (GridView) root.findViewById(R.id.favorites_grid);
        fullScreenContainer = (ImageView) root.findViewById(R.id.fullscreen_container);
        imageMessage = (TextView) root.findViewById(R.id.message);
        exitFullscreenBtn = (ImageButton) root.findViewById(R.id.exit_fullscreen);
        unfavoriteBtn = (ImageButton) root.findViewById(R.id.unfavorite);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);

        mAdapter = new FavoriteSignsAdapter(this);
        gridView.setAdapter(mAdapter);

        exitFullscreenBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitFullscreen();
                    }
                }
        );

        mActivity.getFireBaseUtility().getFavorites();

        return root;
    }

    public void showFullScreenImage(final ImageSign sign) {
        Glide.with(mActivity).load(sign.imgURL).listener(
                new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                           Target<GlideDrawable> target, boolean isFromMemoryCache,
                           boolean isFirstResource) {
                        fullScreenContainer.setVisibility(View.VISIBLE);

                        if (sign.message != null) {
                            imageMessage.setText(sign.message);
                            imageMessage.setVisibility(View.VISIBLE);
                        }

                        return false;
                    }

                }
        ).into(fullScreenContainer);

        unfavoriteBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.getFireBaseUtility().unfavorite(sign);
                        mAdapter.updateDataSet();
                        exitFullscreen();
                    }
                }
        );

        exitFullscreenBtn.setVisibility(View.VISIBLE);
        unfavoriteBtn.setVisibility(View.VISIBLE);

        gridView.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.INVISIBLE);

        mActivity.hideBottomBar();
    }

    public void exitFullscreen(){
        toolbar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.VISIBLE);
        mActivity.showBottomBar();
        exitFullscreenBtn.setVisibility(View.INVISIBLE);
        unfavoriteBtn.setVisibility(View.INVISIBLE);
        fullScreenContainer.setVisibility(View.INVISIBLE);
        imageMessage.setVisibility(View.INVISIBLE);
    }
}
