package com.example.chat_de;

import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoomUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChatDB {
    public static final String CHAT_ROOMS = "chatRooms";
    public static final String CHATS = "chats";
    public static final String CHAT_ROOM_META = "chatRoomMeta";
    public static final String LAST_MESSAGE_INDEX = "lastMessageIndex";
    public static final String LAST_READ_INDEX = "lastReadIndex";
    public static final String USERS = "users";
    public static final String CHAT_ROOM_JOINED = "chatRoomJoined";
    public static final String USER_JOINED = "userJoined";

    private static DatabaseReference ref = null;

    public static void setReference(String root) { // 시작할때 딱 1번만 호출할 것
        if(ref == null) {
            ref = FirebaseDatabase.getInstance().getReference(root);
        }
    }
    public static void uploadMessage(String message, int index, String chatRoomKey, String userKey, Chat.Type type) {
        Chat chat = new Chat(message, index, userKey, Chat.Type.TEXT);
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHATS).push().setValue(chat); // 데이터 푸쉬
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).setValue(index);
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).child(userKey).child(LAST_READ_INDEX).setValue(index);

        //이 부분은 firebase function으로 구현가능하면 그걸로 구현하는 것이 더 좋을 듯
        ref.child("chatRoomJoined").child(chatRoomKey).get().addOnCompleteListener(task -> {
            HashMap<String, ChatRoomUser> users = (HashMap<String, ChatRoomUser>)task.getResult().getValue();
            for(String key: users.keySet()) {
                ref.child("userJoined").child(key).child(chatRoomKey).child("lastMessageIndex").setValue(index);
            }
        });
    }
}
