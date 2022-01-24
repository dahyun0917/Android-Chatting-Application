package com.example.chat_de;

import androidx.annotation.NonNull;

import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.User;

public class UserListItem extends AUser {
    private boolean checked;

    public UserListItem() { }
    public UserListItem(String name, String pictureURL, int generation, String userKey) {
        super(name, pictureURL, generation, userKey);
        setChecked(false);
    }
    //유저클래스로 생성
    public UserListItem(User userMeta) {
        setUserMeta(userMeta);
        setChecked(false);
    }
    //Copy constructor
    public UserListItem(@NonNull UserListItem original) {
        super(original.userMeta());
        setChecked(original.getChecked());
    }

    public boolean getChecked() { return checked; }

    public void setChecked(boolean checked) { this.checked = checked; }
    public void setUserMeta(@NonNull User userMeta) {
        name = userMeta.getName();
        pictureURL = userMeta.getPictureURL();
        generation = userMeta.getGeneration();
        userKey = userMeta.getUserKey();
    }

    @Override
    public String toString() {
        return  "{ name: " + name +
                ", pictureURL: " + pictureURL +
                ", generation: " + generation +
                ", userKey: " + userKey +
                ", checked: " + checked + " }";
    }
}
