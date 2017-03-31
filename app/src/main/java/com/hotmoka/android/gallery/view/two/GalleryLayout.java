package com.hotmoka.android.gallery.view.two;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;
import com.hotmoka.android.gallery.model.Pictures;
import com.hotmoka.android.gallery.view.GalleryActivity;

public class GalleryLayout extends LinearLayout
        implements com.hotmoka.android.gallery.view.GalleryLayout {

    public void updateShareAndShow(MenuItem share){
        getPictureFragment().updateShareAndShow(share);
    }

    private TitlesFragment getTitlesFragment() {
        return (TitlesFragment) ((Activity) getContext())
                .getFragmentManager().findFragmentById(R.id.titles_fragment);
    }

    private PictureFragment getPictureFragment() {
        return (PictureFragment) ((Activity) getContext())
                .getFragmentManager().findFragmentById(R.id.picture_fragment);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MVC.registerView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        MVC.unregisterView(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void showPicture(int position) {
        getPictureFragment().showPicture(position);
    }

    @Override
    public void onModelChanged(Pictures.Event event) {
        // Delegate to both fragments inside this layout
        getTitlesFragment().onModelChanged(event);
        getPictureFragment().onModelChanged(event);
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
