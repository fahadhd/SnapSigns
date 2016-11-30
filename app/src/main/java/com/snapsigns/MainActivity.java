package com.snapsigns;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.snapsigns.create_sign.CameraFragment;
import com.snapsigns.my_signs.MySignsFragment;
import com.snapsigns.nearby_signs.NearbySignsFragment;
import com.snapsigns.utilities.FireBaseUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;
import co.lujun.androidtagview.TagContainerLayout;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static GoogleApiClient mGoogleApiClient;
    FragmentManager mFragmentManager;
    FrameLayout mCameraFragmentContainer,mFragmentContainer;
    LinearLayout mLocationDisplay;
    ImageButton mCaptureButton,mSaveSign,mExitPreview,mAddText;
    EditText mLocationView,mEnterTextView;
    AutoCompleteTextView mAddTagView;
    OptionPicker mLocationPicker;
    BottomBar mBottomBar;
    TagContainerLayout mTagContainerLayout;
    String mCurrentFragment;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MY_SIGNS_FRAGMENT = "my_signs_fragment";
    private static final String CREATE_SIGN_FRAGMENT = "camera_fragment";
    private static final String NEARBY_SIGNS_FRAGMENT = "nearby_signs_fragment";
    private static final String FAVORITES_FRAGMENT = "favorites_fragment";
    private static final String SETTINGS_FRAGMENT = "settings_fragment";

    public static final int PICTURE_TAKEN = 23;

    private FireBaseUtility fireBaseUtility;
    private SignIn signIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationDisplay = (LinearLayout) findViewById(R.id.location_display);

        mCaptureButton = (ImageButton) findViewById(R.id.button_capture);
        mExitPreview = (ImageButton) findViewById(R.id.exit_preview);
        mSaveSign = (ImageButton) findViewById(R.id.save_sign);
        mAddText = (ImageButton) findViewById(R.id.btn_add_text) ;
        mTagContainerLayout = (TagContainerLayout) findViewById(R.id.tag_container);
        mTagContainerLayout.setTags(new String[]{"Weird Rally","Washington DC"});


        mLocationView = (EditText) findViewById(R.id.location_name);
        mEnterTextView = (EditText) findViewById(R.id.enter_text);
        mAddTagView = (AutoCompleteTextView) findViewById(R.id.enter_tag) ;



        mGoogleApiClient = ((SnapSigns)getApplicationContext()).getmGoogleApiClient();

        //Camera fragment is always active but its view hides if other tab is selected
        mCameraFragmentContainer = (FrameLayout)findViewById(R.id.camera_fragment_container);

        //Used to house other fragments other than camera fragment
        mFragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);

        mFragmentManager = getSupportFragmentManager();
        setBottomBarListeners();
        setPhotoTakeLayoutListeners();


       // signIn = ((SnapSigns) getApplicationContext()).getSignIn();
        //signIn.setActivity(this);

