package com.example.chat_de.datas;

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

    public HashMap<String, Chat> getChats()         { return chats; }
    public HashMap<String, ChatRoomUser> getUsers() { return users; }
    public ChatRoomMeta getChatRoomMeta()           { return chatRoomMeta; }

    public void setChats(HashMap<String, Chat> chats)           { this.chats = chats; }
    public void setUsers(HashMap<String, ChatRoomUser> users)   { this.users = users; }
    public void setChatRoomMeta( ChatRoomMeta chatRoomMeta)     { this.chatRoomMeta = chatRoomMeta; }
}