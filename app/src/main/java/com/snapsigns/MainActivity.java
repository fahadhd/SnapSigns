package com.snapsigns;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.snapsigns.create_sign.CameraFragment;
import com.snapsigns.my_signs.MySignsFragment;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    FragmentManager mFragmentManager;
    String mTag;
    FrameLayout mCameraFragmentContainer;
    FrameLayout mFragmentContainer;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Camera fragment is always active but its view hides if other tab is selected
        mCameraFragmentContainer = (FrameLayout)findViewById(R.id.camera_fragment_container);

        //Used to house other fragments other than camera fragment
        mFragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);

        mFragmentManager = getSupportFragmentManager();
        setBottomBarListeners();

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
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

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
                            mTag = "my_signs_fragment";
                            break;

                        case R.id.tab_favorites:
                            break;

                        case R.id.tab_create_sign:
                            mTag = "camera_fragment";
                            mFragmentContainer.setVisibility(View.GONE);
                            mCameraFragmentContainer.setVisibility(View.VISIBLE);
                            break;

                        case R.id.tab_nearby_signs:
                            break;

                        case R.id.tab_settings:
                            break;

                        //In case no tab is selected, use "my_signs" as default
                        default:
                            targetFragment = new MySignsFragment();
                            mTag = "my_signs_fragment";
                            break;
                    }

                    /**
                     * Populating container with selected fragment
                     */

                    if(targetFragment != null &&!mTag.equals("camera_fragment")) {

                        mCameraFragmentContainer.setVisibility(View.GONE);
                        mFragmentContainer.setVisibility(View.VISIBLE);
                        mFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container,targetFragment,mTag)
                                .commit();
                    }

                }
            });
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
