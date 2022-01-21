package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    protected String name;
    protected String pictureURL;
    protected int generation;
    protected String userKey;
    protected boolean admin;

    public User() { }
    public User(String name, String pictureURL, int generation, String userKey, boolean admin) {
        setName(name);
        setPictureURL(pictureURL);
        setGeneration(generation);
        setUserKey(userKey);
        setAdmin(admin);
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
    public boolean getAdmin()       { return admin; }
    public User userMeta()          { return this; }

    public void setName(String name)                { this.name = name; }
    public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
    public void setGeneration(int generation)       { this.generation = generation; }
    public void setUserKey(String userKey)          { this.userKey = userKey; }
    public void setAdmin(boolean admin)             { this.admin = admin; }

    @Override
    public String toString() {
        return  "{ name: " + name +
                ", pictureURL: " + pictureURL +
                ", generation: " + generation +
                ", userKey: " + userKey +
                ", admin: " + admin + " }";
    }
}
