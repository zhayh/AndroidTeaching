package edu.niit.android.photogallery.toutiao;

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

/**
 * Created by zhayh on 2017-10-1.
 */

public class ShowApiFetch {
    private static final String TAG = "ShowApiFetch";
    private static final String API_ID  = "47511";
    private static final String API_KEY  = "1fc7af0d963a486d93a6eb1b93afcd75";
    private static final String FETCH_RECENTS_METHOD = "";
    private static final String SEARCH_METHOD = "";

    private static final Uri ENDPOINT = Uri.parse("http://route.showapi.com/1443-1")
            .buildUpon()
            .appendQueryParameter("showapi_appid", API_ID)
            .appendQueryParameter("showapi_sign", API_KEY)
            .build();

    private List<NewsItem> items = new ArrayList<>();

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

    public List<NewsItem> fetchRecentPhotots() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadPhotoItems(url);
    }

    public List<NewsItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadPhotoItems(url);
    }

    private List<NewsItem> downloadPhotoItems(String url) {
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

    private void parseItems(List<NewsItem> items, JSONObject json) throws JSONException {
        JSONObject photosJson = json.getJSONObject("showapi_res_body");
        JSONArray photosArray = photosJson.getJSONArray("list");

        for(int i = 0; i < photosArray.length(); i++) {
            JSONObject photoJson = photosArray.getJSONObject(i);

            NewsItem item =new NewsItem();
            item.setTitle(photoJson.getString("title"));
            item.setName(photoJson.getString("name"));
            if(!photoJson.has("image")) {
                continue;
            }
            item.setImage(photoJson.getString("image"));
            item.setUrl(photoJson.getString("url"));
            item.setDay(photoJson.getString("day"));
            items.add(item);
        }

        // Gson获取对象
        NewsItems newsItems = new Gson().fromJson(photosJson.toString(), NewsItems.class);
        Log.i(TAG, "Gson解析对象：" + newsItems);

        List<NewsItem> photoItems = new Gson().fromJson(photosArray.toString(),
                new TypeToken<List<NewsItem>>(){}.getType());
        Log.i(TAG, "Gson解析数组：" + photoItems);

        // Fastjson
        newsItems = JSON.parseObject(photosJson.toString(), NewsItems.class);
        Log.i(TAG, "Fastjson解析对象：" + newsItems);

        photoItems = JSON.parseArray(photosArray.toString(), NewsItem.class);
        Log.i(TAG, "Fastjson解析数组：" + photoItems);
    }
}
