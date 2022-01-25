package com.example.chat_de;

import com.example.chat_de.datas.ChatRoomMeta;

public interface IChatRoomListChangedListener {
    void ChatRoomAdded(String key, ChatRoomMeta chatRoomMeta);
    void ChatRoomChanged(String key, ChatRoomMeta chatRoomMeta);
    void ChatRoomRemoved(String key);
}