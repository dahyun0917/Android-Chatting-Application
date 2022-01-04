package com.example.chat_de;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

public class FirebaseDataStructure {
    @IgnoreExtraProperties
    public static class Chat {
        private String text;
        private int index;
        private int date;
        private String from;
        private String type;

        public Chat() { }
        public Chat(String text, int index, int date, String from, String type) {
            setText(text);
            setIndex(index);
            setDate(date);
            setFrom(from);
            setType(type);
        }

        public String getText() { return text; }
        public int getIndex()   { return index; }
        public int getDate()    { return date; }
        public String getFrom() { return from; }
        public String getType() { return type; }

        public void setText(String text)    { this.text = text; }
        public void setIndex(int index)     { this.index = index; }
        public void setType(String type)    { this.type = type; }
        public void setDate(int date)       { this.date = date; }
        public void setFrom(String from)    { this.from = from; }
    }

    @IgnoreExtraProperties
    public static class ChatRoomMeta {
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

    @IgnoreExtraProperties
    public static class ChatRoomUser {
        private int lastReadIndex;

        public ChatRoomUser() { }
        public ChatRoomUser(int lastReadIndex) {
            setLastReadIndex(lastReadIndex);
        }

        public int getLastReadIndex() { return lastReadIndex; }

        public void setLastReadIndex(int lastReadIndex) { this.lastReadIndex = lastReadIndex; }
    }

    @IgnoreExtraProperties
    public static class ChatRoom {
        private HashMap<String, Chat> messages;
        private HashMap<String, ChatRoomUser> users;
        private ChatRoomMeta chatRoomMeta;

        public ChatRoom() { }
        public ChatRoom(HashMap<String, Chat> chats, HashMap<String, ChatRoomUser> users, ChatRoomMeta chatRoomMeta) {
            setMessages(chats);
            setUsers(users);
            setChatRoomMeta(chatRoomMeta);
        }

        public HashMap<String, Chat> getMessages()   { return messages; }
        public HashMap<String, ChatRoomUser> getUsers() { return users; }
        public ChatRoomMeta getChatRoomMeta()           { return chatRoomMeta; }

        public void setMessages(HashMap<String, Chat> messages)  { this.messages = messages; }
        public void setUsers(HashMap<String, ChatRoomUser> users)   { this.users = users; }
        public void setChatRoomMeta(ChatRoomMeta chatRoomMeta)      { this.chatRoomMeta = chatRoomMeta; }
    }

    @IgnoreExtraProperties
    public static class UserChatRoom {
        private int lastMessageIndex;

        public UserChatRoom() { }
        public UserChatRoom(int lastMessageIndex) {
            setLastMessageIndex(lastMessageIndex);
        }

        public int getLastMessageIndex() { return lastMessageIndex; }

        public void setLastMessageIndex(int lastMessageIndex) { this.lastMessageIndex = lastMessageIndex; }
    }

    @IgnoreExtraProperties
    public static class UserMeta {
        private String name;
        private String pictureURL;

        public UserMeta() { }
        public UserMeta(String name, String pictureURL) {
            setName(name);
            setPictureURL(pictureURL);
        }

        public String getName()         { return name; }
        public String getPictureURL()   { return pictureURL; }

        public void setName(String name)                { this.name = name; }
        public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
    }

    @IgnoreExtraProperties
    public static class User {
        private HashMap<String, UserChatRoom> joined;
        private UserMeta userMeta;

        public User() { }
        public User(HashMap<String, UserChatRoom> joined, UserMeta userMeta) {
            setJoined(joined);
            setUserMeta(userMeta);
        }

        public HashMap<String, UserChatRoom> getJoined()    { return joined; }
        public UserMeta getUserMeta()                       { return userMeta; }

        public void setJoined(HashMap<String, UserChatRoom> joined) { this.joined = joined; }
        public void setUserMeta(UserMeta userMeta)                  { this.userMeta = userMeta; }
    }
}
