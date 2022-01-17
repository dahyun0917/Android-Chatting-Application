package com.example.chat_de;

import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoomUser;

import java.util.Date;

public class RoomElementItem {
    //TODO 그냥 처음부터 챗이 from대신 chatroomuser를 가지고 있으면 해결됨..
    //TODO 근데 구조를 바꿔야되는거니까 나중에 시간날때 해야될듯
    private String text;
    private int index;
    private Date date;
    private String from;
    private Chat.Type type;
    private ChatRoomUser messageUser;
    private int currentReadStates;

    public RoomElementItem(Chat chat){
        setText(chat.getText());
        setIndex(chat.getIndex());
        setDate(chat.normalDate());
        setFrom(chat.getFrom());
        setType(chat.getType());
    }
    public RoomElementItem(String text, int index, Date date, String from, Chat.Type type,ChatRoomUser messageUser){
        setText(text);
        setIndex(index);
        setDate(date);
        setFrom(from);
        setType(type);
        setMessageUser(messageUser);
    }

    public void setText(String text) { this.text = text; }
    public void setIndex(int index) { this.index = index; }
    public void setDate(Date date)  { this.date = date; }
    public void setFrom(String from) { this.from = from; }
    public void setType(Chat.Type type) { this.type = type; }
    public void setMessageUser(ChatRoomUser messageUser) { this.messageUser = messageUser; }

    public void setCurrentReadStates(int currentReadStates) { this.currentReadStates = currentReadStates; }

    public String getText()   { return text; }
    public int getIndex()   { return index; }
    public Date getDate()   { return date; }
    public String getFrom()   { return from; }
    public Chat.Type getType()   { return type; }
    public ChatRoomUser getMessageUser()   { return messageUser; }

    public int getCurrentReadStates() { return currentReadStates; }
}
