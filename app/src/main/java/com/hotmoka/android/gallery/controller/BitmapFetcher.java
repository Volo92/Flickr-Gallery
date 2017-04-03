package com.hotmoka.android.gallery.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hotmoka.android.gallery.MVC;

/**
 * An object that fetches a given bitmap from Flickr's servers.
 */
class BitmapFetcher {
    private final static String TAG = BitmapFetcher.class.getSimpleName();

    @WorkerThread
    BitmapFetcher(String url, boolean highQuality) {
        Bitmap bitmap = null;

        if (highQuality)
        {
            url = url.substring(0, url.length() - 5);
            url += "z.jpg";
        }

        try {
            Log.d(TAG, "Loading image " + url);
            byte[] bitmapBytes = getUrlBytes(url);
            bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        }
        catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
        finally {
            MVC.controller.taskFinished();
        }

        if (bitmap != null)
            MVC.model.setBitmap(url, bitmap);
    }

    private byte[] getUrlBytes(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        ByteArrayOutputStream out = null;

        try {
            out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return new byte[0];

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
                out.write(buffer, 0, bytesRead);

            return out.toByteArray();
        }
        finally {
            if (out != null)
                out.close();

            connection.disconnect();
        }
    }
}
