package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserMeta {
    private String name;
    private String pictureURL;

    public UserMeta() { }
    public UserMeta(String name, String pictureURL) {
        setName(name);
        setPictureURL(pictureURL);
    }

    public String getName()         { return name; }
    public String getPictureURL()   { return pictureURL; }

    public void setName(String name)                { this.name = name; }
    public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
}