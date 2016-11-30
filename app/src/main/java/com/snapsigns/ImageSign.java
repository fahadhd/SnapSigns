package com.snapsigns;

import android.location.Location;
import android.media.Image;
import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.sql.Array;
import java.sql.Blob;
import java.util.ArrayList;

/**
 * Class in charge of storing information for a created sign.
 * Implementing serializable allows it to be passed with intents.
 *
 * Do NOT add getter methods to this class
 */

@IgnoreExtraProperties
public class ImageSign {
    public String userID;
    public String imgURL;
    public String message;
    public ArrayList<Double> location;
    public ArrayList<String> tags;
    public ArrayList<String> comments;

    public ImageSign(){

    }

    public ImageSign(String userID,String imgURL,String message, ArrayList<Double> location){
        this.userID = userID;
        this.imgURL = imgURL;
        this.message = message;
        this.tags = new ArrayList<>();
        this.location = location;
        this.comments = new ArrayList<>();
    }



}
