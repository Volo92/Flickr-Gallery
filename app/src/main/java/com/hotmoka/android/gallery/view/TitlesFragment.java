package com.hotmoka.android.gallery.view;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.PicturesList;
import com.hotmoka.android.gallery.PicturesListAdapter;
import com.hotmoka.android.gallery.R;
import com.hotmoka.android.gallery.model.Picture;
import com.hotmoka.android.gallery.model.Pictures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Delayed;

import static com.hotmoka.android.gallery.model.Pictures.Event.PICTURES_LIST_CHANGED;

/**
 * A fragment containing the titles of the Flickr Gallery app.
 * Titles can be clicked to show their corresponding picture.
 * Titles can be reloaded through a menu item.
 */
public abstract class TitlesFragment extends ListFragment
        implements GalleryFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Show the titles, or the empty list if there is none yet
        String[] titles = MVC.model.getTitles();
        if (titles != null)
        {
            ArrayList<Picture> pictures = new ArrayList<>();
            for(int i = 0; i < titles.length; i++) {
                String url = MVC.model.getUrl(i);
                String title = titles[i];
                Picture picture = new Picture(title, url);
                pictures.add(picture);
            }

            PicturesListAdapter adapter = new PicturesListAdapter(getActivity(), pictures);
            setListAdapter(adapter);
            showPictureOrDownloadIfMissing();
        }


        // If no titles exist yet, ask the controller to reload them
        if (titles == null) {
            //((GalleryActivity) getActivity()).showProgressIndicator();
            MVC.controller.onTitlesReloadRequest(getActivity());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment uses menus
        setHasOptionsMenu(true);
        boolean flag = true;
        if (flag){
            flag = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onActivityCreated(savedInstanceState);
                }
            }, 5000);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.fragment_titles, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_load) {
            ((GalleryActivity) getActivity()).showProgressIndicator();
            MVC.controller.onTitlesReloadRequest(getActivity());
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Delegate to the controller
        MVC.controller.onTitleSelected(position);
    }

    @Override @UiThread
    public void onModelChanged(Pictures.Event event) {
        if (event == PICTURES_LIST_CHANGED)
        {
            String[] titles = MVC.model.getTitles();
            ArrayList<Picture> pictures = new ArrayList<>();
            for(int i = 0; i < titles.length; i++) {
                String url = MVC.model.getUrl(i);
                String title = titles[i];
                Picture picture = new Picture(title, url);
                pictures.add(picture);
            }

            PicturesListAdapter adapter = new PicturesListAdapter(getActivity(), pictures);
            setListAdapter(adapter);
            showPictureOrDownloadIfMissing();
        }
            // Show the new list of titles
            //setListAdapter(new ArrayAdapter<>(getActivity(),
                    //android.R.layout.simple_list_item_activated_1,
                    //MVC.model.getTitles()));
    }

    @UiThread
    protected void showPictureOrDownloadIfMissing() {

        String url;
        int listSize = getListAdapter().getCount();

        for (int i = 0; i < listSize; i++)
        {
            Picture currentPicture = (Picture) getListAdapter().getItem(i);
            if (!showBitmapIfDownloaded(MVC.model.getBitmap(i)) && (url = MVC.model.getUrl(i)) != null) {
                MVC.controller.onPictureRequired(getActivity(), url);
            }
        }

    }

    @UiThread
    protected boolean showBitmapIfDownloaded(Bitmap bitmap) {
        if (bitmap != null) {
            return true;
        }
        else
            return false;
    }
}