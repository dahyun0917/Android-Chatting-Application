package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String name;
    private String pictureURL;
    private int generation;
    private String userKey;

    public User() { }
    public User(String name, String pictureURL, int generation, String userKey) {
        setName(name);
        setPictureURL(pictureURL);
        setGeneration(generation);
        setUserKey(userKey);
    }
    //Copy constructor
    public User(User original) {
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
