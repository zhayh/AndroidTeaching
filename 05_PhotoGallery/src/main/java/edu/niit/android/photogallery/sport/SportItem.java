package edu.niit.android.photogallery.sport;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by zhayh on 2017-10-11.
 */

public class SportItem implements Serializable{
    private String title;
    private String picUrl;
    private String description;
    private String ctime;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "title = " + title +
                ", picUrl = " + picUrl;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse(picUrl)
                .buildUpon()
                .build();
    }
}
