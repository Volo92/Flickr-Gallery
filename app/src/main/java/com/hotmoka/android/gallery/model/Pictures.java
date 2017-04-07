package com.hotmoka.android.gallery.model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.hotmoka.android.gallery.MVC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The model of the application. It stores information about the titles
 * and the url of the bitmaps corresponding to each title.
 */
public class Pictures {

    /**
     * The titles of the pictures.
     */
    private String[] titles;

    /**
     * The url from where their bitmaps can be download.
     */
    private String[] urls;
    private String[] urlsHigh;

    /**
     * A map from each url to the downloaded bitmap.
     * It maps to null if the bitmap for a url has not been downloaded yet.
     */
    private final Map<String, Bitmap> bitmaps = new HashMap<>();

    /**
     * Yields the titles of the pictures, if any.
     *
     * @return the titles. Yields {@code null} if no titles have been stored yet
     */
    @UiThread
    public synchronized String[] getTitles() {
        return titles;
    }

    /**
     * Yields the bitmap corresponding to the title at the given position, if any.
     *
     * @param position the position
     * @return the bitmap. Yields {@code null} if the bitmap has not been stored yet
     *         or if the position is illegal
     */
    @UiThread
    public synchronized Bitmap getBitmap(int position) {
        if (urls == null || position < 0 || position >= urls.length)
            return null;
        else
            return bitmaps.get(urls[position]);
    }

    @UiThread
    public synchronized Bitmap getBitmapHigh(int position) {
        if (urlsHigh == null || position < 0 || position >= urlsHigh.length)
            return null;
        else
            return bitmaps.get(urlsHigh[position]);
    }

    /**
     * Yields the url from where it is possible to download the bitmap
     * corresponding to the title at the given position, if any.
     *
     * @param position the position
     * @return the url. Yields {@code null} if the url has not been stored yet or
     *         if the position is illegal
     */
    @UiThread
    public synchronized String getUrl(int position) {
        return urls != null && position >= 0 && position < urls.length ? urls[position] : null;
    }

    @UiThread
    public synchronized String getUrlHigh(int position) {
        return urlsHigh != null && position >= 0 && position < urlsHigh.length ? urlsHigh[position] : null;
    }

    /**
     * The kind of events that can be notified to a view.
     */
    public enum Event {
        PICTURES_LIST_CHANGED,
        BITMAP_CHANGED,
        BITMAP_CHANGED_HIGH
    }

    /**
     * Sets the pictures of this model. They are an enumeration of pairs
     * containing title and url of each picture.
     *
     * @param pictures the pictures
     */
    @WorkerThread @UiThread
    public void setPictures(Iterable<Picture> pictures) {
        List<String> titles = new ArrayList<>();
        List<String> urls = new ArrayList<>();

        for (Picture picture: pictures) {
            titles.add(picture.title);
            urls.add(picture.url);
        }

        String[] titlesAsArray = titles.toArray(new String[titles.size()]);
        String[] urlsAsArray = urls.toArray(new String[urls.size()]);

        // Synchronize for the shortest possible time
        synchronized (this) {
            this.titles = titlesAsArray;
            this.urls = urlsAsArray;
            this.bitmaps.clear();
        }

        // Tell all registered views that the list of pictures has changed
        notifyViews(Event.PICTURES_LIST_CHANGED);
    }

    @WorkerThread @UiThread
    public void setPicturesHigh(Iterable<Picture> pictures) {
        List<String> urls = new ArrayList<>();

        for (Picture picture: pictures) {
            urls.add(picture.url);
        }

        String[] urlsAsArray = urls.toArray(new String[urls.size()]);

        // Synchronize for the shortest possible time
        synchronized (this) {
            this.urlsHigh = urlsAsArray;
            this.bitmaps.clear();
        }

        // Tell all registered views that the list of pictures has changed
        notifyViews(Event.PICTURES_LIST_CHANGED);
    }

    /**
     * Sets the bitmap corresponding to the given url.
     *
     * @param url the url
     * @param bitmap the bitmap
     */
    @WorkerThread @UiThread
    public void setBitmap(String url, Bitmap bitmap) {
        synchronized (this) {
            this.bitmaps.put(url, bitmap);
        }

        // Tell all registered views that a bitmap changed
        if(url.contains("_z.")){
            notifyViews(Event.BITMAP_CHANGED_HIGH);
        }else{
            notifyViews(Event.BITMAP_CHANGED);
        }
    }

    /**
     * Notifies all views about an event.
     *
     * @param event the event
     */
    private void notifyViews(Event event) {
        // Notify the views. This must be done in the UI thread,
        // since views might have to redraw themselves
        new Handler(Looper.getMainLooper()).post
                (() -> MVC.forEachView(view -> view.onModelChanged(event)));
    }
}