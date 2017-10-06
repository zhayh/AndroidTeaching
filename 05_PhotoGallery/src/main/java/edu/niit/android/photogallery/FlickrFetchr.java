package edu.niit.android.photogallery;

import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zhayh on 2017-10-1.
 */

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "b1d2bfb0af38d54a2f93bf56f55ff5d6";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";

    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("method", "flickr.photos.getRecent")
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    private List<GalleryItem> items = new ArrayList<>();

    public byte[] getUrlBytes(String urlSpect) throws IOException {
        URL url = new URL(urlSpect);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpect);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotots() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    private List<GalleryItem> downloadGalleryItems(String url) {
        try {
            String jsonStr = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonStr);

            JSONObject json = new JSONObject(jsonStr);
            parseItems(items, json);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to fetch items ", e);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to parse JSON ", e);
        }
        return items;
    }

    private String buildUrl(String method, String query) {
        Uri.Builder builder = ENDPOINT.buildUpon().appendQueryParameter("method", method);


        if(method.equals(SEARCH_METHOD)) {
            builder.appendQueryParameter("text", query);
        }
        return builder.build().toString();
    }

    private void parseItems(List<GalleryItem> items, JSONObject json) throws JSONException {
        JSONObject photosJson = json.getJSONObject("photos");
        JSONArray photosArray = photosJson.getJSONArray("photo");

        for(int i = 0; i < photosArray.length(); i++) {
            JSONObject photoJson = photosArray.getJSONObject(i);

            GalleryItem item =new GalleryItem();
            item.setId(photoJson.getString("id"));
            item.setTitle(photoJson.getString("title"));
            if(!photoJson.has("url_s")) {
                continue;
            }
            item.setUrl_s(photoJson.getString("url_s"));
            item.setOwner(photoJson.getString("owner"));
            items.add(item);
        }

        // Gson获取对象
        GalleryItems galleyItems = new Gson().fromJson(photosJson.toString(), GalleryItems.class);
        Log.i(TAG, "Gson解析对象：" + galleyItems);

        List<GalleryItem> photoItems = new Gson().fromJson(photosArray.toString(),
                new TypeToken<List<GalleryItem>>(){}.getType());
        Log.i(TAG, "Gson解析数组：" + photoItems);

        // Fastjson
        galleyItems = JSON.parseObject(photosJson.toString(), GalleryItems.class);
        Log.i(TAG, "Fastjson解析对象：" + galleyItems);

        photoItems = JSON.parseArray(photosArray.toString(), GalleryItem.class);
        Log.i(TAG, "Fastjson解析数组：" + photoItems);
    }
}
