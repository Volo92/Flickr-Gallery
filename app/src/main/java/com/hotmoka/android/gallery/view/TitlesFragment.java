package com.hotmoka.android.gallery.view;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;
import com.hotmoka.android.gallery.model.Pictures;

import java.util.ArrayList;

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
        setListAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_activated_1,
                titles == null ? new String[0] : titles));

        // If no titles exist yet, ask the controller to reload them
        if (titles == null) {
            ((GalleryActivity) getActivity()).showProgressIndicator();
            MVC.controller.onTitlesReloadRequest(getActivity());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment uses menus
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_load && MVC.controller.isIdle()) {
            ((GalleryActivity) getActivity()).showProgressIndicator();
            MVC.model.setPictures(new ArrayList<>());
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
        if (event == PICTURES_LIST_CHANGED){

            // Show the new list of titles
            setListAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    MVC.model.getTitles()));

        }

    }
}