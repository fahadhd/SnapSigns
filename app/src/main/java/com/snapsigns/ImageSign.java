package com.snapsigns;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public List<Double> location;
    public List<String> tags;
    public List<String> comments;
    public List<String> favoritedBy;

    public ImageSign(){

    }

    public ImageSign(String key,String userID,String imgURL,String message, String locationName, List<Double> location, List<String> tags){
        this.key = key;
        this.userID = userID;
        this.imgURL = imgURL;
        this.message = message;
        this.locationName = locationName;
        this.location = location;
        this.tags = tags;
        this.comments = new ArrayList<>();
        this.favoritedBy = new ArrayList<>();
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
        result.put("favoritedBy", favoritedBy);

        return result;
    }
}
