package edu.niit.android.photogallery.sport;

import java.util.List;

/**
 * Created by zhayh on 2017-10-4.
 */

public class SportItems {
    public List<SportItem> getNewslist() {
        return newslist;
    }

    public void setNewslist(List<SportItem> newslist) {
        this.newslist = newslist;
    }

    private List<SportItem> newslist;



    @Override
    public String toString() {
        return "photo = " + newslist;
    }
}
