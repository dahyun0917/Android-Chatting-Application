package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserChatRoom {
    private int lastMessageIndex;
    private ChatRoomMeta chatRoomMeta;

    public UserChatRoom() { }
    public UserChatRoom(int lastMessageIndex, ChatRoomMeta chatRoomMeta) {
        setLastMessageIndex(lastMessageIndex);
        setChatRoomMeta(chatRoomMeta);
    }
    public UserChatRoom(ChatRoomMeta chatRoomMeta) {
        setLastMessageIndex(-1);
        setChatRoomMeta(chatRoomMeta);
    }
    //Copy constructor
    public UserChatRoom(@NonNull UserChatRoom original) {
        setLastMessageIndex(original.getLastMessageIndex());
        setChatRoomMeta(new ChatRoomMeta(original.getChatRoomMeta()));
    }

    public ChatRoomMeta getChatRoomMeta()   { return chatRoomMeta; }
    public int getLastMessageIndex()        { return lastMessageIndex; }

    public void setChatRoomMeta(ChatRoomMeta chatRoomMeta)  { this.chatRoomMeta = chatRoomMeta; }
    public void setLastMessageIndex(int lastMessageIndex)           { this.lastMessageIndex = lastMessageIndex; }
}