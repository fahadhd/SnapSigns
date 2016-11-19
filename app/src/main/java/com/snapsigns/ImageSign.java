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
 */
@IgnoreExtraProperties
public class ImageSign {
    String userID;
    String imgURL;
    ArrayList<Double> location;
    ArrayList<String> tags;
    ArrayList<String> comments;

    public ImageSign(){

    }

    public ImageSign(String userID,String imgURL,ArrayList<Double> location){
        this.userID = userID;
        this.imgURL = imgURL;
        this.tags = new ArrayList<>();
        this.location = location;
        this.comments = new ArrayList<>();
    }

    public String getImgURL() {
        return imgURL;
    }
}
