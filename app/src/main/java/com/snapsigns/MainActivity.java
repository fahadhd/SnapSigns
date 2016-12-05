package com.snapsigns;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.snapsigns.create_sign.CameraFragment;
import com.snapsigns.favorite_signs.FavoriteSignsFragment;
import com.snapsigns.login.SignInActivity;
import com.snapsigns.my_signs.MySignsFragment;
import com.snapsigns.nearby_signs.NearbySignsFragment;
import com.snapsigns.settings.SettingsFragment;
import com.snapsigns.utilities.AddTagDialog;
import com.snapsigns.utilities.Constants;
import com.snapsigns.utilities.FireBaseUtility;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import app.dinus.com.loadingdrawable.LoadingView;
import cn.qqtheme.framework.picker.OptionPicker;
import co.lujun.androidtagview.TagContainerLayout;


public class MainActivity extends AppCompatActivity implements AddTagDialog.Communicator {
    GoogleApiClient mGoogleApiClient;
    SnapSigns app;
    FragmentManager mFragmentManager;
    FrameLayout mCameraFragmentContainer,mFragmentContainer;
    LinearLayout mLocationDisplay;
    ImageButton mCaptureButton, mSaveSignButton, mExitPreviewButton, mAddTextButton;
    TextView mAddTagButton,mPlacePickerButton;
    EditText mLocationView,mEnterTextView;
    OptionPicker mLocationPicker;

    BottomBar mBottomBar;
    TagContainerLayout mTagContainerLayout;
    String mCurrentFragment;


    LoadingView mLoadingView;
    public boolean signJustSaved = false;
    ArrayList<String> mNearbyLocations = new ArrayList<>();
    ArrayList<String> nearbyFakeLocations= new ArrayList<>();

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MY_SIGNS_FRAGMENT = "my_signs_fragment";
    private static final String CREATE_SIGN_FRAGMENT = "camera_fragment";
    public static final String NEARBY_SIGNS_FRAGMENT = "nearby_signs_fragment";
    private static final String FAVORITES_FRAGMENT = "favorites_fragment";
    private static final String SETTINGS_FRAGMENT = "settings_fragment";

    private static final String USER_LOGIN_SUCCESS = "login_success";

    public static final int PICTURE_TAKEN = 23;
    public static final int PLACE_PICKER_REQUEST = 40;

    private FireBaseUtility fireBaseUtility;
    private FirebaseAuth mAuth;

    private File mPictureFile;
    private boolean mDoneWithPicture = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationDisplay = (LinearLayout) findViewById(R.id.location_display);

        mCaptureButton = (ImageButton) findViewById(R.id.button_capture);
        mPlacePickerButton = (TextView) findViewById(R.id.place_picker);
        mExitPreviewButton = (ImageButton) findViewById(R.id.exit_preview);
        mSaveSignButton = (ImageButton) findViewById(R.id.save_sign);
        mAddTextButton = (ImageButton) findViewById(R.id.btn_add_text) ;
        mAddTagButton = (TextView) findViewById(R.id.btn_add_tag) ;
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        signJustSaved = false;

        mTagContainerLayout = (TagContainerLayout) findViewById(R.id.tag_container);

        mLocationView = (EditText) findViewById(R.id.location_name);
        mEnterTextView = (EditText) findViewById(R.id.enter_text);

        fireBaseUtility = new FireBaseUtility(MainActivity.this);
        app = (SnapSigns) getApplication();

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

        //used by OptionPicker for when there are no nearby locations
        nearbyFakeLocations.add(0,"No nearby locations");

