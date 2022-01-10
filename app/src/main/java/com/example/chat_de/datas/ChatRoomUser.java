package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomUser {
    private int lastReadIndex;
    private User userMeta;
    private String userKey;

    public ChatRoomUser() { }
    public ChatRoomUser(int lastReadIndex, String userKey, User userMeta) {
        setLastReadIndex(lastReadIndex);
        setUserKey(userKey);
        setUserMeta(userMeta);
    }
    //Copy constructor
    public ChatRoomUser(@NonNull ChatRoomUser original) {
        setLastReadIndex(original.getLastReadIndex());
        setUserKey(original.getUserKey());
        setUserMeta(new User(original.getUserMeta()));
    }

    public int getLastReadIndex()   { return lastReadIndex; }
    public String getUserKey()      { return userKey; }
    public User getUserMeta()       { return userMeta; }

    public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
    public void setUserKey(String userKey)          { this.userKey = userKey; }
    public void setUserMeta(User userMeta)        { this.userMeta = userMeta; }
}