package com.hotmoka.android.gallery.view.single;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;

/**
 * The picture fragment for a single pane layout.
 * It adds to ability to create the fragment programmatically
 * and redefines the behavior at picture show up by also
 * reporting the title of the picture below it.
 */
public class PictureFragment extends com.hotmoka.android.gallery.view.PictureFragment {

    public PictureFragment(){
    }

    /**
     * Convenience method to create a fragment that shows the picture
     * for the title corresponding to the given position.
     *
     * @param position
     * @return the fragment that has been created
     */
    @UiThread
    public static PictureFragment mkInstance(int position) {
        PictureFragment fragment = new PictureFragment();
        fragment.init(position);

        return fragment;
    }

    @Override @UiThread
    protected boolean showBitmapIfDownloaded(int position) {
        boolean shown = super.showBitmapIfDownloaded(position);
        // If the picture has been shown, report its title below it
        if (shown)
            ((TextView) getView().findViewById(R.id.picture_title))
                    .setText(MVC.model.getTitles()[position]);

        return shown;
    }
}
