package com.snapsigns.utilities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.snapsigns.SnapSigns;

import java.io.File;
import java.util.ArrayList;

/** Helper class which executes reading/writing to FireBase backend **/
public class FireBaseUtility {
    public static final String TAG = FireBaseUtility.class.getSimpleName();
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    ArrayList<ImageSign> myImageSigns;
    Context mContext;

    /**************** Action Intents for Fragment Broadcast Recivers ****************/
    Intent mySignsIntent = new Intent(Constants.MY_SIGNS.GET_MY_SIGNS);

    /*******************************************************************************/



    public FireBaseUtility(Context mContext) {
        initFireBase();
        this.mContext = mContext;
        this.myImageSigns = ((SnapSigns)mContext.getApplicationContext()).getMyImageSigns();
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

        //TODO: Replace placeholder username fha423
        final String userName = "fha423";
        String fileName = pictureFile.getName();

        String storagePath = "signs/"+userName+"/"+fileName;

        final StorageReference signsFolder = mStorageRef.child(storagePath);

        //Storing image into storage
        signsFolder.putFile(takenPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Once image is successfully in storage, add it to the database
                addFileToDatabase(userName,signsFolder.getPath());
            }
        });

    }

    /**
     * Adds uploaded image url and user info in database
     * @param userName
     * @param path
     */
    private void addFileToDatabase(final String userName, final String path){
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
                ImageSign imageSign = new ImageSign(userName, imgURL,getUserLocation(10));

                //Pushes a new imagesign object into database
                mDatabase.getRef().push().setValue(imageSign);

                //Storing new image into cache at front
                myImageSigns.add(0,imageSign);

                //Broadcasting result to MySignsFragment
                mContext.sendBroadcast(mySignsIntent);
                
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
                MainActivity.mGoogleApiClient);

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

    /**
     * Returns all signs created by the user
     * @return
     */
    public ArrayList<ImageSign> getUserSigns(){
        final String userName = "fha423";
        final ArrayList<ImageSign> myImageSigns = new ArrayList<>();
        if(mDatabase != null) {
            mDatabase.orderByChild("userID").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "in onDataChange");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        myImageSigns.add(0,child.getValue(ImageSign.class));
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

        //Broadcasting result to MySignsFragment
        mContext.sendBroadcast(mySignsIntent);

        return myImageSigns;
    }


}
