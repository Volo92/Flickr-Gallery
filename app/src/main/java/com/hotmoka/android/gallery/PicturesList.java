package com.hotmoka.android.gallery;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.widget.ImageView;

/**
 * Java Class that define the layout model for the new list with pictures
 */

public class PicturesList {

    public Bitmap picture;
    public String title;

    public PicturesList(Bitmap picture, String title)
    {
        this.picture = picture;
        this.title = title;
    }

}
