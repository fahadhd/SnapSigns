package com.snapsigns.nearby_signs;

import android.os.Bundle;
import android.support.v4.app.FragmentContainer;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Displays Nearby Signs
 * For now uses test images.
 * Right Left buttons to go through the images.
 */
public class NearbySignsFragment extends BaseFragment {
    private ViewPager mPager;
    private SignPagerAdapter mPagerAdapter;
    private SlidingUpPanelLayout mLayout;
    private String TAG = "nearby_signs_tag";
    private ImageSign mCurrImageSign;
    ArrayList<ImageSign> mNearbySigns;
    ArrayList<String> comments = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_sign_view_pager, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.comment_list);

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                comments );

        lv.setAdapter(arrayAdapter);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new SignPagerAdapter(getActivity());
        mPager.setAdapter(mPagerAdapter);


        this.mNearbySigns = mPagerAdapter.mNearbySigns;

        comments.add("First Entry");

        if(!mNearbySigns.isEmpty()) {
            Log.i(TAG,"should have nearby signs");
            mCurrImageSign = mNearbySigns.get(0);
            if(mCurrImageSign.comments != null)
                comments.addAll(mCurrImageSign.comments);
        } else {
            Log.i(TAG,"no nearby signs");
        }

        /* Sets a listener for when the pages change so we can change the comments with it*/
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageSign imageSign = mNearbySigns.get(position);
//                arrayAdapter.clear();
                //TODO: Error found here
//                arrayAdapter.addAll(imageSign.comments);
//                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        final EditText addComment = (EditText) rootView.findViewById(R.id.add_comment);
        Button postBtn = (Button) rootView.findViewById(R.id.post_button);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = addComment.getText().toString();
                //TODO add comment to imageSign
                arrayAdapter.add(newComment);
                arrayAdapter.notifyDataSetChanged();
                addComment.getText().clear();
            }
        });


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
