package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class ChatRoomUser extends User {
    private int lastReadIndex;

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
        setUserMeta(original.userMeta());
    }

    public int getLastReadIndex()   { return lastReadIndex; }

    public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
    public void setUserMeta(User userMeta) {
        name = userMeta.getName();
        pictureURL = userMeta.getPictureURL();
        generation = userMeta.getGeneration();
        userKey = userMeta.getUserKey();
    }

    @Override
    public String toString() {
        return  "{ lastReadIndex: " + lastReadIndex +
                ", name: " + name +
                ", pictureURL: " + pictureURL +
                ", generation: " + generation +
                ", userKey: " + userKey + " }";
    }
}