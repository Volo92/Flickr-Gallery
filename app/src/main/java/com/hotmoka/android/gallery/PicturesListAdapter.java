package com.hotmoka.android.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotmoka.android.gallery.model.Picture;
import com.hotmoka.android.gallery.view.PictureFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by volo1 on 21/03/2017.
 */

public class PicturesListAdapter extends ArrayAdapter<Picture> {

    List<Picture> list;
    ImageView currentImage;

    public PicturesListAdapter(Context context, ArrayList<Picture> picturesList)
    {
        super(context,R.layout.titles_layout, R.id.title, picturesList);
        list = picturesList;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Picture picture = getItem(position);
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.titles_layout, parent, false);
        }
        currentImage = (ImageView) convertView.findViewById(R.id.image);
        TextView text = (TextView) convertView.findViewById(R.id.title);
        currentImage.setImageBitmap(MVC.model.getBitmap(position));
        text.setText(picture.title);

        return convertView;
    }

    public int getCount()
    {
        return list.size();
    }

    public Picture getItem(int position)
    {
        return list.get(position);
    }

}
