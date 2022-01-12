package com.example.chat_de;

import com.example.chat_de.datas.User;

public class UserListItem {
        private boolean checked;
        private User userMeta;

        public UserListItem() { }
        public UserListItem(String name, String pictureURL, int generation, String userKey) {
            setUserMeta(new User(name, pictureURL, generation, userKey));
            setChecked(false);
        }
        //유저클래스로 생성
        public UserListItem(User userMeta) {
            setUserMeta(userMeta);
            setChecked(false);
        }

        //Copy constructor
        public UserListItem(UserListItem original) {
            setUserMeta(new User(original.getUserMeta()));
            setChecked(original.getChecked());
        }

        public User getUserMeta()           { return userMeta; }
        public boolean getChecked()     { return checked; }
        public String getName()         { return userMeta.getName(); }
        public String getPictureURL()   { return userMeta.getPictureURL(); }
        public int getGeneration()      { return userMeta.getGeneration(); }
        public String getUserKey()      { return userMeta.getUserKey(); }

        public void setUserMeta(User userMeta)                  { this.userMeta = userMeta; }
        public void setChecked(boolean checked)         { this.checked = checked; }
        public void setName(String name)                { this.userMeta.setName(name); }
        public void setPictureURL(String pictureURL)    { this.userMeta.setPictureURL(pictureURL); }
        public void setGeneration(int generation)       { this.userMeta.setGeneration(generation); }
        public void setUserKey(String userKey)          { this.userMeta.setUserKey(userKey); }
}
