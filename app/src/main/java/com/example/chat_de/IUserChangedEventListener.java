package com.example.chat_de;

import com.example.chat_de.datas.ChatRoomUser;

public interface IUserChangedEventListener {
    void changed(ChatRoomUser changedUser);
    void removed(ChatRoomUser exitedUser);
}
