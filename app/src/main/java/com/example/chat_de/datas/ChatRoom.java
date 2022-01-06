package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class ChatRoom {
    private HashMap<String, Chat> chats;
    private HashMap<String, ChatRoomUser> users;
    private ChatRoomMeta chatRoomMeta;

    public ChatRoom() { }
    public ChatRoom(HashMap<String, Chat> chats, HashMap<String, ChatRoomUser> users, ChatRoomMeta chatRoomMeta) {
        setChats(chats);
        setUsers(users);
        setChatRoomMeta(chatRoomMeta);
    }
    //Copy constructor
    public ChatRoom(@NonNull ChatRoom original) {
        setChats(original.getChats());
        setUsers(original.getUsers());
        setChatRoomMeta(original.getChatRoomMeta());
    }

    public HashMap<String, Chat> getChats()         { return chats; }
    public HashMap<String, ChatRoomUser> getUsers() { return users; }
    public ChatRoomMeta getChatRoomMeta()           { return chatRoomMeta; }

    public void setChats(@NonNull HashMap<String, Chat> chats)          { this.chats = (HashMap<String, Chat>) chats.clone(); }
    public void setUsers(@NonNull HashMap<String, ChatRoomUser> users)  { this.users = (HashMap<String, ChatRoomUser>) users.clone(); }
    public void setChatRoomMeta(ChatRoomMeta chatRoomMeta)              { this.chatRoomMeta = new ChatRoomMeta(chatRoomMeta); }
}