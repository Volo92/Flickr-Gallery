package com.hotmoka.android.gallery.view.single;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;
import com.hotmoka.android.gallery.model.Pictures;
import com.hotmoka.android.gallery.view.GalleryActivity;
import com.hotmoka.android.gallery.view.GalleryFragment;

/**
 * The view for a single pane implementation of the app.
 */
public class GalleryLayout extends FrameLayout
        implements com.hotmoka.android.gallery.view.GalleryLayout {

    public void updateShareAndShow(MenuItem share){

    }

    private GalleryFragment getFragment() {
        return (GalleryFragment) getFragmentManager().findFragmentById(R.id.gallery_layout);
    }

    private FragmentManager getFragmentManager() {
        return ((Activity) getContext()).getFragmentManager();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MVC.registerView(this);

        // Show the titles fragment at start
        if (getFragment() == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.gallery_layout, new TitlesFragment()).commit();
    }

    @Override
    protected void onDetachedFromWindow() {
        MVC.unregisterView(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void showPicture(int position) {
        // Create fragment and give it an argument for the selected picture
        getFragmentManager().beginTransaction()
            // Replace whatever is in the gallery_layout_container view with this fragment
            .replace(R.id.gallery_layout, PictureFragment.mkInstance(position))
            // Add the transaction to the back stack so the user can navigate back
            .addToBackStack(null)
            // Commit the transaction
            .commit();
    }



    @Override
    public void onModelChanged(Pictures.Event event) {
        // Delegate to the only fragment inside this layout
        getFragment().onModelChanged(event);
        // If no background task is in progress, remove the progress indicator
        if (MVC.controller.isIdle())
            ((GalleryActivity) getContext()).hideProgressIndicator();
    }

    public GalleryLayout(Context context) {
        super(context);
    }
    public GalleryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}