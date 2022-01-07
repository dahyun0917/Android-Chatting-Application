package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class ChatRoom {
    private HashMap<String, Chat> chats;
    private ChatRoomMeta chatRoomMeta;

    public ChatRoom() { }
    public ChatRoom(HashMap<String, Chat> chats, ChatRoomMeta chatRoomMeta) {
        setChats(chats);
        setChatRoomMeta(chatRoomMeta);
    }
    //Copy constructor
    public ChatRoom(@NonNull ChatRoom original) {
        setChats(original.getChats());
        setChatRoomMeta(original.getChatRoomMeta());
    }

    public HashMap<String, Chat> getChats()         { return chats; }
    public ChatRoomMeta getChatRoomMeta()           { return chatRoomMeta; }

    public void setChats(@NonNull HashMap<String, Chat> chats)          { this.chats = (HashMap<String, Chat>) chats.clone(); }
    public void setChatRoomMeta(ChatRoomMeta chatRoomMeta)              { this.chatRoomMeta = new ChatRoomMeta(chatRoomMeta); }
}