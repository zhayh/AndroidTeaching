package edu.niit.android.photogallery;

import java.io.Serializable;

/**
 * Created by zhayh on 2017-10-1.
 */

public class GalleryItem implements Serializable {
    private String title;
    private String id;
    private String url_s;

    public String getTitle() {
        return title;
    }

    public void setTitle(String caption) {
        title = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_s() {
        return url_s;
    }

    public void setUrl_s(String url) {
        url_s = url;
    }

    @Override
    public String toString() {
        return "title = " + title +
                ", id = " + id +
                ", url = " + url_s;
    }
}
