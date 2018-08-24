package com.example.carinacunha.newsapp.News;

import android.graphics.drawable.Drawable;

public class News {

    private final String newsTitle;
    private final String newsSection;
    private final String newsAuthor;
    private final String newsDate;
    private final String newsUrl;
    private Drawable newsImage;

    /**
     * @param newsTitle   is for the news title
     * @param newsSection is for the news section
     * @param newsAuthor  is for the news Author
     * @param newsDate    is for the news publishing date
     * @param newsUrl     is for the news url
     * @param newsImage   is for the news associated image
     */

    public News(String newsTitle, String newsSection, String newsAuthor, String newsDate, String newsUrl, Drawable newsImage) {
        this.newsTitle = newsTitle;
        this.newsSection = newsSection;
        this.newsAuthor = newsAuthor;
        this.newsDate = newsDate;
        this.newsUrl = newsUrl;
        this.newsImage = newsImage;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsSection() {
        return newsSection;
    }

    public String getNewsAuthor() {
        return newsAuthor;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public Drawable getNewsImage() {
        return newsImage;
    }

}