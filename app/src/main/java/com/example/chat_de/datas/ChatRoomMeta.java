package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomMeta {
    public enum Type { BY_COLLEGE, BY_YEAR, BY_USER }
    private String name;
    private int lastMessageIndex;
    private Type type;

    public ChatRoomMeta() { }
    public ChatRoomMeta(String name, int lastMessageIndex, Type type) {
        setName(name);
        setLastMessageIndex(lastMessageIndex);
        setType(type);
    }

    public String getName()             { return name; }
    public int getLastMessageIndex()    { return lastMessageIndex; }
    public Type getType()             { return type; }

    public void setName(String name)                        { this.name = name; }
    public void setLastMessageIndex(int lastMessageIndex)   { this.lastMessageIndex = lastMessageIndex; }
    public void setType(Type type)                        { this.type = type; }
}