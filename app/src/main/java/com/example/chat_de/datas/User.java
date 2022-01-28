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
    /*chat_userData_arr to User*/
    public User(String[] chat_userData_arr) {
        /****************************
         * chat_userData_arr 정보
         * [0]: userKey
         * [1]: userName
         * [2]: pictureURL
         * [3]: generation
         * [4]: 기수 접근 권한 정보
         * [5]: admin
         ***************************/
        super(chat_userData_arr[1], chat_userData_arr[2], Integer.parseInt(chat_userData_arr[3]), chat_userData_arr[0]);
        //TODO: chat_userData_arr[5]의 값에 따라 if조건 수정해야 함
        if(true) {
            setAdmin(true);
        }
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
