package com.snapsigns;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.snapsigns.login.SignInActivity;
import com.snapsigns.utilities.FireBaseUtility;

import java.util.ArrayList;


public class SnapSigns extends android.app.Application implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    ArrayList<ImageSign> myImageSigns;
    ArrayList<ImageSign> mNearbySigns;
    FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FireBaseUtility fireBaseUtility = new FireBaseUtility(this);
        myImageSigns = fireBaseUtility.getUserSigns();
        //TODO: Change this to getNearbySigns
        mNearbySigns = fireBaseUtility.getUserSigns();


        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        mAuth = FirebaseAuth.getInstance();
    }


    public ArrayList<ImageSign> getMyImageSigns() {
        return myImageSigns;
    }

    public ArrayList<ImageSign> getNearbySigns() {
        return mNearbySigns;
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
