package com.example.chat_de;

import androidx.annotation.NonNull;

import com.example.chat_de.datas.User;

public class UserListItem extends User {
    private boolean checked;

    public UserListItem() { }
    public UserListItem(String name, String pictureURL, int generation, String userKey, boolean admin) {
        super(name, pictureURL, generation, userKey, admin);
        setChecked(false);
    }
    //유저클래스로 생성
    public UserListItem(User userMeta) {
        setUserMeta(userMeta);
        setChecked(false);
    }
    //Copy constructor
    public UserListItem(UserListItem original) {
        super(original);
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
}
