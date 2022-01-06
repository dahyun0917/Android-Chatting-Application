package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserChatRoom {
    private int lastMessageIndex;

    public UserChatRoom() { }
    public UserChatRoom(int lastMessageIndex) {
        setLastMessageIndex(lastMessageIndex);
    }
    //Copy constructor
    public UserChatRoom(@NonNull UserChatRoom original) {
        setLastMessageIndex(original.getLastMessageIndex());
    }

    public int getLastMessageIndex() { return lastMessageIndex; }

    public void setLastMessageIndex(int lastMessageIndex) { this.lastMessageIndex = lastMessageIndex; }
}