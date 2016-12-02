package com.snapsigns;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.snapsigns.utilities.FireBaseUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class SnapSigns extends android.app.Application implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<ImageSign> myImageSigns;
    private ArrayList<ImageSign> mNearbySigns;
    private ArrayList<ImageSign> filteredNearbySigns;
    private ArrayList<String> allTags;
    private ArrayList<String> filterTags;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    requestEmail()
                    .requestIdToken(getString(R.string.web_client_id))
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        mGoogleApiClient.connect();
        mAuth = FirebaseAuth.getInstance();

    }


    public ArrayList<ImageSign> getMyImageSigns() {
        return myImageSigns;
    }

    public ArrayList<ImageSign> getNearbySigns() {
        return mNearbySigns;
    }

    public ArrayList<ImageSign> getFilteredNearbySigns() {return filteredNearbySigns;}

    public ArrayList<String> getAllTags() {return allTags;}

    public ArrayList<String> getFilterTags() {return filterTags;}

    public void populateAllTags() {
        TreeSet<String> seenTags = new TreeSet<String>();

        for (ImageSign sign : mNearbySigns) {
            for (String tag : sign.tags) {
                seenTags.add(tag);
            }
        }

        allTags = new ArrayList<>(seenTags);
    }

    public ArrayList<ImageSign> filterNearbySigns(List<String> tags) {
        filterTags = new ArrayList<String>(tags);
        filteredNearbySigns.clear();

        for (ImageSign sign : mNearbySigns) {
            Set<String> toCheck = new HashSet<String>(tags);

            for (String tag : sign.tags) {
                if (toCheck.contains(tag)) {
                    toCheck.remove(tag);

                    if (toCheck.size() == 0) {
                        filteredNearbySigns.add(sign);
                        break;
                    }
                }
            }
        }

        return filteredNearbySigns;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public void signOut() {
        // FireBase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                }
        );
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        FireBaseUtility fireBaseUtility = new FireBaseUtility(this);
        myImageSigns = fireBaseUtility.getUserSigns();

        mNearbySigns = fireBaseUtility.getNearbySigns();

        filteredNearbySigns = new ArrayList<>(mNearbySigns);
        populateAllTags();

        filterTags = new ArrayList<>(allTags);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onTerminate() {
        signOut();
        super.onTerminate();
    }
}
