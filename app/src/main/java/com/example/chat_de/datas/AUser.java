package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import java.io.Serializable;

public abstract class AUser implements Serializable {
    protected String name;
    protected String pictureURL;
    protected int generation;
    protected String userKey;

    public AUser() { }
    public AUser(String name, String pictureURL, int generation, String userKey) {
        setName(name);
        setPictureURL(pictureURL);
        setGeneration(generation);
        setUserKey(userKey);
    }
    public AUser(@NonNull AUser original) {
        setName(original.getName());
        setPictureURL(original.getPictureURL());
        setGeneration(original.getGeneration());
        setUserKey(original.getUserKey());
    }

    public String getName()         { return name; }
    public String getPictureURL()   { return pictureURL; }
    public int getGeneration()      { return generation; }
    public String getUserKey()      { return userKey; }
    public AUser userMeta()         { return this; }

    public void setName(String name)                { this.name = name; }
    public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
    public void setGeneration(int generation)       { this.generation = generation; }
    public void setUserKey(String userKey)          { this.userKey = userKey; }

    @Override abstract public String toString();
}