//        if (signIn.getCurrentUser() == null) {
//            signIn.signIn();
//        }


    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        startMainActivityLayout(false);
        super.onResume();
    }

    /**
     * Initialize bottom-bar tab listeners for fragments
     */
    public void setBottomBarListeners(){
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

        if(mBottomBar != null) {

            //Defaults tab position to be "create sign" and starts camera fragment
            mBottomBar.selectTabAtPosition(2);

            mFragmentManager.beginTransaction()
                    .replace(R.id.camera_fragment_container,new CameraFragment(),"camera_fragment")
                    .commit();


            mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                /**
                 * Determine which fragment to create based on bottom bar tab id
                 */
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    BaseFragment targetFragment = null;
                    switch(tabId){
                        case R.id.tab_my_signs:
                            targetFragment = new MySignsFragment();
                            mCurrentFragment = MY_SIGNS_FRAGMENT;
                            break;

                        case R.id.tab_favorites:
                            break;

                        case R.id.tab_create_sign:
                            mCurrentFragment = CREATE_SIGN_FRAGMENT;
                            mFragmentContainer.setVisibility(View.GONE);
                            mCaptureButton.setVisibility(View.VISIBLE);
                            mCameraFragmentContainer.setVisibility(View.VISIBLE);
                            break;

                        case R.id.tab_nearby_signs:
                            targetFragment = new NearbySignsFragment();
                            mCurrentFragment = NEARBY_SIGNS_FRAGMENT;
                            break;

                        case R.id.tab_settings:
                            break;

                        //In case no tab is selected, use "my_signs" as default
                        default:
                            targetFragment = new MySignsFragment();
                            mCurrentFragment = MY_SIGNS_FRAGMENT;
                            break;
                    }
                    if(!mCurrentFragment.equals(CREATE_SIGN_FRAGMENT)){
                        displayFragment(targetFragment);
                    }

                }
            });
        }
    }



    //Displays selected fragment overlaid on top of camera fragment for efficiency
    public void displayFragment(BaseFragment targetFragment){
        if(targetFragment != null) {
            mCaptureButton.setVisibility(View.INVISIBLE);
            mCameraFragmentContainer.setVisibility(View.GONE);
            mFragmentContainer.setVisibility(View.VISIBLE);
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,targetFragment,mCurrentFragment)
                    .commit();
        }

    }

    /****************** Activity Result Methods ******************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SignIn.RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    Log.d(TAG, "Sign in succeeded.");
                    signIn.firebaseAuthWithGoogle(result.getSignInAccount());

                } else {
                    Log.w(TAG, "Failed to sign in");
                    Toast.makeText(this, "Failed to sign in.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    /******************* Google API Client Methods ***************************/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**************************** Photo Taken Methods *****************************/


    private void setPhotoTakeLayoutListeners(){

        /*********** Setting up Location View UI *****************/
        ArrayList<String> nearbyFakeLocations= new ArrayList<>();
        nearbyFakeLocations.add(0,"CMSC Building, College Park MD");
        nearbyFakeLocations.add(0,"Some close by building, City/State its in");
        mLocationPicker = new OptionPicker(this, nearbyFakeLocations);
        mLocationPicker.setOffset(2);
        mLocationPicker.setSelectedIndex(1);
        mLocationPicker.setTextSize(13);
        mLocationPicker.setTitleText("Locations Near You");
        mLocationPicker.setTitleTextSize(15);
        mLocationPicker.setCancelTextColor(ContextCompat.getColor(this, R.color.red_900));
        mLocationPicker.setSubmitTextColor(ContextCompat.getColor(this, R.color.dark_purple));
        mLocationPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int position, String option) {
                mLocationView.setText(option);
            }

        });

        /************** Setting up Tag View UI *******************/




        /************** Setting up Add Text UI *******************/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEnterTextView, InputMethodManager.SHOW_IMPLICIT);

        mAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Displaying edit text view
                mEnterTextView.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEnterTextView, InputMethodManager.SHOW_IMPLICIT);
                mEnterTextView.setCursorVisible(true);
            }
        });



        /*************Exit Button and Location View UI************/


        mExitPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivityLayout(false);
            }
        });

        mLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationPicker.show();
            }
        });
    }



    /**
     * Displays picutre user just took and other options
     * @param pictureFile
     */
    public void startPhotoTakenLayout(final File pictureFile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSaveSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = null;
                        if(mEnterTextView.getVisibility() == View.VISIBLE){
                            message = mEnterTextView.getEditableText().toString();
                        }
                        fireBaseUtility = new FireBaseUtility(MainActivity.this);
                        fireBaseUtility.uploadImageToFireBase(pictureFile,message);
                        startMainActivityLayout(true);

                    }
                });
                /////// Hides unnecessary UI elements during a taken preview ////////
                mBottomBar.setVisibility(View.INVISIBLE);
                mCaptureButton.setVisibility(View.INVISIBLE);

                /////////// Displaying preview taken UI elements ////////////////

                mExitPreview.setVisibility(View.VISIBLE);
                mSaveSign.setVisibility(View.VISIBLE);
                mLocationDisplay.setVisibility(View.VISIBLE);
                mAddText.setVisibility(View.VISIBLE);

            }
        });

    }

    /**
     * Displays the original activity layout once a preview is over.
     * @param signSaved - If sign was saved, display MySigns Fragment
     */
    public void startMainActivityLayout(final boolean signSaved){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Displaying a new camera preview fragment after taking a picture
                mFragmentManager.beginTransaction()
                        .replace(R.id.camera_fragment_container,new CameraFragment(),"camera_fragment")
                        .commit();

                //////////////Displaying and hiding original UI elements ////////////////
                mBottomBar.setVisibility(View.VISIBLE);

                //Only display capture button if on camera fragment
                if(mCurrentFragment.equals(CREATE_SIGN_FRAGMENT))
                    mCaptureButton.setVisibility(View.VISIBLE);

                mExitPreview.setVisibility(View.INVISIBLE);
                mSaveSign.setVisibility(View.INVISIBLE);
                mLocationDisplay.setVisibility(View.INVISIBLE);
                mEnterTextView.setVisibility(View.INVISIBLE);
                mAddText.setVisibility(View.INVISIBLE);

                if(signSaved){
                    mCurrentFragment = MY_SIGNS_FRAGMENT;
                    mBottomBar.selectTabAtPosition(0);
                }



            }
        });
    }


    /**
     * Used to hide keyboard and cursor of edit text view when user taps out of its box
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if(v.equals(mEnterTextView)) {
                        mEnterTextView.setCursorVisible(false);
                        if (mEnterTextView.getText().toString().isEmpty()) {
                            mEnterTextView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
