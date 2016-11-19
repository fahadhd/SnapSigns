package com.snapsigns.create_sign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.snapsigns.MainActivity;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class PictureTakenActivity extends Activity {
    public static final String TAG = PictureTakenActivity.class.getSimpleName();
    public static final String PICTURE_KEY = "PICTURE_FILE";
    File pictureFile;
    ImageView pictureView;
    TextView mLocationView;
    Button exit, confirm;
    GoogleApiClient mGoogleApiClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_taken);
        pictureView = (ImageView) findViewById(R.id.pictureView);
        mLocationView = (TextView) findViewById(R.id.locationText);

        displayPicture();
        displayUserLocation();

        /*** Setting up button listeners ***/

        exit = (Button) findViewById(R.id.exitButton);
        confirm = (Button) findViewById(R.id.confirmPicture);

        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(Activity.RESULT_OK,new Intent().putExtra(PICTURE_KEY,pictureFile));
                finish();
            }
        });
        /*************************************/
    }

    public void displayPicture(){
        //Display taken picture as background
        Intent previewIntent = getIntent();

        if(previewIntent != null && previewIntent.hasExtra(PICTURE_KEY)){
            pictureFile = (File) previewIntent.getSerializableExtra(PICTURE_KEY);
            Log.v(TAG,PICTURE_KEY+pictureFile.getAbsolutePath());
            Picasso.with(this).load(pictureFile).into(pictureView);
        }
        else{
            Log.v(TAG,PICTURE_KEY+" was null");
        }

    }

    public void displayUserLocation(){
        List<Address> addressList;
        Geocoder geocoder;
        double longitude, latitude;
        String locationName = "N/A", country = "N/A";

        mGoogleApiClient = ((SnapSigns)getApplicationContext()).getmGoogleApiClient();

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //TODO: Handle case where user location is null
        if(mLastLocation == null) return;

        // create a gps tracker and get longitude/latitude
        longitude = mLastLocation.getLongitude();
        latitude = mLastLocation.getLatitude();

        // get location at coordinates
        if(Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                locationName = addressList.get(0).getLocality();
                country = addressList.get(0).getCountryName();
            } catch (IOException e) {
                Log.d("ADDRESS ISSUE:", "gecoder failed to get from location");
                e.printStackTrace();
            }
        } else {
            locationName = "Invalid";
            country = "Address!";
            Log.d("ADDRESS ISSUE: ", "invalid coordinates");
        }

        // Set text to display location

        String locText = locationName + ", " + country;
        mLocationView.setText(locText);
    }

}
