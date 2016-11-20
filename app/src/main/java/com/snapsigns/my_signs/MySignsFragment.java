package com.snapsigns.my_signs;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Creates a list view of past signs taken. When user clicks on an item in list view it
 * opens the activity SignDetail which displays a full screen view on the sign.
 */
public class MySignsFragment extends BaseFragment {
    GridView gridView;
    MySignsAdapter mAdapter;
    ArrayList<ImageSign> myImageSigns = new ArrayList<>();

    private final static String TAG = "My_Signs_Tag";
    public final static String IMAGE_URL_KEY = "img_url";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG,"in onCreateView of MySignsFragment");
        View rootView = inflater.inflate(R.layout.my_signs_grid_view, container, false);

        myImageSigns = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the todoItems child items it the database
        DatabaseReference myRef = database.getReferenceFromUrl("https://snapsigns-c2dc1.firebaseio.com/");




        if(myRef != null) {
            myRef.orderByChild("userID").equalTo("fha423").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "in onDataChange");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        myImageSigns.add(child.getValue(ImageSign.class));
                    }
                    Log.i(TAG, "notifying data changed");
                    mAdapter.notifyDataSetChanged();
                    // mySigns.clear();
                    //mySigns.addAll(tempSigns);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, "Failed to read value");
                }
            });
        }
        else{
            Log.i(TAG,"null ref");
        }

        mAdapter = new MySignsAdapter((MainActivity) getActivity(),myImageSigns);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mAdapter);


        /* TODO: When an image is selected it will open up a new view with just that image
         * with it's other data  */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FullImageFragment fullImageFragment = new FullImageFragment();
                ImageSign imageSign = (ImageSign) mAdapter.getItem(position);
                Bundle args = new Bundle();
                args.putString(IMAGE_URL_KEY,imageSign.getImgURL());
                fullImageFragment.setArguments(args);


                getFragmentManager().beginTransaction()
                        .add(fullImageFragment,"full_image_fragment").commit();

            }
        });

        //on configuration changes (screen rotation) we want fragment member variables to be preserved
        //setRetainInstance(true);

        return rootView;
    }
}
