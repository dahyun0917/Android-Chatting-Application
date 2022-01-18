package com.example.chat_de;

public class ChatRoomListItem {
    private String chatRoomKey;
    private String chatRoomPictureURL;
    private String chatRoomName;

    public ChatRoomListItem() {    }
    public ChatRoomListItem(String chatRoomKey, String chatRoomPictureURL, String chatRoomName) {
        this.chatRoomKey = chatRoomKey;
        this.chatRoomPictureURL = chatRoomPictureURL;
        this.chatRoomName = chatRoomName;
    }

    public String getChatRoomPictureURL() {
        return chatRoomPictureURL;
    }
    public String getChatRoomKey() {
        return chatRoomKey;
    }

    public void setChatRoomKey(String chatRoomKey) {
        this.chatRoomKey = chatRoomKey;
    }
    public void setChatRoomPictureURL(String chatRoomPictureURL) {
        this.chatRoomPictureURL = chatRoomPictureURL;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }


    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }
}
