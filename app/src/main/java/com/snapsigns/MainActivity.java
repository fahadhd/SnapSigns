package com.snapsigns;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.snapsigns.create_sign.CameraFragment;
import com.snapsigns.login.SignInActivity;
import com.snapsigns.my_signs.MySignsFragment;
import com.snapsigns.nearby_signs.NearbySignsFragment;
import com.snapsigns.settings.SettingsFragment;
import com.snapsigns.utilities.AddTagDialog;
import com.snapsigns.utilities.FireBaseUtility;

import java.io.File;
import java.util.ArrayList;

import cn.qqtheme.framework.picker.OptionPicker;
import co.lujun.androidtagview.TagContainerLayout;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,AddTagDialog.Communicator {
    public static GoogleApiClient mGoogleApiClient;
    FragmentManager mFragmentManager;
    FrameLayout mCameraFragmentContainer,mFragmentContainer;
    LinearLayout mLocationDisplay;
    ImageButton mCaptureButton, mSaveSignButton, mExitPreviewButton, mAddTextButton;
    TextView mAddTagButton;
    EditText mLocationView,mEnterTextView;
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

    private static final String USER_LOGIN_SUCCESS = "login_success";

    public static final int PICTURE_TAKEN = 23;

    private FireBaseUtility fireBaseUtility;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationDisplay = (LinearLayout) findViewById(R.id.location_display);

        mCaptureButton = (ImageButton) findViewById(R.id.button_capture);
        mExitPreviewButton = (ImageButton) findViewById(R.id.exit_preview);
        mSaveSignButton = (ImageButton) findViewById(R.id.save_sign);
        mAddTextButton = (ImageButton) findViewById(R.id.btn_add_text) ;
        mAddTagButton = (TextView) findViewById(R.id.btn_add_tag) ;

        mTagContainerLayout = (TagContainerLayout) findViewById(R.id.tag_container);

        mLocationView = (EditText) findViewById(R.id.location_name);
        mEnterTextView = (EditText) findViewById(R.id.enter_text);

        SnapSigns app = (SnapSigns) getApplication();

        mGoogleApiClient = app.getmGoogleApiClient();

        //Camera fragment is always active but its view hides if other tab is selected
        mCameraFragmentContainer = (FrameLayout)findViewById(R.id.camera_fragment_container);

        //Used to house other fragments other than camera fragment
        mFragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);

        mFragmentManager = getSupportFragmentManager();
        setBottomBarListeners();
        setPhotoTakeLayoutListeners();

        mAuth = app.getFirebaseAuth();

        if (mAuth.getCurrentUser() == null) {
            startActivityForResult(new Intent(this, SignInActivity.class),
                    SignInActivity.SIGN_IN_REQUEST_CODE);

        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            mBottomBar.selectTabAtPosition(2);
        }
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
                            targetFragment = new SettingsFragment();
                            mCurrentFragment = SETTINGS_FRAGMENT;
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
        mAddTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTagDialog tagDialog = new AddTagDialog();
                tagDialog.show(getFragmentManager(),"Add Tag Dialog");
            }
        });





        /************** Setting up Add Text UI *******************/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEnterTextView, InputMethodManager.SHOW_IMPLICIT);

        mAddTextButton.setOnClickListener(new View.OnClickListener() {
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


        mExitPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPreviewData();
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
                mSaveSignButton.setOnClickListener(new View.OnClickListener() {
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

                /////// Core Button and Location Items /////////
                mExitPreviewButton.setVisibility(View.VISIBLE);
                mSaveSignButton.setVisibility(View.VISIBLE);
                mLocationDisplay.setVisibility(View.VISIBLE);

                //////////// Add Text Items /////////////////
                mAddTextButton.setVisibility(View.VISIBLE);

                /////////////// Tag View Items ////////////
                mAddTagButton.setVisibility(View.VISIBLE);
                mTagContainerLayout.setVisibility(View.VISIBLE);

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

                ///////////// Hiding preview items ////////////////////

                /////// Core Button and Location Items /////////
                mExitPreviewButton.setVisibility(View.INVISIBLE);
                mSaveSignButton.setVisibility(View.INVISIBLE);
                mLocationDisplay.setVisibility(View.INVISIBLE);

                //////////// Add Text Items /////////////////
                mEnterTextView.setVisibility(View.INVISIBLE);
                mAddTextButton.setVisibility(View.INVISIBLE);

                /////////////// Tag View Items ////////////
                mTagContainerLayout.setVisibility(View.INVISIBLE);
                mAddTagButton.setVisibility(View.INVISIBLE);

                //If the sign was saved then go to MySignsFragment
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

    public void resetPreviewData(){
        mTagContainerLayout.removeAllTags();
        mEnterTextView.setText(null);
    }

    @Override
    public void addTag(String tag) {
        mTagContainerLayout.addTag(tag);
    }
}
