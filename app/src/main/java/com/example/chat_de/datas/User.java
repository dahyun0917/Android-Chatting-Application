package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

@IgnoreExtraProperties
public class User implements Serializable {
    protected String name;
    protected String pictureURL;
    protected int generation;
    protected String userKey;

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
    public User userMeta()          { return this; }

    public void setName(String name)                { this.name = name; }
    public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
    public void setGeneration(int generation)       { this.generation = generation; }
    public void setUserKey(String userKey)          { this.userKey = userKey; }

    @Override
    public String toString() {
        return  "{ name: " + name +
                ", pictureURL: " + pictureURL +
                ", generation: " + generation +
                ", userKey: " + userKey + " }";
    }
}
