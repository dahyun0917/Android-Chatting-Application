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
import java.util.Iterator;

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
    private static final ArrayList<Pair<String, ChildEventListener>> eventListeners = new ArrayList<>();
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
    public static void setChatRoom(String chatRoomName, ArrayList<UserListItem> userList, String callUserName, RoomElementEventListener<String> listener) {
        final ChatRoomMeta chatRoomMeta = new ChatRoomMeta(chatRoomName, ChatRoomMeta.Type.BY_USER);
        ChatRoom chatRoom = new ChatRoom(new HashMap<>(), chatRoomMeta);
        ref.child(CHAT_ROOMS).push().setValue(chatRoom, (error, rf) -> {
            if(error == null) {
                final String chatRoomKey = rf.getKey();
                HashMap<String, Object> result = new HashMap<>();
                for (UserListItem item : userList) {
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
                Log.e("FRD", "Make chat room error: " + error.toString());
            }
        });
    }
    public static void setPersonalChatRoom(ChatRoomUser userMe, ChatRoomUser userOther, RoomElementEventListener<String> listener) {
        String chatRoomName = userMe.takeName()+", "+userOther.takeName();
        final ChatRoomMeta chatRoomMeta = new ChatRoomMeta(chatRoomName, ChatRoomMeta.Type.BY_USER);
        ChatRoom chatRoom = new ChatRoom(new HashMap<>(), chatRoomMeta);
        ref.child(CHAT_ROOMS).push().setValue(chatRoom, (error, rf) -> {
            if(error == null) {
                final String chatRoomKey = rf.getKey();
                HashMap<String, Object> result = new HashMap<>();
                // chatRoomJoined의 chatRoomKey에 새로운 user들 추가
                result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey, userMe.getUserMeta().getUserKey()), new ChatRoomUser(userMe.getUserMeta()));
                result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey, userOther.getUserMeta().getUserKey()), new ChatRoomUser(userOther.getUserMeta()));
                // userJoined의 userKey들에 새로운 chatRoom 추가
                result.put(makePath(USER_JOINED, userMe.getUserMeta().getUserKey(), chatRoomKey), new UserChatRoom(chatRoomMeta));
                result.put(makePath(USER_JOINED, userOther.getUserMeta().getUserKey(), chatRoomKey), new UserChatRoom(chatRoomMeta));

                // 종합한 값들을 최종적으로 update
                ref.updateChildren(result).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = userMe.takeName() + "님이 새 채팅방을 생성하셨습니다.";
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
                ref.child(makePath(CHAT_ROOM_JOINED, chatRoomKey, userKey, LAST_READ_INDEX)).setValue(task.getResult().getValue(Integer.class));
            } else {
                Log.e("FRD", "Can not get a lastMessageIndex of: " + chatRoomKey);
            }
        });
    }

    public static void getChatRoomUserListCompleteListener(String chatRoomKey, RoomElementEventListener<HashMap<String, ChatRoomUser>> listener) {
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                HashMap<String, ChatRoomUser> chatRoomUserList = new HashMap<>();
                for(DataSnapshot snapshot: task.getResult().getChildren()) {
                    chatRoomUserList.put(snapshot.getKey(), snapshot.getValue(ChatRoomUser.class));
                }
                listener.eventListener(chatRoomUserList);
            } else {
                Log.e("FRD", "Can not get " + chatRoomKey + "'s user list");
            }
        });
    }
    public static void getLastChatCompleteListener(String chatRoomKey, RoomElementEventListener<Pair<String, Chat>> listener) {
        ref.child(makePath(CHAT_ROOMS, chatRoomKey, CHATS)).limitToLast(1).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               Pair<String, Chat> result = new Pair<>(null, null);
               for(DataSnapshot snapshot: task.getResult().getChildren()) {
                   result = new Pair<>(snapshot.getKey(), snapshot.getValue(Chat.class));
                   break;
               }
               listener.eventListener(result);
           } else {
               Log.e("FRD", "Can not get last chat of the " + chatRoomKey);
           }
        });
    }
    public static void getPrevChatCompleteListener(String chatRoomKey, String frontChatKey, int chatLimit, RoomElementEventListener<Pair<String, ArrayList<Chat>>> listener) {
        if(frontChatKey != null) {
            ref.child(makePath(CHAT_ROOMS, chatRoomKey, CHATS)).orderByKey().endBefore(frontChatKey).limitToLast(chatLimit).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<Chat> chatList = new ArrayList<>();
                    String chatKey = null;
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        if (chatKey == null) {
                            chatKey = snapshot.getKey();
                        }
                        chatList.add(snapshot.getValue(Chat.class));
                    }
                    listener.eventListener(new Pair<>(chatKey, chatList));
                } else {
                    Log.e("FRD", "Can not get chats of the" + chatRoomKey);
                }
            });
        } else {
            listener.eventListener(new Pair<>(null, new ArrayList<>()));
        }
    }
    public static void messageAddedEventListener(String chatRoomKey, String lastChatKey, RoomElementEventListener<Pair<String, Chat>> listener) {
        class myChildEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listener.eventListener(new Pair<>(snapshot.getKey(), snapshot.getValue(Chat.class)));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        }
        myChildEventListener myListener = new myChildEventListener();
        String path = makePath(CHAT_ROOMS, chatRoomKey, CHATS);

        if(lastChatKey != null) {   // 빈 채팅방이 아닐 때
            ref.child(path).orderByKey().startAfter(lastChatKey).addChildEventListener(myListener);
        } else {                    // 빈 채팅방일 때
            ref.child(path).addChildEventListener(myListener);
        }
        eventListeners.add(new Pair<>(path, myListener));
    }
    public static void userListChangedEventListener(String chatRoomKey, RoomElementEventListener<Pair<String, ChatRoomUser>> listener) {
        String path = makePath(CHAT_ROOM_JOINED, chatRoomKey);
        ref.child(path).addChildEventListener(new ChildEventListener() {
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
    public static void chatRoomListChangedEventListener(String userKey, ChatRoomListAdapter chatRoomListAdapter) {
        ref.child(makePath(USER_JOINED, userKey)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatRoomMeta chatRoomMeta = snapshot.getChildren().iterator().next().getValue(ChatRoomMeta.class);
                //TODO : 나중에 ChatRoomMeta에 pictureURL 추가되면 그것도 받아서
                chatRoomListAdapter.addChatRoom(snapshot.getKey(),"",chatRoomMeta.getName());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatRoomMeta chatRoomMeta = snapshot.getChildren().iterator().next().getValue(ChatRoomMeta.class);
                chatRoomListAdapter.changeChatRoom(snapshot.getKey(),"",chatRoomMeta.getName());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                chatRoomListAdapter.removeChatRoom(snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    private static String makePath(@NonNull String... strings) {
        StringBuilder ret = new StringBuilder();
        for(String str: strings) {
            ret.append("/").append(str);
        }

        return ret.toString();
    }

    public static void removeEventListenerBindOnThis() {
        for(Pair<String, ChildEventListener> i: eventListeners) {
            ref.child(i.first).removeEventListener(i.second);
        }
        eventListeners.clear();
    }
}
