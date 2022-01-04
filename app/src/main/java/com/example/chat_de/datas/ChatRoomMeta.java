package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomMeta {
    private String name;
    private int lastMessageIndex;
    private String type;

    public ChatRoomMeta() { }
    public ChatRoomMeta(String name, int lastMessageIndex, String type) {
        setName(name);
        setLastMessageIndex(lastMessageIndex);
        setType(type);
    }

    public String getName()             { return name; }
    public int getLastMessageIndex()    { return lastMessageIndex; }
    public String getType()             { return type; }

    public void setName(String name)                        { this.name = name; }
    public void setLastMessageIndex(int lastMessageIndex)   { this.lastMessageIndex = lastMessageIndex; }
    public void setType(String type)                        { this.type = type; }
}