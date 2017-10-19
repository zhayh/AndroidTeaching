package edu.niit.android.photogallery.toutiao;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by zhayh on 2017-10-11.
 */

public class NewsItem implements Serializable{
    /*
     {
        "title": "机器人写稿的技术原理及实现方法",
        "name": "传媒评论",
        "image": "//p1.pstatp.com/list/300x170/3173000c1ee2da9adcb4",
        "day": "2017-10-13",
        "aid": "6469638960749478414",
        "url": "http://www.toutiao.com/i6469638960749478414"
      }
     */
    private String title;
    private String name;
    private String image;
    private String day;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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
                ", image = " + image;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse(url)
                .buildUpon()
                .build();
    }
}
