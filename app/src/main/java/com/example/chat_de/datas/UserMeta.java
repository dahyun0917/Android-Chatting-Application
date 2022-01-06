package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserMeta {
    private String name;
    private String pictureURL;
    private int generation;
    private String userKey;

    public UserMeta() { }
    public UserMeta(String name, String pictureURL, int generation) {
        setName(name);
        setPictureURL(pictureURL);
        setGeneration(generation);
        setUserKey(null);
    }
    //Copy constructor
    public UserMeta(UserMeta original) {
        setName(original.getName());
        setPictureURL(original.getPictureURL());
        setGeneration(original.getGeneration());
        setUserKey(original.getUserKey());
    }

    public String getName()         { return name; }
    public String getPictureURL()   { return pictureURL; }
    public int getGeneration()      { return generation; }
    public String getUserKey()      { return userKey; }

    public void setName(String name)                { this.name = name; }
    public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
    public void setGeneration(int generation)       { this.generation = generation; }
    public void setUserKey(String userKey)          { this.userKey = userKey; }
}
