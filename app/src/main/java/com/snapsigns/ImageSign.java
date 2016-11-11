package com.snapsigns;

import android.location.Location;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.sql.Blob;

/**
 * Class in charge of storing information for a created sign.
 * Implementing serializable allows it to be passed with intents.
 */
public class ImageSign implements Serializable {
    //Location the image was taken
    Location location;
    //binary large object allows images to be stored in a relational database such as SQL.
    Uri image;
    String imageURL;

    public ImageSign(Location location, Uri image, String imageURL){
        this.location = location;
        this.image = image;
        this.imageURL = imageURL;
    }

    public Location getLocation() {
        return location;
    }

    public Uri getImage() {
        return image;
    }

    public String getImageURL() {
        return imageURL;
    }
}
