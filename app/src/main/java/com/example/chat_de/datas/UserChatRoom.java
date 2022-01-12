package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserChatRoom {
    private ChatRoomMeta chatRoomMeta;

    public UserChatRoom() { }
    public UserChatRoom(int lastMessageIndex, ChatRoomMeta chatRoomMeta) {
        setChatRoomMeta(chatRoomMeta);
    }
    public UserChatRoom(ChatRoomMeta chatRoomMeta) {
        setChatRoomMeta(chatRoomMeta);
    }
    //Copy constructor
    public UserChatRoom(@NonNull UserChatRoom original) {
        setChatRoomMeta(new ChatRoomMeta(original.getChatRoomMeta()));
    }

    public ChatRoomMeta getChatRoomMeta()   { return chatRoomMeta; }

    public void setChatRoomMeta(ChatRoomMeta chatRoomMeta)  { this.chatRoomMeta = chatRoomMeta; }
}