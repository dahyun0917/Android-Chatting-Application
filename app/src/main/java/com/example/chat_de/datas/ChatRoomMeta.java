package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

// DB에서 chatRoomMeta들 전부 수정될때까지는 pictureURL에 관련된 주석 풀지 말 것
@IgnoreExtraProperties
public class ChatRoomMeta implements Serializable {

    public enum Type { BY_COLLEGE, BY_YEAR, BY_USER }
    private String name;
    private int lastMessageIndex;
    private Type type;
    private long lastMessageTime;
    private String pictureURL;

    public ChatRoomMeta() { }
    public ChatRoomMeta(String name, Type type, String pictureURL) {
        setName(name);
        setLastMessageIndex(-1);
        setType(type);
        setLastMessageTime(0L);
        setPictureURL(pictureURL);
    }
    //Copy constructor
    public ChatRoomMeta(@NonNull ChatRoomMeta original) {
        setName(original.getName());
        setLastMessageIndex(original.getLastMessageIndex());
        setType(original.getType());
        setLastMessageTime(original.getLastMessageTime());
        setPictureURL(original.getPictureURL());
    }

    public String getName()             { return name; }
    public int getLastMessageIndex()    { return lastMessageIndex; }
    public Type getType()               { return type; }
    public long getLastMessageTime()    { return lastMessageTime; }
    public String getPictureURL()       { return pictureURL; }

    public void setName(String name)                        { this.name = name; }
    public void setLastMessageIndex(int lastMessageIndex)   { this.lastMessageIndex = lastMessageIndex; }
    public void setType(Type type)                          { this.type = type; }
    public void setLastMessageTime(long lastMessageTime)    { this.lastMessageTime = lastMessageTime; }
    public void setPictureURL(String pictureURL)            { this.pictureURL = pictureURL; }

    @Override
    public String toString() {
        return  "{ name: " + name +
                ", lastMessageIndex: " + lastMessageIndex +
                ", type: " + type +
                ", lastMessageTime: " + lastMessageTime +
                ", pictureURL: " + pictureURL + " }";
    }
}
