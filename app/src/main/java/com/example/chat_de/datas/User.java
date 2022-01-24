package com.example.chat_de.datas;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User extends AUser {
    protected boolean admin;

    public User() { }
    public User(String name, String pictureURL, int generation, String userKey, boolean admin) {
        super(name, pictureURL, generation, userKey);
        setAdmin(admin);
    }
    //Copy constructor
    public User(@NonNull User original) {
        super(original.userMeta());
        setAdmin(admin);
    }

    public boolean getAdmin() { return admin; }

    public void setAdmin(boolean admin) { this.admin = admin; }

    @Override
    public String toString() {
        return  "{ name: " + name +
                ", pictureURL: " + pictureURL +
                ", generation: " + generation +
                ", userKey: " + userKey +
                ", admin: " + admin + " }";
    }
}
