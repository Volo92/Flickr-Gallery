package com.hotmoka.android.gallery.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.hotmoka.android.gallery.MVC;
import com.hotmoka.android.gallery.R;

/**
 * An Android service that executes long-running background tasks
 * on a worker thread. At the end, it modifies the model accordingly.
 */
public class ControllerService extends IntentService {
    private final static String ACTION_FETCH_LIST_OF_PICTURES = "fetch list";
    private final static String PARAM_HOW_MANY = "how many";
    private final static String PARAM_API_KEY = "API key";
    private final static String ACTION_FETCH_BITMAP = "fetch bitmap";
    private final static String PARAM_URL = "url";
    private static final String ACTION_FETCH_BITMAP_HIGH = "fetch bitmap high quality";

    public ControllerService() {
        super("gallery controller");
    }

    /**
     * Fetches the latest titles from Flickr servers.
     *
     * @param context the context that needs the titles
     * @param howMany the number of titles to download
     */
    static void fetchListOfPictures(Context context, int howMany) {
        Intent intent = new Intent(context, ControllerService.class);
        intent.setAction(ACTION_FETCH_LIST_OF_PICTURES);
        intent.putExtra(PARAM_HOW_MANY, howMany);
        intent.putExtra(PARAM_API_KEY, context.getString(R.string.flick_key));
        context.startService(intent);
    }

    /**
     * Fetches the picture at the given address.
     *
     * @param context the context that needs the picture
     * @param url the address from where the picture can be downloaded
     */
    static void fetchPicture(Context context, String url, boolean highQuality) {
        Intent intent = new Intent(context, ControllerService.class);
        if (highQuality)
        {
            AsyncTask downloader = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    new BitmapFetcher(url, true);
                    return null;
                }
            };
            downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else
        {
            intent.setAction(ACTION_FETCH_BITMAP);
            intent.putExtra(PARAM_URL, url);
            context.startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        MVC.controller.resetTaskCounter();
        super.onDestroy();
    }

    @Override @WorkerThread
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_FETCH_LIST_OF_PICTURES:
                new ListOfPicturesFetcher(intent.getIntExtra(PARAM_HOW_MANY, 40),
                        intent.getStringExtra(PARAM_API_KEY));
                break;
            case ACTION_FETCH_BITMAP:
                new BitmapFetcher(intent.getStringExtra(PARAM_URL), false);
                break;
            case ACTION_FETCH_BITMAP_HIGH:
                new BitmapFetcher(intent.getStringExtra(PARAM_URL), true);
                break;
        }
    }
}
