package com.example.carinacunha.newsapp.News;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.carinacunha.newsapp.NewsUtils;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        List<News> News = null;
        try {
            News = NewsUtils.queryNews(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return News;
    }
}
