package it.univr.android.gallery.view;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.univr.android.gallery.R;
import it.univr.android.gallery.controller.Controller;
import it.univr.android.gallery.model.Pictures;

public abstract class PictureFragment extends Fragment implements GalleryFragment {
    private final static String ARG_POSITION = "position";

    /**
     * This constructor is called when creating the view for the
     * two pane layout and when recreating the fragment upon
     * configuration change. We ensure that args exist. If they
     * already existed, previous args will be kept by the OS.
     */
    protected PictureFragment() {
        Log.d(PictureFragment.class.getName(), "Created");
        init(-1);
    }

    protected void init(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picture_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        reflectModel();
    }

    public void updatePicture(int position) {
        getArguments().putInt(ARG_POSITION, position);
        reflectModel();
    }

    private void reflectModel() {
        Bundle args = getArguments();
        int position = args.getInt(ARG_POSITION);
        if (position >= 0 && !reflectPosition(position)) {
            ((GalleryActivity) getActivity()).showProgressIndicator();
            Controller.fetchPicture(getActivity(), position);
        }
    }

    protected boolean reflectPosition(int position) {
        Bitmap bitmap = Pictures.get().getBitmap(position);
        if (bitmap != null) {
            ((ImageView) getView().findViewById(R.id.picture)).setImageBitmap(bitmap);
            return true;
        }
        else
            return false;
    }

    public void onModelChanged(Pictures.Event event) {
        switch (event) {
            case BITMAP_CHANGED: {
                reflectModel();
                break;
            }
            case PICTURES_LIST_CHANGED:
                ((ImageView) getView().findViewById(R.id.picture)).setImageBitmap(null);
                getArguments().putInt(ARG_POSITION, -1);
                break;
        }
    }
}