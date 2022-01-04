package com.example.chat_de.datas;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class User {
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