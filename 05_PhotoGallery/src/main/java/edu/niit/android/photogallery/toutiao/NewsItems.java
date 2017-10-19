package edu.niit.android.photogallery.toutiao;

import java.util.List;

/**
 * Created by zhayh on 2017-10-4.
 */

public class NewsItems {
    private int ret_code;
    private List<NewsItem> list;

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public List<NewsItem> getList() {
        return list;
    }

    public void setList(List<NewsItem> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return ", ret_code = " + ret_code +
                ", photo = " + list;
    }
}
