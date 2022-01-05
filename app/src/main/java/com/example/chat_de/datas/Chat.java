package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {
    public enum Type { TEXT, IMAGE, FILE, SYSTEM }

    private String text;
    private int index;
    private Object date;
    private String from;
    private Type type;

    public Chat() { }
    public Chat(String text, int index, String from, Type type) {
        setText(text);
        setIndex(index);
        setDate(ServerValue.TIMESTAMP);
        setFrom(from);
        setType(type);
    }

    public String getText() { return text; }
    public int getIndex()   { return index; }
    public Object getDate() { return date; }
    public String getFrom() { return from; }
    public Type getType()   { return type; }

    public void setText(String text)    { this.text = text; }
    public void setIndex(int index)     { this.index = index; }
    public void setType(Type type)      { this.type = type; }
    public void setDate(Object date)    { this.date = date; }
    public void setFrom(String from)    { this.from = from; }

    public long unixTime() {
        return (long)date;
    }
    public Date normalDate() {
        return new Date((long)date);
    }
}