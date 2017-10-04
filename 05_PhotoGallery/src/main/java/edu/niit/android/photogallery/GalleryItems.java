package edu.niit.android.photogallery;

import java.util.List;

/**
 * Created by zhayh on 2017-10-4.
 */

public class GalleryItems {
    private int page;
    private int pages;
    private int perpage;
    private int total;
    private List<GalleryItem> photo;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GalleryItem> getPhoto() {
        return photo;
    }

    public void setPhoto(List<GalleryItem> photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "page = " + page +
                ", pages = " + pages +
                ", pergage = " + perpage +
                ", total = " + total +
                ", photo = " + photo;
    }
}
