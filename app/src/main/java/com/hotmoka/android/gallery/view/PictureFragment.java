package com.hotmoka.android.gallery.view;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;
import com.hotmoka.android.gallery.model.Pictures;

/**
 * A fragment containing the picture currently shown in the
 * Flickr Gallery app.
 */
public abstract class PictureFragment extends Fragment implements GalleryFragment {
    private final static String ARG_POSITION = "position";

    private int positionShown;

    /**
     * This constructor is called when creating the view for the
     * two panes layout and when recreating the fragment upon
     * configuration change. We ensure that args exist. If they
     * already existed, previous args will be kept by the OS.
     */
    @UiThread
    protected PictureFragment() {
        positionShown = -1;

        init(-1);
    }

    @UiThread
    protected void init(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        setArguments(args);

    }

    private void addShareButtonListener(){

        Button shareButton = (Button) getView().findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, getCurrentPictureUri());
                startActivity(Intent.createChooser(sharingIntent, "Share your picture with"));

            }
        });
    }

    private Uri getCurrentPictureUri(){

        Bitmap pictureBitmap = MVC.model.getBitmap(positionShown);

        String picturePath = MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(), pictureBitmap, "", "");
        Uri pictureUri = Uri.parse(picturePath);

        return pictureUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View created = inflater.inflate(R.layout.picture_view, container, false);
        return created;
    }

    @Override
    public void onStart() {
        super.onStart();
        addShareButtonListener();
        showPictureOrDownloadIfMissing();
    }

    /**
     * Shows the picture corresponding to the given position in the list.
     * If missing, it will start a background download task.
     *
     * @param position
     */
    @UiThread
    public void showPicture(int position) {
        getArguments().putInt(ARG_POSITION, position);
        showPictureOrDownloadIfMissing();
    }

    @UiThread
    private void showPictureOrDownloadIfMissing() {
        int position = getArguments().getInt(ARG_POSITION);
        String url;
        positionShown = position;

        Button shareButton = (Button) getView().findViewById(R.id.share_button);

        if (!showBitmapIfDownloaded(position) && (url = MVC.model.getUrl(position)) != null) {
            shareButton.setEnabled(false);
            ((GalleryActivity) getActivity()).showProgressIndicator();
            MVC.controller.onPictureRequired(getActivity(), url);
        }else{
            shareButton.setEnabled(true);
        }
    }

    /**
     * Shows the bitmap at the given position in the model, if it is in the model.
     *
     * @param position the position
     * @return true if the bitmap was shown, false otherwise, hence if the
     *         position is illegal or the model does not contain the bitmap yet
     */
    @UiThread
    protected boolean showBitmapIfDownloaded(int position) {
        Bitmap bitmap = MVC.model.getBitmap(position);
        if (bitmap != null) {
            ((ImageView) getView().findViewById(R.id.picture)).setImageBitmap(bitmap);
            return true;
        }
        else
            return false;
    }

    @UiThread
    public void onModelChanged(Pictures.Event event) {
        switch (event) {
            case BITMAP_CHANGED: {
                // A new bitmap arrived: update the picture in the view
                showPictureOrDownloadIfMissing();
                break;
            }
            case PICTURES_LIST_CHANGED:
                // Erase the picture shown in the view, since the list of pictures has changed
                ((ImageView) getView().findViewById(R.id.picture)).setImageBitmap(null);
                // Take note that no picture is currently selected
                getArguments().putInt(ARG_POSITION, -1);
                break;
        }
    }
}