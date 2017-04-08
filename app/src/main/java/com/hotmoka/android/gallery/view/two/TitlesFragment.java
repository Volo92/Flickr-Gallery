package com.hotmoka.android.gallery.view.two;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;
import com.hotmoka.android.gallery.view.*;

import java.util.ArrayList;

/**
 * The titles fragment for a two panes layout. It modifies the standard
 * behavior by making the selected item remain highlighted.
 */
public class TitlesFragment extends com.hotmoka.android.gallery.view.TitlesFragment {

    private MenuItem shareItem = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Make the clicked item remain visually highlighted
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Keep the selected item checked also after click
        getListView().setItemChecked(position, true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_and_load_action_bar, menu);

        shareItem = menu.findItem(R.id.menu_share);

        MVC.controller.onMenuShareItemCreated(shareItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_load && MVC.controller.isIdle()) {
            shareItem.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }
}
