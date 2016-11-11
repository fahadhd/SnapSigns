package com.snapsigns;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.snapsigns.create_sign.CameraFragment;
import com.snapsigns.my_signs.MySignsFragment;

public class MainActivity extends AppCompatActivity {
    FragmentManager mFragmentManager;
    String mTag;
    FrameLayout mCameraFragmentContainer;
    FrameLayout mFragmentContainer;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Camera fragment is always active but its view hides if other tab is selected
        mCameraFragmentContainer = (FrameLayout)findViewById(R.id.camera_fragment_container);

        //Used to house other fragments other than camera fragment
        mFragmentContainer = (FrameLayout)findViewById(R.id.fragment_container);

        mFragmentManager = getSupportFragmentManager();
        setBottomBarListeners();

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



}
