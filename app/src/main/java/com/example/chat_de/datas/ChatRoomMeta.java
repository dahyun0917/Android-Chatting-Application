package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChatRoomMeta {
    public enum Type { BY_COLLEGE, BY_YEAR, BY_USER }
    private String name;
    private int lastMessageIndex;
    private Type type;
    private long lastMessageTime;

    public ChatRoomMeta() { }
    public ChatRoomMeta(String name, Type type) {
        setName(name);
        //setLastMessageIndex(-1);
        setLastMessageIndex(-1);
        setType(type);
        setLastMessageTime(0L);
    }
    //Copy constructor
    public ChatRoomMeta(@NonNull ChatRoomMeta original) {
        setName(original.getName());
        setLastMessageIndex(original.getLastMessageIndex());
        setType(original.getType());
        setLastMessageTime(original.getLastMessageTime());
    }

    public String getName()             { return name; }
    public int getLastMessageIndex()    { return lastMessageIndex; }
    public Type getType()               { return type; }
    public long getLastMessageTime()    { return lastMessageTime; }

    public void setName(String name)                        { this.name = name; }
    public void setLastMessageIndex(int lastMessageIndex)   { this.lastMessageIndex = lastMessageIndex; }
    public void setType(Type type)                          { this.type = type; }
    public void setLastMessageTime(long lastMessageTime)    { this.lastMessageTime = lastMessageTime; }
}
