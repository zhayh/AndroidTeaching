package edu.niit.android.photogallery.bing;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by zhayh on 2017-10-11.
 */

public class PhotoItem  implements Serializable{
    private String title;
    private String pic;
    private String day;
    private String content;
    private String subtitle;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public String toString() {
        return "title = " + title +
                ", pic = " + pic;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse(pic)
                .buildUpon()
                .build();
    }
}
