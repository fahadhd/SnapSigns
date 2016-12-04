package com.snapsigns;

import android.location.Location;
import android.media.Image;
import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.sql.Array;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class in charge of storing information for a created sign.
 * Implementing serializable allows it to be passed with intents.
 *
 * Do NOT add getter methods to this class
 */

@IgnoreExtraProperties
public class ImageSign implements Serializable {
    public String key;
    public String userID;
    public String imgURL;
    public String message;
    public String locationName;
    public ArrayList<Double> location;
    public ArrayList<String> tags;
    public ArrayList<String> comments;

    public ImageSign(){

    }

    public ImageSign(String key,String userID,String imgURL,String message, String locationName, ArrayList<Double> location, ArrayList<String> tags){
        this.key = key;
        this.userID = userID;
        this.imgURL = imgURL;
        this.message = message;
        this.locationName = locationName;
        this.location = location;
        this.tags = tags;
        this.comments = new ArrayList<>();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userID);
        result.put("imgURL", imgURL);
        result.put("message", message);
        result.put("locationName", locationName);
        result.put("location", location);
        result.put("tags", tags);
        result.put("comments", comments);

        return result;
    }



}
