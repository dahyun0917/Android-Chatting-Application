package com.example.chat_de;

import java.util.concurrent.ScheduledExecutorService;

public class UserItem {
        private boolean checked;
        private String name;
        private String pictureURL;
        private int generation;
        private String userKey;

        public UserItem() { }
        public UserItem(String name, String pictureURL, int generation, String userKey) {
            setName(name);
            setPictureURL(pictureURL);
            setGeneration(generation);
            setUserKey(userKey);
            checked = false;
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
