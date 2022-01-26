package com.example.chat_de;

import com.example.chat_de.datas.ChatRoomMeta;

public class ChatRoomListItem extends ChatRoomMeta {
    private String chatRoomKey;

    public ChatRoomListItem() { }
    public ChatRoomListItem(String chatRoomKey, ChatRoomMeta chatRoomMeta) {
        super(chatRoomMeta);
        setChatRoomKey(chatRoomKey);
    }

    public String getChatRoomKey() {
        return chatRoomKey;
    }

    public void setChatRoomKey(String chatRoomKey) {
        this.chatRoomKey = chatRoomKey;
    }
}