        /* Starts a place picker map activity */
        mPlacePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {

                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG,"GooglePlayServicesNotAvailableException");
                } catch(GooglePlayServicesRepairableException e) {
                    Log.e(TAG,"GooglePlayServicesRepairableException");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        if(!mGoogleApiClient.isConnected()) mGoogleApiClient.connect();
        registerImageSignReceiver();
        super.onStart();
    }

    @Override
    protected void onStop() {
        app.removeLocationUpdates();
        unregisterReceiver(broadcastReceiver);
        if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"in onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"in onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"in onResume");
        //startMainActivityLayout(false);
    }

    /* Need to commit FragmentTransactions after the Activity's state has been restored
    to its original state (onPostResume() is guaranteed to be called after the Activity's state
    has been restored) --> otherwise throws IllegalStateException */
    @Override
    protected void onPostResume() {
        Log.i(TAG,"in onPostResume");
        super.onPostResume();
        startMainActivityLayout(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SignInActivity.SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK){
            mBottomBar.selectTabAtPosition(2);
        } else if(requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this,data);
            String placeName = place.getName().toString();
            mPlacePickerButton.setText(placeName);

            mLocationView.setText(place.getName());
            mNearbyLocations.add(placeName);
            Log.i(TAG,"Selected Place: "+place.getName());
        }
    }

    /**
     * Initialize bottom-bar tab listeners for fragments
     */
    public void setBottomBarListeners(){
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
                            targetFragment = new FavoriteSignsFragment();
                            mCurrentFragment = FAVORITES_FRAGMENT;
                            break;

                        case R.id.tab_create_sign:
                            mCurrentFragment = CREATE_SIGN_FRAGMENT;
                            mFragmentContainer.setVisibility(View.INVISIBLE);
                            mCameraFragmentContainer.setVisibility(View.VISIBLE);
                            mCaptureButton.setVisibility(View.VISIBLE);
                            mPlacePickerButton.setVisibility(View.VISIBLE);
                            mLoadingView.setVisibility(View.INVISIBLE);
                            break;

                        case R.id.tab_nearby_signs:
                            targetFragment = new NearbySignsFragment();
                            mCurrentFragment = NEARBY_SIGNS_FRAGMENT;
                            break;

                        case R.id.tab_settings:
                            targetFragment = new SettingsFragment();
                            mCurrentFragment = SETTINGS_FRAGMENT;
                            mLoadingView.setVisibility(View.INVISIBLE);
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
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,targetFragment,mCurrentFragment)
                    .commit();
            mLoadingView.setVisibility(View.INVISIBLE);
            mCaptureButton.setVisibility(View.INVISIBLE);
            mPlacePickerButton.setVisibility(View.INVISIBLE);
            mCameraFragmentContainer.setVisibility(View.GONE);
            mFragmentContainer.setVisibility(View.VISIBLE);
        }

    }


    /**************************** Photo Taken Methods *****************************/

    /* Called whenever a new LocationPicker is created from a new location */
    private void setLocationPickerOptions() {
        mLocationPicker.setOffset(2);
       // mLocationPicker.setSelectedIndex(1);
        mLocationPicker.setTextSize(13);
        mLocationPicker.setTitleText("Locations Near You");
        mLocationPicker.setTitleTextSize(15);
        mLocationPicker.setCancelTextColor(ContextCompat.getColor(this, R.color.red_900));
        mLocationPicker.setSubmitTextColor(ContextCompat.getColor(this, R.color.dark_purple));
        mLocationPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int position, String option) {
                Log.i(TAG,"option picked");
                mLocationView.setText(option);
                mLocationPicker.dismiss();
            }

        });
        mLocationPicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i(TAG,"option dismissed");
                mLocationPicker.dismiss();
            }
        });
    }

    private void setPhotoTakeLayoutListeners(){


        /*********** Setting up Location View UI *****************/

        Location currLocation = app.getLocation();
        if(currLocation != null) {
            Log.i(TAG,"setPhotoTaken currLocation not null");
            new getNearbyLocations().execute(currLocation.getLatitude(), currLocation.getLongitude());
        }
        if(mNearbyLocations == null || mNearbyLocations.isEmpty()) {
            mLocationPicker = new OptionPicker(this, nearbyFakeLocations);
        } else {
            mLocationPicker = new OptionPicker(this, mNearbyLocations);
        }

        setLocationPickerOptions();

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
                if(mNearbyLocations != null && !mNearbyLocations.isEmpty()) {
                    mLocationPicker = new OptionPicker(MainActivity.this, mNearbyLocations);
                    setLocationPickerOptions();
                } else {
                    mLocationPicker = new OptionPicker(MainActivity.this, nearbyFakeLocations);
                    setLocationPickerOptions();
                }
                Log.i(TAG,"location picker");
                mLocationPicker.show();
            }
        });
    }

    public void updateLocationPicker() {
        Location currLocation = app.getLocation();
        if(currLocation != null) {
            Log.i(TAG,"currLocation not null");
            new getNearbyLocations().execute(currLocation.getLatitude(), currLocation.getLongitude());
        }


    }

    /**
     * Displays picture user just took and other options
     * @param pictureFile
     */
    public void startPhotoTakenLayout(final File pictureFile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"in startPhotoTakenLayout with pictureFile:" +pictureFile);
                mPictureFile = pictureFile;
                mSaveSignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = null;
                        if(mEnterTextView.getVisibility() == View.VISIBLE){
                            message = mEnterTextView.getEditableText().toString();
                        }
                        String locationName = mPlacePickerButton.getText().toString();
                        String locationViewName = mLocationView.getText().toString();
                        if(!locationViewName.equals(locationName)){
                            locationName = mLocationView.getEditableText().toString();
                        }
                        fireBaseUtility.uploadImageToFireBase(pictureFile,locationName,message,(ArrayList<String>) mTagContainerLayout.getTags());
                        startMainActivityLayout(true);
                        signJustSaved = true;
                        resetPreviewData();
                    }
                });
                /////// Hides unnecessary UI elements during a taken preview ////////
                mBottomBar.setVisibility(View.INVISIBLE);
                mCaptureButton.setVisibility(View.INVISIBLE);
                mPlacePickerButton.setVisibility(View.INVISIBLE);

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

                //Only display capture button and place picker if on camera fragment
                if(mCurrentFragment.equals(CREATE_SIGN_FRAGMENT)) {
                    mCaptureButton.setVisibility(View.VISIBLE);
                    mPlacePickerButton.setVisibility(View.VISIBLE);
                }

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
        mLocationView.setText(null);
    }

    @Override
    public void addTag(String tag) {
        mTagContainerLayout.addTag(tag);
    }


    /****************** View Pager Helper Methods **********************/
    public void startFullScreenViewPager(){
        if(mBottomBar.getVisibility() == View.GONE) return;

        mBottomBar.setVisibility(View.GONE);

    }


    public void restoreMainFromFullScreenViewPager(){
        mBottomBar.setVisibility(View.VISIBLE);
    }


    /******** Broadcast Receiver in charge of notifying adapter when signs are downloaded *******/
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.LOADING_SIGNS.STOP_LOADING)){
                mLoadingView.setVisibility(View.GONE);
                Log.v(TAG,"Retrieved broadcast to stop loading screen");
            }
            if(intent.getAction().equals(Constants.LOADING_SIGNS.START_LOADING)){
                mFragmentContainer.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                Log.v(TAG,"Retrieved broadcast to start loading screen");
            }
        }
    };
    public void registerImageSignReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOADING_SIGNS.START_LOADING);
        intentFilter.addAction(Constants.LOADING_SIGNS.STOP_LOADING);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void hideLoadingView(){
        mLoadingView.setVisibility(View.GONE);
        signJustSaved = false;
    }
    public void showLoadingView(){
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideBottomBar(){
        mBottomBar.setVisibility(View.GONE);
    }

    public void showBottomBar(){
        mBottomBar.setVisibility(View.VISIBLE);
    }

    /******** Background Thread to retrieve nearby location names *******/

    class getNearbyLocations extends AsyncTask<Double,Integer,ArrayList<String>> {
        HttpURLConnection httpURLConnection = null;
        String stream = null;

        @Override
        protected ArrayList<String> doInBackground(Double... params) {
            ArrayList<String> nearbyLocations = new ArrayList<>();
            Double lat = params[0];
            Double lon = params[1];

            String nearbySearchRequestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+
                    lat+","+lon+"&radius=500&key=AIzaSyBs-rk7XwonUuPuy20g5LDXBOgrwQ6KV04";

            /*************** Connect to google api url ********************/
            try {
                httpURLConnection = (HttpURLConnection) new URL(nearbySearchRequestURL).openConnection();
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                /* putting resulting input stream in one string */
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                stream = sb.toString();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                Log.e(TAG,"malformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG,"IOException");
                e.printStackTrace();
            }

            /* Parsing JSON result*/
            if(stream != null) {
                try {
                    JSONObject reader = new JSONObject(stream);

                    JSONArray results = reader.getJSONArray("results");

                    for(int i = 0; i < results.length();i++) {
                        String place = results.getJSONObject(i).getString("name");
                        nearbyLocations.add(place);
                    }
                } catch (JSONException e) {
                    Log.e(TAG,"JSONException");
                    e.printStackTrace();
                }

            }

            return nearbyLocations;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            Log.i(TAG,"in onPostExecute with results:"+ strings+", length: "+strings.size());
            mNearbyLocations.clear();
            mNearbyLocations.addAll(strings);
            super.onPostExecute(strings);
        }
    }

    public FireBaseUtility getFireBaseUtility() {
        return fireBaseUtility;
    }
}
