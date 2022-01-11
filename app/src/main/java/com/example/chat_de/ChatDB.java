package com.example.chat_de;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.User;
import com.example.chat_de.datas.UserChatRoom;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatDB {
    public static final String CHAT_ROOMS = "chatRooms";
    public static final String CHATS = "chats";
    public static final String CHAT_ROOM_META = "chatRoomMeta";
    public static final String LAST_MESSAGE_INDEX = "lastMessageIndex";
    public static final String LAST_MESSAGE_TIME = "lastMessageTime";
    public static final String LAST_READ_INDEX = "lastReadIndex";
    public static final String USERS = "users";
    public static final String CHAT_ROOM_JOINED = "chatRoomJoined";
    public static final String USER_JOINED = "userJoined";

    private static DatabaseReference ref = null;
    private static final HashMap<String, ArrayList<Pair<String, ChildEventListener>>> eventListeners = new HashMap<>();
    private static String rootPath;

    public static void setReference(String root) { // 앱 시작할때 딱 1번만 호출할 것
        if(ref == null) {
            ref = FirebaseDatabase.getInstance().getReference(root);
            rootPath = root;
        }
    }
    public static DatabaseReference getReference() {
        return ref;
    }
    public static String getRootPath() {
        return rootPath;
    }

    public static void getUsersCompleteEventListener(RoomElementEventListener<HashMap<String, User>> listener) {
        ref.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, User> item = snapshot.getValue(HashMap.class);
                listener.eventListener(item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FRD", "Can not get users");
                listener.eventListener(new HashMap<>());
            }
        });        
    }
    public static void setChatRoom(String chatRoomName, ArrayList<UserListItem> items, String callUserName, RoomElementEventListener<String> listener) {
        final ChatRoomMeta chatRoomMeta = new ChatRoomMeta(callUserName, ChatRoomMeta.Type.BY_USER);
        ChatRoom chatRoom = new ChatRoom(new HashMap<>(), chatRoomMeta);
        ref.child(CHAT_ROOMS).push().setValue(chatRoom, (error, rf) -> {
            final String chatRoomKey = rf.getKey();
            final String U_J_PATH = "/" + USER_JOINED + "/";
            final String C_J_PATH = "/" + CHAT_ROOM_JOINED + "/";
            HashMap<String, Object> result = new HashMap<>();
            // chatRoomJoined의 chatRoomKey에 새로운 user들 추가
            for(UserListItem item: items) {
                result.put(C_J_PATH + chatRoomKey + "/" + item.getUserKey(), new ChatRoomUser(item.getUserMeta()));
            }
            // userJoined의 userKey들에 새로운 chatRoom 추가
            for(UserListItem item: items) {
                result.put(U_J_PATH + item.getUserKey() + "/" + chatRoomKey, new UserChatRoom(chatRoomMeta));
            }
            // 종합한 값들을 최종적으로 update
            ref.updateChildren(result).addOnCompleteListener(task -> {
                //TODO upload system message
                String message = callUserName+"님이 새 채팅방을 생성하셨습니다.";
                uploadMessage(message, -2, Chat.Type.SYSTEM, chatRoomKey, "SYSTEM");
            });

            listener.eventListener(chatRoomKey);
        });
    }

    public static void userListChangeEventListener(RoomElementEventListener<HashMap<String, ChatRoomUser>> listener) {

    }

    public static void uploadMessage(String message, int index, Chat.Type messageType, String chatRoomKey, String userKey) {
        Chat chat = new Chat(message, index, userKey, messageType);
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHATS).push().setValue(chat); // 데이터 푸시
        if(messageType != Chat.Type.SYSTEM) {
            ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_TIME).setValue(chat.getDate()); // 완전히 정확할 필요는 없으니 서버와 통신 줄이고자 이렇게 처리
            ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).setValue(index);
            userReadMessageIndex(index, chatRoomKey, userKey);
            //밑 부분은 firebase function으로 구현가능하면 그걸로 구현하는 것이 더 좋을 듯
            ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    HashMap<String, ChatRoomUser> users = (HashMap<String, ChatRoomUser>) task.getResult().getValue();
                    for (String key : users.keySet()) {
                        ref.child(USER_JOINED).child(key).child(chatRoomKey).child(LAST_MESSAGE_INDEX).setValue(index);
                    }
                } else {
                    Log.e("FRD", "Can not get users of: " + chatRoomKey);
                }
            });
        }
    }

    public static void userReadLatestMessage(String chatRoomKey, String userKey) {
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                userReadMessageIndex(task.getResult().getValue(Integer.class), chatRoomKey, userKey);
            } else {
                Log.e("FRD", "Can not get a lastMessageIndex of: " + chatRoomKey);
            }
        });
    }
    private static void userReadMessageIndex(int index, String chatRoomKey, String userKey) {
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).child(userKey).child(LAST_READ_INDEX).setValue(index);
    }

    public static void messageAddEventListener(String chatRoomKey, RoomElementEventListener<Chat> listener) {
        class childAddedEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                listener.eventListener(dataSnapshot.getValue(Chat.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }
        childAddedEventListener firebaseListener = new childAddedEventListener();
        final String CLASS_NAME = Thread.currentThread().getStackTrace()[3].getClassName();
        final String PATH = CHAT_ROOMS + '/' + chatRoomKey + '/' + CHATS;

        ref.child(PATH).addChildEventListener(firebaseListener);
        if(!eventListeners.containsKey(CLASS_NAME))
            eventListeners.put(CLASS_NAME, new ArrayList<>());
        eventListeners.get(CLASS_NAME).add(new Pair<>(PATH, firebaseListener));
    }
    public static void chatRoomListChangeEventListener(String userKey, RoomElementEventListener<ChatRoomMeta> listener) {
        class childAddedEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                listener.eventListener(dataSnapshot.getValue(ChatRoomMeta.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO 작업해야함
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO 작업해야함
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }
        //       setChildEventListener(CHAT_ROOMS + '/' + chatRoomKey + '/' + CHATS, listener, ChatRoomMeta.class);
    }

    public static void removeEventListenerBindOnThis() {
        final String CLASS_NAME = Thread.currentThread().getStackTrace()[3].getClassName();

        if(eventListeners.containsKey(CLASS_NAME)) {
            for(Pair<String, ChildEventListener> i : eventListeners.get(CLASS_NAME)) {
                ref.child(i.first).removeEventListener(i.second);
            }
            eventListeners.get(CLASS_NAME).clear();
        }
    }
}