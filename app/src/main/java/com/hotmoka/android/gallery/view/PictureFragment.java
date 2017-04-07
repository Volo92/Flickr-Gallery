package com.hotmoka.android.gallery.view;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private final static String PICTURE_PATH = "picture_path";

    private final static int SHARE_REQUEST = 0;

    private int positionShown = -1;

    private MenuItem shareItem = null;

    /**
     * This constructor is called when creating the view for the
     * two panes layout and when recreating the fragment upon
     * configuration change. We ensure that args exist. If they
     * already existed, previous args will be kept by the OS.
     */
    @UiThread
    protected PictureFragment() {

        init(-1);
    }

    @UiThread
    protected void init(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        setArguments(args);

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
    }

    public void updateShareAndShow(MenuItem share){
        shareItem = share;

        shareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Uri pictureUri = getCurrentPictureUri();

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
                startActivityForResult(Intent.createChooser(sharingIntent, "Share your picture with"), SHARE_REQUEST);

                getArguments().putString(PICTURE_PATH, pictureUri.toString());

                return true;
            }
        });

        showPictureOrDownloadIfMissing();
    }

    private Uri getCurrentPictureUri(){
        Bitmap pictureBitmap = MVC.model.getBitmapHigh(positionShown);

        String picturePath = MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(), pictureBitmap, "", "");
        Uri pictureUri = Uri.parse(picturePath);

        return pictureUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHARE_REQUEST) {
            Uri pictureUri = Uri.parse(getArguments().getString(PICTURE_PATH));
            getActivity().getApplicationContext().getContentResolver().delete(pictureUri, null,null);
        }
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
    protected void showPictureOrDownloadIfMissing() {

        if(shareItem==null) return;

        int position = getArguments().getInt(ARG_POSITION);
        String url;
        positionShown = position;

        if(showBitmapIfDownloaded(position)){
            shareItem.setVisible(true);
        }else{
            shareItem.setVisible(false);
            if((url = MVC.model.getUrlHigh(position)) != null){
                ((ImageView) getView().findViewById(R.id.picture)).setImageBitmap(null);
                ((GalleryActivity) getActivity()).showProgressIndicator();
                MVC.controller.onPictureRequired(getActivity(), url, true);
            }
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
        Bitmap bitmap = MVC.model.getBitmapHigh(position);
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
            case BITMAP_CHANGED_HIGH: {
                // A new bitmap arrived: update the picture in the view
                showPictureOrDownloadIfMissing();
                ((GalleryActivity) getActivity()).hideProgressIndicator();
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