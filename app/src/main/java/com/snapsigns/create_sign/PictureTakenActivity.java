package com.snapsigns.create_sign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class PictureTakenActivity extends Activity {
    String pictureFile;
    Bitmap picture;
    ImageView pictureView;
    double longitude, latitude;
    Geocoder geocoder;
    List<Address> addressList;
    String loc, country;
    TextView location;
    Button exit, confirm;
    GoogleApiClient mGoogleApiClient;

    public void onCreate(Bundle savedInstanceState) {
        // set up layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_taken);
        pictureView = (ImageView) findViewById(R.id.pictureView);

        // get the picture which was passed by an intent
        // and set it as the background
        Intent intent = getIntent();
        pictureFile = intent.getStringExtra("FILE_PATH");
        picture = BitmapFactory.decodeFile(pictureFile);
        pictureView.setImageBitmap(picture);

        mGoogleApiClient = ((SnapSigns)getApplicationContext()).getmGoogleApiClient();

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);




        // create a gps tracker and get longitude/latitude
        longitude = mLastLocation.getLongitude();
        latitude = mLastLocation.getLatitude();

        // get location at coordinates
        if(Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                loc = addressList.get(0).getLocality();
                country = addressList.get(0).getCountryName();
            } catch (IOException e) {
                Log.d("ADDRESS ISSUE:", "gecoder failed to get from location");
                e.printStackTrace();
            }
        } else {
            loc = "Invalid";
            country = "Address!";
            Log.d("ADDRESS ISSUE: ", "invalid coordinates");
        }

        // Set text to display location
        location = (TextView) findViewById(R.id.locationText);
        String locText = loc + ", " + country;
        location.setText(locText);

        // Manage buttons
        exit = (Button) findViewById(R.id.exitButton);
        confirm = (Button) findViewById(R.id.confirmPicture);

        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishActivity(Activity.RESULT_CANCELED);
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishActivity(Activity.RESULT_OK);
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
