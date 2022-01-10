package com.example.chat_de;

import com.example.chat_de.datas.User;

public class UserListItem {
        private boolean checked;
        private String name;
        private String pictureURL;
        private int generation;
        private String userKey;

        public UserListItem() { }
        public UserListItem(String name, String pictureURL, int generation, String userKey) {
            setName(name);
            setPictureURL(pictureURL);
            setGeneration(generation);
            setUserKey(userKey);
            setChcked(false);
        }

        //유저클래스로 생성
        public UserListItem(User user) {
            setName(user.getName());
            setPictureURL(user.getPictureURL());
            setGeneration(user.getGeneration());
            setUserKey(user.getUserKey());
            setChcked(false);
        }

        //Copy constructor
        public UserListItem(UserListItem original) {
            setName(original.getName());
            setPictureURL(original.getPictureURL());
            setGeneration(original.getGeneration());
            setUserKey(original.getUserKey());
            setChcked(false);
        }

        public boolean getChecked()     { return checked; }
        public String getName()         { return name; }
        public String getPictureURL()   { return pictureURL; }
        public int getGeneration()      { return generation; }
        public String getUserKey()      { return userKey; }

        public void setChcked(boolean checked)          { this.checked = checked; }
        public void setName(String name)                { this.name = name; }
        public void setPictureURL(String pictureURL)    { this.pictureURL = pictureURL; }
        public void setGeneration(int generation)       { this.generation = generation; }
        public void setUserKey(String userKey)          { this.userKey = userKey; }
}
