package com.example.chat_de;

import com.example.chat_de.datas.ChatRoomUser;

public interface IUserChangedEventListener {
    void onChanged(ChatRoomUser changedUser);
    void onRemoved(ChatRoomUser exitedUser);
}