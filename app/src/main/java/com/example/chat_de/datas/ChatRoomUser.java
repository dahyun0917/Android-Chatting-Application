package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class ChatRoomUser extends User {
    private int lastReadIndex;
    private boolean exist;

    public ChatRoomUser() { }
    public ChatRoomUser(int lastReadIndex, boolean exist, User userMeta) {
        setLastReadIndex(lastReadIndex);
        setExist(exist);
        setUserMeta(userMeta);
    }
    public ChatRoomUser(User userMeta) {
        setLastReadIndex(-1);
        setExist(true);
        setUserMeta(userMeta);
    }
    //Copy constructor
    public ChatRoomUser(@NonNull ChatRoomUser original) {
        setLastReadIndex(original.getLastReadIndex());
        setExist(original.getExist());
        setUserMeta(original.userMeta());
    }

    public int getLastReadIndex()   { return lastReadIndex; }
    public boolean getExist()       { return exist; }

    public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
    public void setExist(boolean exist)             { this.exist = exist; }
    public void setUserMeta(@NonNull User userMeta) {
        name = userMeta.getName();
        pictureURL = userMeta.getPictureURL();
        generation = userMeta.getGeneration();
        userKey = userMeta.getUserKey();
    }

    @Override
    public String toString() {
        return  "{ lastReadIndex: " + lastReadIndex +
                ", exist: " + exist +
                ", name: " + name +
                ", pictureURL: " + pictureURL +
                ", generation: " + generation +
                ", userKey: " + userKey + " }";
    }
}