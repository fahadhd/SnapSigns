package com.snapsigns;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignIn implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignIn";
    public static final int RC_SIGN_IN = 11;

    private Activity mActivity;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;

    public SignIn(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;

        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(
            new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();

                    if (user != null) {
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            }
        );
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public FirebaseUser getCurrentUser() {
        return user;
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        mAuth.signInWithCredential(
            GoogleAuthProvider.getCredential(
                acct.getIdToken(),
                null
            )
        ).addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        Log.d(TAG, "Firebase auth success");
                        Toast.makeText(mActivity, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Log.w(TAG, "Firebase auth failure", task.getException());
                        Toast.makeText(mActivity, "Firebase authentication failed.", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        );
    }

    public void signIn() {
        mActivity.startActivityForResult(
            Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient),
            RC_SIGN_IN
        );
    }

    public void signOut() {
        // Firebase sign out
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

    public void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                }
            }
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(mActivity, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
