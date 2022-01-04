package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserChatRoom {
    private int lastMessageIndex;

    public UserChatRoom() { }
    public UserChatRoom(int lastMessageIndex) {
        setLastMessageIndex(lastMessageIndex);
    }

    public int getLastMessageIndex() { return lastMessageIndex; }

    public void setLastMessageIndex(int lastMessageIndex) { this.lastMessageIndex = lastMessageIndex; }
}