package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserMeta {
    private String name;
    private String pictureURL;
    private int generation;

    public UserMeta() { }
    public UserMeta(String name, String pictureURL, int generation) {
        setName(name);
        setPictureURL(pictureURL);
        setGeneration(generation);
    }

    public String getName()         { return name; }
    public String getPictureURL()   { return pictureURL; }
    public int getGeneration(){return generation;}

    public void setName(String name)                { this.name = name; }
    public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
    public void setGeneration(int generation)   {this.generation = generation;}
}
