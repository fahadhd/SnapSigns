package com.snapsigns;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.snapsigns.utilities.FireBaseUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class SnapSigns extends android.app.Application implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    public Location mLocation;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private List<ImageSign> myImageSigns;
    private List<ImageSign> mNearbySigns;
    /********** Used to check values faster for cached items****************/
    private HashMap<String,ImageSign> mNearbySignsMap;
    /*************************/
    private List<String> allTags;
    private List<String> filterTags;
    private FirebaseAuth mAuth;
    private boolean useFilter = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mNearbySigns = new ArrayList<>();
        myImageSigns = new ArrayList<>();
        filterTags = new ArrayList<>();
        allTags = new ArrayList<>();

        mNearbySignsMap  = new HashMap<>();

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


    public List<ImageSign> getMyImageSigns() {
        return myImageSigns;
    }


    public List<ImageSign> getNearbySigns() {
        return mNearbySigns;
    }

    public HashMap<String,ImageSign> getNearbySignsMap() {
        return mNearbySignsMap;
    }

    public List<String> getAllTags() {return allTags;}

    public List<String> getFilterTags() {return filterTags;}

    public void populateAllTags() {
        Set<String> seenTags = new TreeSet<String>();

        for (ImageSign sign : mNearbySigns) {
            if (sign.tags != null) {
                for (String tag : sign.tags) {
                    seenTags.add(tag);
                }
            }
        }

        allTags = new ArrayList<>(seenTags);
    }

    public List<ImageSign> getFilteredNearbySigns() {
        if(mNearbySigns == null)
            mNearbySigns = new ArrayList<>();

        if (!useFilter)
            return new ArrayList<>(mNearbySigns);

        List<ImageSign> filteredNearbySigns = new ArrayList<>();

        for (ImageSign sign : mNearbySigns) {
            Set<String> toCheck = new HashSet<String>(filterTags);

            if (sign.tags != null) {
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
        }

        return filteredNearbySigns;
    }

    public void setUseFilter(boolean use) {
        useFilter = use;

        if (!useFilter)
            filterTags.clear();
    }
    public void setFilterTags(List<String> tags) {
        filterTags = tags;
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
        //Retrieve user signs at start up, ie when this list is empty
        if(myImageSigns.isEmpty()) fireBaseUtility.getUserSigns();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        // Request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void onLocationChanged(Location location) {
        mLocation = location;
        FireBaseUtility fireBaseUtility = new FireBaseUtility(this);
        fireBaseUtility.checkNearbySigns(location);

    }

    public void removeLocationUpdates(){
        if(mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public Location getLocation() {
        return mLocation;
    }
}
