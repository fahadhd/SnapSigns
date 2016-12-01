package com.snapsigns.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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
import com.snapsigns.R;
import com.snapsigns.SnapSigns;


public class SignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignIn";
    public static final int RC_SIGN_IN = 11;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        SnapSigns app = (SnapSigns) getApplication();
        mAuth = app.getFirebaseAuth();
        mGoogleApiClient = app.getmGoogleApiClient();

        findViewById(R.id.google_sign_in_btn).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            }
        );
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
                        Toast.makeText(
                                SignInActivity.this,
                                "Welcome, " + user.getDisplayName(),
                                Toast.LENGTH_LONG
                        ).show();
                        finish();
                    } else {
                        Log.w(TAG, "Firebase auth failure", task.getException());
                        Toast.makeText(
                                SignInActivity.this,
                                "Firebase authentication failed.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        );
    }

    public void signIn() {
        startActivityForResult(
            Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient),
            RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                Log.d(TAG, "Sign in succeeded.");
                firebaseAuthWithGoogle(result.getSignInAccount());
            } else {
                Log.w(TAG, "Failed to sign in");
                Toast.makeText(this, "Failed to sign in.", Toast.LENGTH_LONG).show();
            }
        }
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
    public void onBackPressed() {
        if (user != null) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_LONG).show();
    }
}
