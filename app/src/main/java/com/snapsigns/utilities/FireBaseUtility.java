package com.snapsigns.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Helper class which executes reading/writing to FireBase backend **/
public class FireBaseUtility {
    public static final String TAG = FireBaseUtility.class.getSimpleName();
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private String uid;
    FirebaseAuth auth;
    List<ImageSign> mMyImageSigns, mNearbySigns;
    HashMap<String,ImageSign> mNearbySignsMap;

    SnapSigns appContext;
    Context mContext;

    /**************** Action Intents for Fragment Broadcast Recivers ****************/
    public  Intent mySignsIntent = new Intent(Constants.MY_SIGNS.GET_MY_SIGNS);
    public  Intent nearbySignsIntent = new Intent(Constants.NEARBY_SIGNS.GET_NEARBY_SIGNS);

    public  Intent startLoadingIntent = new Intent(Constants.LOADING_SIGNS.START_LOADING);
    /*******************************************************************************/



    public FireBaseUtility(Context mContext) {
        initFireBase();
        this.mContext = mContext;

        appContext = (SnapSigns) mContext.getApplicationContext();

        this.mGoogleApiClient = appContext.getmGoogleApiClient();

        auth = appContext.getFirebaseAuth();
        mNearbySigns = appContext.getNearbySigns();
        mMyImageSigns = appContext.getMyImageSigns();
        mNearbySignsMap = appContext.getNearbySignsMap();

        checkUserName();

    }

    private void initFireBase(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Getting instance of storage bucket for storing images
        this.mStorageRef = storage.getReferenceFromUrl("gs://snapsigns-c2dc1.appspot.com");
        //Getting instance of json database
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void uploadImageToFireBase(File pictureFile, final String locationName, final String message, final ArrayList<String> tags) {
        checkUserName();

        /** Uploading image to FireBase storage **/
        Uri takenPhoto = Uri.fromFile(pictureFile);

        String fileName = pictureFile.getName();

        String storagePath = "signs/" + uid + "/" + fileName;

        final StorageReference signsFolder = mStorageRef.child(storagePath);

        //Storing image into storage
        signsFolder.putFile(takenPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.v(TAG,signsFolder.getPath());
                addFileToDatabase(signsFolder.getPath(), locationName, message, tags);
            }
        });

    }

    /**
     * Adds uploaded image url and user info in database
     * @param path
     */
    private void addFileToDatabase(final String path, final String locationName, final String message, final ArrayList<String> tags){
        Log.v(TAG,path);

        /** Writing image to FireBase database **/
        mStorageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            /**
             *@param uri - Returns the public download url of image that was just stored in bucket.
             */
            public void onSuccess(Uri uri) {
                Log.v(TAG,"Image Successfully Uploaded");
                String imgURL = uri.toString();
                Location currentLocation = appContext.getLocation();
                if(currentLocation == null){
                    Toast.makeText(mContext,"Failed to get location",Toast.LENGTH_SHORT).show();;
                    return;
                }
                ArrayList<Double> locationCoords = new ArrayList<>();
                locationCoords.add(currentLocation.getLatitude());
                locationCoords.add(currentLocation.getLongitude());
                String finalName = locationName;
                if(finalName == null){
                    finalName = currentLocation.getLatitude()+","+currentLocation.getLongitude();
                }


                //Pushes a new imagesign object into database
                DatabaseReference pushedPostRef = mDatabase.getRef().push();
                ImageSign imageSign = new ImageSign(pushedPostRef.getKey(),uid, imgURL, message, finalName,locationCoords,tags);
                pushedPostRef.setValue(imageSign);


                //Storing new image into cache at front
                mMyImageSigns.add(0,imageSign);
                mNearbySigns.add(0,imageSign);
                mNearbySignsMap.put(imageSign.imgURL,imageSign);

                //Broadcasting result to MySignsFragment and NearbySignsFragment
                mContext.sendBroadcast(mySignsIntent);
                mContext.sendBroadcast(nearbySignsIntent);
            }
        });

    }


    /**
     * Returns all signs created by the user
     * @return
     */
    public void getUserSigns(){
        checkUserName();
        if(mDatabase != null) {
            mDatabase.orderByChild("userID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "in onDataChange");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ImageSign currentSign = child.getValue(ImageSign.class);
                        mMyImageSigns.add(0,currentSign);
                    }
                    Log.i(TAG, "notifying data changed");
                    //Broadcasting result to MySignsFragment
                    mContext.sendBroadcast(mySignsIntent);
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
    }

    public void deleteUserSigns(){
        checkUserName();

        final String userName = uid;
        if(mDatabase != null) {
            mDatabase.orderByChild("userID").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "in onDataChange");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        child.getRef().removeValue();
                    }
                    Log.i(TAG, "notifying data changed");
                    //Broadcasting result to MySignsFragment
                    mMyImageSigns.clear();
                    mContext.sendBroadcast(mySignsIntent);

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

    }

    //Returns users location in list form [latitude,longitude] using google's api client.
    private ArrayList<Double> getUserLocation(int tries) {
        if(tries == 0){
            Log.v(TAG,"Request user location failed");
            return null;

        }
        ArrayList<Double> coordinates = new ArrayList<>();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            coordinates.add(mLastLocation.getLatitude());
            coordinates.add(mLastLocation.getLongitude());
            return coordinates;
        }
        else {
            //Attempt to read location again
            return getUserLocation(tries - 1);
        }
    }

    public void checkUserName(){
        if (uid == null && auth != null) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                this.uid = user.getUid();
            }
        }
    }



    public void checkNearbySigns(final Location currentLocation){
        final int originalSize = mNearbySigns.size();
        if(currentLocation == null){
            Toast.makeText(mContext,"Unable to get location",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mDatabase != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            final int searchRadius = prefs.getInt(mContext.getString(R.string.searchRadiusKey),500);

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "in onDataChange");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Location signLocation = new Location("");
                        ImageSign currentSign = child.getValue(ImageSign.class);

                        if(mNearbySignsMap.get(currentSign.imgURL) != null){
                            //ImageSign is in cache, keep looking
                            continue;
                        }

                        if(currentSign.location != null) {
                            signLocation.setLatitude(currentSign.location.get(0));
                            signLocation.setLongitude(currentSign.location.get(1));

                            if(currentLocation.distanceTo(signLocation) < searchRadius){
                                mNearbySigns.add(0,currentSign);
                                mNearbySignsMap.put(currentSign.imgURL,currentSign);
                                Log.v(TAG,"added nearby sign: distance"+currentLocation.distanceTo(signLocation));
                            }
                        }
                    }
                    if(originalSize != mNearbySigns.size()) {
                        mContext.sendBroadcast(nearbySignsIntent);
                        Log.i(TAG, "notifying data changed");
                    }

                    appContext.populateAllTags();
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
    }

    public void updateImageSign(ImageSign currentSign){
        if(currentSign != null) mDatabase.getRef().child(currentSign.key).setValue(currentSign);
        else Toast.makeText(mContext,"Failed to update",Toast.LENGTH_SHORT).show();
    }
}