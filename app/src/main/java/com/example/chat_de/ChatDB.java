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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
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
    public static final String DATE = "date";

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
        ref.child(USERS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                HashMap<String, User> item = new HashMap<>();
                for(DataSnapshot userSnapshot: task.getResult().getChildren()) {
                    item.put(userSnapshot.getKey(), userSnapshot.getValue(User.class));
                }
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
            if(error == null) {
                final String chatRoomKey = rf.getKey();
                HashMap<String, Object> result = new HashMap<>();
                for (UserListItem item : items) {
                    // chatRoomJoined의 chatRoomKey에 새로운 user들 추가
                    result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey, item.getUserKey()), new ChatRoomUser(item.getUserMeta()));
                    // userJoined의 userKey들에 새로운 chatRoom 추가
                    result.put(makePath(USER_JOINED, item.getUserKey(), chatRoomKey), new UserChatRoom(chatRoomMeta));
                }
                // 종합한 값들을 최종적으로 update
                ref.updateChildren(result).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = callUserName + "님이 새 채팅방을 생성하셨습니다.";
                        uploadMessage(message, -2, Chat.Type.SYSTEM, chatRoomKey, "SYSTEM", new HashMap<>());
                    } else {
                        Log.e("FRD", "Can not update data of users and the new chat room");
                    }
                });

                listener.eventListener(chatRoomKey);
            } else {
                Log.e("FDB", "Make chat room error: " + error.toString());
            }
        });
    }

    public static void uploadMessage(String message, int index, Chat.Type messageType, String chatRoomKey, String userKey, HashMap<String, ChatRoomUser> chatRoomUserList) {
        // upload message
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHATS).push().setValue(new Chat(message, index, userKey, messageType), (error, rf) -> {
            if(error == null && messageType != Chat.Type.SYSTEM) {
                rf.child(DATE).get().addOnCompleteListener(task -> {
                    Object serverTime;
                    if(task.isSuccessful()) {
                        serverTime = task.getResult().getValue();
                    } else {
                        serverTime = ServerValue.TIMESTAMP;
                        Log.e("FRD", "Can not get a server time");
                    }
                    HashMap<String, Object> result = new HashMap<>();
                    // update user read last message
                    result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey, userKey, LAST_READ_INDEX), index);
                    // update chat room's last message index and time
                    result.put(makePath(CHAT_ROOMS, chatRoomKey, CHAT_ROOM_META, LAST_MESSAGE_INDEX), index);
                    result.put(makePath(CHAT_ROOMS, chatRoomKey, CHAT_ROOM_META, LAST_MESSAGE_TIME), serverTime);
                    // update last message index and time of all users in the chat room
                    for(String key: chatRoomUserList.keySet()) {
                        result.put(makePath(USER_JOINED, key, chatRoomKey, CHAT_ROOM_META, LAST_MESSAGE_INDEX), index);
                        result.put(makePath(USER_JOINED, key, chatRoomKey, CHAT_ROOM_META, LAST_MESSAGE_TIME), serverTime);
                    }
                    // update
                    ref.updateChildren(result);
                });
            } else if(error != null) {
                Log.e("FRD", "Upload message error:" + error.toString());
            }
        });
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

    @NonNull
    private static String makePath(String... strings) {
        StringBuilder ret = new StringBuilder();
        for(String str: strings) {
            ret.append("/" + str);
        }

        return ret.toString();
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