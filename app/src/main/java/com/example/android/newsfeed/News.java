package com.example.android.newsfeed;

public class News {
    private String mTitle;
    private String mName;
    private String mAuthor = "";
    private String mDate = "";
    private String mUrl;

    public News(String title, String name, String author, String date, String url){
        this.mTitle = title;
        this.mName = name;
        this.mAuthor = author;
        this.mDate = date;
        this.mUrl = url;
    }

    public String getTitle() { return mTitle; }

    public String getName() { return mName;}

    public String getAuthor() { return mAuthor;}

    public String getDate() { return mDate; }

    public String getUrl(){return mUrl;}
}