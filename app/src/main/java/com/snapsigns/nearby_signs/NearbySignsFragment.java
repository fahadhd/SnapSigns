package com.snapsigns.nearby_signs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.R;
import com.snapsigns.utilities.Constants;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays Nearby Signs
 * For now uses test images.
 * Right Left buttons to go through the images.
 */
public class NearbySignsFragment extends BaseFragment {
    private ViewPager mPager;
    private SlidingUpPanelLayout mLayout;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    private String TAG = "nearby_signs_tag";
    private ImageSign mCurrImageSign;
    List<ImageSign> mNearbySigns;
    private SignPagerAdapter mSignPageAdapter;
    EditText addComment;
    List<String> comments = new ArrayList<>();
    Button postBtn;

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

        /*********************** ViewPager Views ***************/
        mSignPageAdapter = new SignPagerAdapter(getActivity());
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mSignPageAdapter);

        /**************** Comment Box Views ********************/
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        listView = (ListView) rootView.findViewById(R.id.comment_list);
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
       arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                comments );
        listView.setAdapter(arrayAdapter);

        addComment = (EditText) rootView.findViewById(R.id.add_comment);
        postBtn = (Button) rootView.findViewById(R.id.post_button);
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

        setupCommentBox();

        return rootView;
    }

    /******** Broadcast Receiver in charge of notifying adapter when signs are downloaded *******/
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS)){
                Log.v(TAG,"Retrieved broadcast to update user signs");
                mSignPageAdapter.updateDataSet();

                if (mNearbySigns.size() != 0) {
                    mPager.setCurrentItem(0);
                }
            }
        }
    };

    public void registerImageSignReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }
    /********************************************************************************************/


    /****** Setting up comment box *******/
    public void setupCommentBox(){
        mNearbySigns = mSignPageAdapter.mNearbySigns;

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
                //TODO: Error found here
                //arrayAdapter.clear();
                //arrayAdapter.addAll(imageSign.comments);
                //arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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


    }
}
