package com.snapsigns;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import static android.location.LocationManager.*;

/** Helper class which executes reading/writing to FireBase backend **/
public class FireBaseUtility {
    public static final String TAG = FireBaseUtility.class.getSimpleName();
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private MainActivity mActivity;

    public FireBaseUtility(){
        initFireBase();
    }
    public FireBaseUtility(MainActivity activity) {
        initFireBase();
        this.mActivity = activity;
    }


    private void initFireBase(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Getting instance of storage bucket for storing images
        this.mStorageRef = storage.getReferenceFromUrl("gs://snapsigns-c2dc1.appspot.com");
        //Getting instance of json database
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void uploadImageToFireBase(File pictureFile) {
        /** Uploading image to FireBase storage **/
        Uri takenPhoto = Uri.fromFile(pictureFile);
        //TODO: Replace placeholders fha423 and img1
        String storagePath = "signs/fha423/img1";

        StorageReference signsFolder = mStorageRef.child(storagePath);
        signsFolder.putFile(takenPhoto);

        /** Writing image to FireBase database **/
        mStorageRef.child(storagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            /**
             *@param uri - Returns the public download url of image that was just stored in bucket.
             */
            public void onSuccess(Uri uri) {
                String currentUser = "fha423";
                String imgURL = uri.toString();
                ImageSign imageSign = new ImageSign(currentUser, imgURL,getUserLocation(10));

                //Pushes a new imagesign object into database
                mDatabase.getRef().push().setValue(imageSign);
            }
        });
    }

    //Returns users location in list form [latitude,longitude] using google's api client.
    private ArrayList<Double> getUserLocation(int tries) {
        if(tries == 0){
            Log.v(TAG,"Request user location failed");
            return null;

        }
        ArrayList<Double> coordinates = new ArrayList<>();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mActivity.mGoogleApiClient);

        if (mLastLocation != null) {
            coordinates.add(mLastLocation.getLatitude());
            coordinates.add(mLastLocation.getLongitude());
            return coordinates;
        }
        else{
            //Attempt to read location again
            return getUserLocation(tries-1);
        }
    }

    public ArrayList<ImageSign> getUserSigns(){
        final ArrayList<ImageSign> myImageSigns = new ArrayList<>();
        if(mDatabase != null) {
            mDatabase.orderByChild("userID").equalTo("fha423").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "in onDataChange");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        myImageSigns.add(child.getValue(ImageSign.class));
                    }
                    Log.i(TAG, "notifying data changed");

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
        return myImageSigns;
    }


}
