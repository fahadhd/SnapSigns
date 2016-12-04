package com.snapsigns.nearby_signs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.snapsigns.utilities.Constants;
import com.snapsigns.utilities.FireBaseUtility;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Displays Nearby Signs
 * For now uses test images.
 * Right Left buttons to go through the images.
 */
public class NearbySignsFragment extends BaseFragment {
    public static  ViewPager mPager;
    View rootView;
    MainActivity mActivity;
    SnapSigns appContext;
    FireBaseUtility fireBaseUtility;

    SlidingUpPanelLayout mLayout;
    LinearLayout commentView;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    Button postBtn;
    ImageButton commentsButton, hideCommentBox;
    EditText addComment;

    ImageSign mCurrImageSign;
    List<ImageSign> mNearbySigns;
    SignPagerAdapter mSignPageAdapter;
    ViewTreeObserver viewTreeObserver;


    public static boolean isFullScreen = false;
    private boolean isHideComments = true;
    public static final int PAGER_REQUEST = 43;
    public static final String POSITION_KEY = "position_key";
    private static final String TAG = NearbySignsFragment.class.getSimpleName();

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

        rootView = inflater.inflate(R.layout.nearby_sign_view_pager, container, false);
        mActivity = (MainActivity) getActivity();
        fireBaseUtility = new FireBaseUtility(mActivity);
        appContext = (SnapSigns)mActivity.getApplicationContext();
        mNearbySigns = appContext.getNearbySigns();

        /************************ Toolbar Views ****************/
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ImageButton gridButton = (ImageButton) rootView.findViewById(R.id.grid_activity_button);
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SnapSigns)mActivity.getApplicationContext()).populateAllTags();

                Fragment currentFragment =
                        mActivity.getSupportFragmentManager().findFragmentByTag(MainActivity.NEARBY_SIGNS_FRAGMENT);

                startActivity(new Intent(mActivity,NearbySignsGridActivity.class));
            }
        });

        ImageButton favoriteButton = (ImageButton) rootView.findViewById(R.id.favorite_button);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        /*********************** ViewPager Views ***************/
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mSignPageAdapter = new SignPagerAdapter(mActivity,rootView);
        mPager.setAdapter(mSignPageAdapter);


        /******************* FullScreen Buttons ******************/
        final ImageButton showFullScreen = (ImageButton) rootView.findViewById(R.id.show_fullscreen_button);
        final ImageButton hideFullScreen = (ImageButton) rootView.findViewById(R.id.hide_fullscreen_button);


        showFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setVisibility(View.GONE);
                mActivity.hideBottomBar();
                showFullScreen.setVisibility(View.INVISIBLE);
                hideFullScreen.setVisibility(View.VISIBLE);
            }
        });

        hideFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setVisibility(View.VISIBLE);
                mActivity.showBottomBar();
                hideFullScreen.setVisibility(View.INVISIBLE);
                showFullScreen.setVisibility(View.VISIBLE);
            }
        });

        /**************** Comment Box Views ********************/
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        if(mNearbySigns.isEmpty()) {
            mPager.setVisibility(View.INVISIBLE);
            mActivity.showLoadingView();

            Snackbar snackbar = Snackbar.make(mActivity.findViewById(android.R.id.content), "Searching for nearby signs....", Snackbar.LENGTH_LONG);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
            ViewGroup.LayoutParams params= layout.getLayoutParams();
            params.height = 220;
            layout.setLayoutParams(params);
            snackbar.show();
            layout.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.dark_purple));
        }

        setupCommentBox();

        return rootView;
    }

    /******** Broadcast Receiver in charge of notifying adapter when signs are downloaded *******/
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS)){
                Log.v(TAG,"Retrieved broadcast to update user signs");
                mSignPageAdapter = new SignPagerAdapter(mActivity,rootView);
                mPager.setVisibility(View.VISIBLE);

                viewTreeObserver = mPager.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(mActivity != null) {
                            mPager.setAdapter(mSignPageAdapter);
                            mActivity.hideLoadingView();
                            mPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
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
        /**************** Setting default options ******************/
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        ArrayList<String> comments;

        if(!mNearbySigns.isEmpty() && mNearbySigns.get(0).comments != null) {
            comments = mNearbySigns.get(0).comments;
        }
        else{
            comments = new ArrayList<>();
        }

        /********** Setting up Views ***************/
        commentView = (LinearLayout)rootView.findViewById(R.id.comment_view);
        addComment = (EditText) rootView.findViewById(R.id.add_comment);
        listView = (ListView) rootView.findViewById(R.id.comment_list);
        postBtn = (Button) rootView.findViewById(R.id.post_button);
        hideCommentBox = (ImageButton) rootView.findViewById(R.id.slide_down_button);

        commentsButton = (ImageButton) rootView.findViewById(R.id.comments_button);

        arrayAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                comments );

        listView.setAdapter(arrayAdapter);

        /************* Setting Comment Button Listeners *********************/

//        if(!mNearbySigns.isEmpty()) {
//            Log.i(TAG,"should have nearby signs");
//            mCurrImageSign = mNearbySigns.get(0);
//            if(mCurrImageSign.comments != null)
//                comments.addAll(mCurrImageSign.comments);
//        } else {
//            Log.i(TAG,"no nearby signs");
//        }
        hideCommentBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLayout != null) {
                    if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        Log.i(TAG, "changing button to show");
                        commentsButton.setImageResource(R.drawable.btn_show_comments);
                    } else {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                }
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNearbySigns = mSignPageAdapter.mNearbySigns;
                ImageSign currentSign = mNearbySigns.get(mPager.getCurrentItem());

                if(currentSign.comments == null) currentSign.comments = new ArrayList<String>();

                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String commentMessage = addComment.getText().toString();

                if(userName != null) commentMessage = userName+": "+commentMessage;

                currentSign.comments.add(commentMessage);


                arrayAdapter.notifyDataSetChanged();
                addComment.getText().clear();

                if(mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }

                fireBaseUtility.updateImageSign(currentSign);
            }
        });

        /* Sets a listener for when the pages change so we can change the comments with it*/
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mNearbySigns = mSignPageAdapter.mNearbySigns;
                final ImageSign currentSign = mNearbySigns.get(position);
                arrayAdapter.clear();

                if(currentSign.comments == null){
                    currentSign.comments = new ArrayList<>();
                }
                arrayAdapter.addAll(currentSign.comments);
                arrayAdapter.notifyDataSetChanged();

                /********* Setting post button listener to current sign *************/
                /********************************************************************/
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

    private void showCommentBox(){
        mPager.setVisibility(View.INVISIBLE);

    }
}
