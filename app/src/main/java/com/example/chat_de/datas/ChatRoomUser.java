package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomUser {
    private int lastReadIndex;
    private User userMeta;

    public ChatRoomUser() { }
    public ChatRoomUser(int lastReadIndex, User userMeta) {
        setLastReadIndex(lastReadIndex);
        setUserMeta(userMeta);
    }
    public ChatRoomUser(User userMeta) {
        setLastReadIndex(-1);
        setUserMeta(userMeta);
    }
    //Copy constructor
    public ChatRoomUser(@NonNull ChatRoomUser original) {
        setLastReadIndex(original.getLastReadIndex());
        setUserMeta(new User(original.getUserMeta()));
    }

    public int getLastReadIndex()   { return lastReadIndex; }
    public User getUserMeta()       { return userMeta; }
    public String takeName()        { return userMeta.getName(); }
    public String takePictureURL()  { return userMeta.getPictureURL(); }
    public int takeGeneration()     { return userMeta.getGeneration(); }
    public String takeUserKey()     { return userMeta.getUserKey(); }

    public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
    public void setUserMeta(User userMeta)          { this.userMeta = userMeta; }
    public void setName(String name)                { this.userMeta.setName(name); }
    public void setPictureURL(String pictureURL)    { this.userMeta.setPictureURL(pictureURL); }
    public void setGeneration(int generation)       { this.userMeta.setGeneration(generation); }
    public void setUserKey(String userKey)          { this.userMeta.setUserKey(userKey); }
}