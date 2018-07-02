package com.example.android.newsfeed;
import java.util.List;
import android.content.Context;
import android.content.AsyncTaskLoader;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    String url_;

    public NewsLoader(Context context, String url) {
        super(context);
        url_ = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {

        if (url_ == null) {
            return null;//if url is null, return
        }

        List<News> result = Utils.fetchData(url_);

        return result;
    }
}
