package com.snapsigns.my_signs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snapsigns.BaseFragment;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.snapsigns.utilities.Constants;

import java.util.ArrayList;

/**
 * Creates a list view of past signs taken. When user clicks on an item in list view it
 * opens the activity SignDetail which displays a full screen view on the sign.
 */
public class MySignsFragment extends BaseFragment {
    GridView gridView;
    MySignsAdapter mAdapter;

    private final static String TAG = MySignsFragment.class.getSimpleName();
    public final static String IMAGE_URL_KEY = "img_url";


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

        Log.i(TAG,"in onCreateView of MySignsFragment");
        View rootView = inflater.inflate(R.layout.my_signs_grid_view, container, false);
        mAdapter = new MySignsAdapter(getActivity());
        gridView = (GridView) rootView.findViewById(R.id.gridview);

        gridView.setAdapter(mAdapter);


        /* TODO: When an image is selected it will open up a new view with just that image
         * with it's other data  */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startActivity(new Intent(getActivity(),FullSignActivity.class).
                        putExtra(MySignsFragment.IMAGE_URL_KEY,(ImageSign)mAdapter.getItem(position)));

            }
        });

        //on configuration changes (screen rotation) we want fragment member variables to be preserved
        //setRetainInstance(true);

        return rootView;
    }

    /******** Broadcast Receiver in charge of notifying adapter when signs are downloaded *******/
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.MY_SIGNS.GET_MY_SIGNS)){
                Log.v(TAG,"Retrieved broadcast to update user signs");
                mAdapter.notifyDataSetChanged();
            }


        }
    };

    public void registerImageSignReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.MY_SIGNS.GET_MY_SIGNS);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

}
