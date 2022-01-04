package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomUser {
    private int lastReadIndex;

    public ChatRoomUser() { }
    public ChatRoomUser(int lastReadIndex) {
        setLastReadIndex(lastReadIndex);
    }

    public int getLastReadIndex() { return lastReadIndex; }

    public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
}