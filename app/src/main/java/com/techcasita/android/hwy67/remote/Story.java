package com.techcasita.android.hwy67.remote;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;

/**
 * Defining a Story class for this app ...
 */
@SuppressWarnings("ALL")
public class Story {
    private int sid;
    private String guid;
    private String author;
    private String date;
    private String title;
    private String content;
    private String category;
    private String image_url;
    private String url;
    private String foreign_url;


    public int getID() {
        return sid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getCategories() {
        return category;
    }

    public String getUrl() {
        return url;
    }

    public String getForeign_url() {
        return foreign_url;
    }

    public String getGuid() {
        return guid;
    }

    public void init(Context context) {
        Target<GlideDrawable> t = Glide.with(context)
                .load(image_url)
                .preload();
    }
}
