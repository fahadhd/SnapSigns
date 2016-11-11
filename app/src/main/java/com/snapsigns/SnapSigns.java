package com.snapsigns;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SnapSigns extends android.app.Application {
    StorageReference mStorageRef;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Put your own bucket name here
        mStorageRef = storage.getReferenceFromUrl("gs://snapsigns-c2dc1.appspot.com");
    }

    public StorageReference getFirebaseStorageRef() {
        return mStorageRef;
    }
}
