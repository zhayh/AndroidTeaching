package edu.niit.android.photogallery.sport;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import edu.niit.android.photogallery.GalleryItem;
import edu.niit.android.photogallery.GalleryItems;

/**
 * Created by zhayh on 2017-10-1.
 */

public class ShowApiFetch {
    private static final String TAG = "ShowApiFetch";
    private static final String API_ID  = "47511";
    private static final String API_KEY  = "1fc7af0d963a486d93a6eb1b93afcd75";
    private static final String FETCH_RECENTS_METHOD = "";
    private static final String SEARCH_METHOD = "";

    private static final Uri ENDPOINT = Uri.parse("http://route.showapi.com/196-1")
            .buildUpon()
            .appendQueryParameter("showapi_appid", API_ID)
            .appendQueryParameter("showapi_sign", API_KEY)
            .appendQueryParameter("num", "100")
            .build();

    private List<SportItem> items = new ArrayList<>();

    public byte[] getUrlBytes(String urlSpect) throws IOException {
        URL url = new URL(urlSpect);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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

    public List<SportItem> fetchRecentPhotots() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadPhotoItems(url);
    }

    public List<SportItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadPhotoItems(url);
    }

    private List<SportItem> downloadPhotoItems(String url) {
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

    private void parseItems(List<SportItem> items, JSONObject json) throws JSONException {
        JSONObject photosJson = json.getJSONObject("showapi_res_body");
        JSONArray photosArray = photosJson.getJSONArray("newslist");

        for(int i = 0; i < photosArray.length(); i++) {
            JSONObject photoJson = photosArray.getJSONObject(i);

            SportItem item =new SportItem();
            item.setTitle(photoJson.getString("title"));
            if(!photoJson.has("picUrl")) {
                continue;
            }
            item.setPicUrl(photoJson.getString("picUrl"));
            item.setDescription(photoJson.getString("description"));
            item.setCtime(photoJson.getString("ctime"));
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
