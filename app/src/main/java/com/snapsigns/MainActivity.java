package com.snapsigns;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.snapsigns.create_sign.CameraFragment;
import com.snapsigns.create_sign.PictureTakenActivity;
import com.snapsigns.my_signs.MySignsFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    FragmentManager mFragmentManager;
    String mCurrentFragment;
    FrameLayout mCameraFragmentContainer;
    BottomBar bottomBar;
    ImageButton captureButton;
    FrameLayout mFragmentContainer;
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

        captureButton = (ImageButton) findViewById(R.id.button_capture);
        mGoogleApiClient = ((SnapSigns)getApplicationContext()).getmGoogleApiClient();

        //Camera fragment is always active but its view hides if other tab is selected
        mCameraFragmentContainer = (FrameLayout)findViewById(R.id.camera_fragment_container);

        //Used to house other fragments other than camera fragment
        mFragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);

        mFragmentManager = getSupportFragmentManager();
        setBottomBarListeners();

        signIn = ((SnapSigns) getApplicationContext()).getSignIn();
        signIn.setActivity(this);

        if (signIn.getCurrentUser() == null) {
            signIn.signIn();
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

    /**
     * Initialize bottom-bar tab listeners for fragments
     */
    public void setBottomBarListeners(){
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        if(bottomBar != null) {

            //Defaults tab position to be "create sign" and starts camera fragment
            bottomBar.selectTabAtPosition(2);

            mFragmentManager.beginTransaction()
                    .replace(R.id.camera_fragment_container,new CameraFragment(),"camera_fragment")
                    .commit();


            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
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
                            captureButton.setVisibility(View.VISIBLE);
                            mCameraFragmentContainer.setVisibility(View.VISIBLE);
                            break;

                        case R.id.tab_nearby_signs:
                            break;

                        case R.id.tab_settings:
                            break;

                        //In case no tab is selected, use "my_signs" as default
                        default:
                            targetFragment = new MySignsFragment();
                            mCurrentFragment = MY_SIGNS_FRAGMENT;
                            break;
                    }
                    if(!mCurrentFragment.equals(CREATE_SIGN_FRAGMENT))
                        displayFragment(targetFragment);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        if(!mCurrentFragment.equals(CREATE_SIGN_FRAGMENT)) {
            bottomBar.selectTabAtPosition(0);
        }

        super.onResume();
    }

    //Displays selected fragment overlaid on top of camera fragment for efficiency
    public void displayFragment(BaseFragment targetFragment){
        if(targetFragment != null) {
            captureButton.setVisibility(View.GONE);
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

            case PICTURE_TAKEN:
                if(resultCode == RESULT_OK) {
                    Log.d("PICTURE TAKEN: ", "onActivityResult was properly reached");
                    fireBaseUtility = new FireBaseUtility(this);
                    File pictureFile = (File) data.getSerializableExtra(PictureTakenActivity.PICTURE_KEY);
                    fireBaseUtility.uploadImageToFireBase(pictureFile);
                    mCurrentFragment = MY_SIGNS_FRAGMENT;
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

}
