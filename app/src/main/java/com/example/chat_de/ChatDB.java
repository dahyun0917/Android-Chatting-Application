package com.example.chat_de;

import android.util.Log;

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
    public static DatabaseReference getReference() {
        return ref;
    }

    public static void uploadMessage(String message, int index, Chat.Type messageType, String chatRoomKey, String userKey) {
        Chat chat = new Chat(message, index, userKey, messageType);
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHATS).push().setValue(chat); // 데이터 푸시
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).setValue(index);
        readMessageIndex(index, chatRoomKey, userKey);

        //이 부분은 firebase function으로 구현가능하면 그걸로 구현하는 것이 더 좋을 듯
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                HashMap<String, ChatRoomUser> users = (HashMap<String, ChatRoomUser>) task.getResult().getValue();
                for (String key : users.keySet()) {
                    ref.child(USER_JOINED).child(key).child(chatRoomKey).child(LAST_MESSAGE_INDEX).setValue(index);
                }
            } else {
                Log.e("FDB", "Can not get users of: " + chatRoomKey);
            }
        });
    }

    public static void readLatestMessage(String chatRoomKey, String userKey) {
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                readMessageIndex(task.getResult().getValue(Integer.class), chatRoomKey, userKey);
            } else {
                Log.e("FDB", "Can not get a lastMessageIndex of: " + chatRoomKey);
            }
        });
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).child(userKey).child(LAST_READ_INDEX);
    }
    private static void readMessageIndex(int index, String chatRoomKey, String userKey) {
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).child(userKey).child(LAST_READ_INDEX).setValue(index);
    }
}
