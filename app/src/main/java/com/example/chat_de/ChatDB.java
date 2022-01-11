package com.example.chat_de;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public static void getUsersCompleteEventListener(RoomElementEventListener<HashMap<String, HashMap<String, Object>>> listener) {
        ref.child(USERS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                HashMap<String, HashMap<String, Object>> item = (HashMap<String, HashMap<String, Object>>)task.getResult().getValue();
                listener.eventListener(item);
            } else {
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
                    HashMap<String, HashMap<String, Object>> users = (HashMap<String, HashMap<String, Object>>)task.getResult().getValue();
                    for (String key: users.keySet()) {
                        ref.child(USER_JOINED).child(key).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).setValue(index);
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

    public static void messageAddedEventListener(String chatRoomKey, RoomElementEventListener<Chat> listener) {
        addEventListener(CHAT_ROOMS + '/' + chatRoomKey + '/' + CHATS,
                Thread.currentThread().getStackTrace()[3].getClassName(),
                new ChildEventListener() {
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
                });
    }
    public static void userListChangedEventListener(String chatRoomKey, RoomElementEventListener<Pair<String, ChatRoomUser>> listener) {
        addEventListener(CHAT_ROOM_JOINED + '/' + chatRoomKey,
                Thread.currentThread().getStackTrace()[3].getClassName(),
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        listener.eventListener(new Pair<>(snapshot.getKey(), snapshot.getValue(ChatRoomUser.class)));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        listener.eventListener(new Pair<>(snapshot.getKey(), snapshot.getValue(ChatRoomUser.class)));
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public static void chatRoomListChangedEventListener(String userKey, RoomElementEventListener<ChatRoomMeta> listener) {
        //TODO
    }
    private static void addEventListener(String path, String className, ChildEventListener eventListener) {
        ref.child(path).addChildEventListener(eventListener);
        if(!eventListeners.containsKey(className)) {
            eventListeners.put(className, new ArrayList<>());
        }
        eventListeners.get(className).add(new Pair<>(path, eventListener));
    }



    public static void removeEventListenerBindOnThis() {
        final String CLASS_NAME = Thread.currentThread().getStackTrace()[3].getClassName();

        if(eventListeners.containsKey(CLASS_NAME)) {
            for(Pair<String, ChildEventListener> i: eventListeners.get(CLASS_NAME)) {
                ref.child(i.first).removeEventListener(i.second);
            }
            eventListeners.get(CLASS_NAME).clear();
        }
    }
}