package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomUser {
    private int lastReadIndex;
    private String userKey;

    public ChatRoomUser() { }
    public ChatRoomUser(int lastReadIndex, String userKey) {
        setLastReadIndex(lastReadIndex);
        setUserKey(userKey);
    }

    public int getLastReadIndex()   { return lastReadIndex; }
    public String getUserKey()      { return userKey; }

    public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
    public void setUserKey(String userKey)          { this.userKey = userKey; }
}