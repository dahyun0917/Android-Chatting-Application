package com.example.chat_de;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.User;
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
    public static final String EXIST = "exist";
    public static final String CHAT_ROOM_NAME = "name";
    public static final String CHAT_ROOM_PICTURE = "pictureURL";
    public static final String ROOT = "KNU_AMP";
    public static final String ALL = "0";

    private static DatabaseReference ref = null;
    private static final HashMap<Integer, ArrayList<Pair<String, ChildEventListener>>> childEventListeners = new HashMap<>();
    private static final HashMap<Integer, ArrayList<Pair<String, ValueEventListener>>> valueEventListeners = new HashMap<>();
    private static int chatMode;
    private static String currentUserKey = null;
    private static boolean adminMode = false;
    private static boolean genAccessPossible = false;
    private static AUser currentUser = null;

    public static void initChatDB(String[] chat_userData_arr) {// 앱 시작할때 딱 1번만 호출할 것
        if(currentUser == null) {
            User user = new User(chat_userData_arr);
            currentUser = user;
            currentUserKey = chat_userData_arr[0];
            FirebaseDatabase.getInstance().getReference(makePath(ROOT, ALL, USERS, currentUserKey)).setValue(user);
            if (!chat_userData_arr[4].equals("003")) {
                FirebaseDatabase.getInstance().getReference(makePath(ROOT, chat_userData_arr[3], USERS, currentUserKey)).setValue(user);
                genAccessPossible = true;
            } else {
                genAccessPossible = false;
            }

            if(true) {
                adminMode = true;
            } else {
                adminMode = false;
            }
            chatMode = 0;
            ref = FirebaseDatabase.getInstance().getReference(makePath(ROOT, ALL));
            FileDB.setReference(makePath(ROOT, ALL));
        }
    }
    public static void changeRef() {
        // detach all listeners
        for(int key : childEventListeners.keySet()) {
            removeEventListener(key);
        }
        for(int key : valueEventListeners.keySet()) {
            removeEventListener(key);
        }

        // set new root
        String root;
        if(chatMode == 0) {
            chatMode = currentUser.getGeneration();
            root = makePath(ROOT, String.valueOf(chatMode));
        } else {
            chatMode = 0;
            root = makePath(ROOT, ALL);
        }
        // set new ref
        ref = FirebaseDatabase.getInstance().getReference(root);
        // set new firestore ref
        FileDB.setReference(root);
    }

    public static int getChatMode() {
        return chatMode;
    }
    public static boolean isGenAccessPossible() {
        return genAccessPossible;
    }
    public static DatabaseReference getReference() {
        return ref;
    }
    public static boolean getAdminMode() {
        return adminMode;
    }
    //TODO : intent로 본인의 키를 넘겨주는 부분 있으면 전부 이쪽으로 바꿔야 함
    public static String getCurrentUserKey() {
        return currentUserKey;
    }
    //TODO : intent로 본인의 데이터를 넘겨주는 부분 있으면 전부 이쪽으로 바꿔야 함
    public static AUser getCurrentUser() {
        return currentUser;
    }

    public static void checkUserExistCompleteListener(String userKey, IEventListener<Boolean> listener) {
        ref.child(makePath(USERS, userKey)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getValue() != null) {
                    listener.eventListener(true);
                } else {
                    listener.eventListener(false);
                }
            } else {
                Log.e("FRD", "Can not access database");
            }
        });
    }
    public static void setUser(String userKey, User user) {
        ref.child(makePath(USERS, userKey)).setValue(user, (error, rf) -> {
            if(error == null) {
                Log.i("FRD", "User data update success");
            } else {
                Log.e("FRD", "User data update fail: " + error);
            }
        });
    }

    public static void setChatRoomMeta(String chatRoomKey, ChatRoomMeta chatRoomMeta) {
        ref.child(makePath(CHAT_ROOMS, chatRoomKey)).setValue(chatRoomMeta, (error, rf) -> {
            if(error == null) {
                Log.i("FRD", "Chat room meta data update success");
            } else {
                Log.e("FRD", "Chat room meta data update fail: " + error);
            }
        });
    }
    public static void changeChatRoomMeta(String chatRoomKey, String chatRoomName, String chatRoomPicture) {
        ref.child(makePath(CHAT_ROOMS, chatRoomKey,CHAT_ROOM_META, CHAT_ROOM_PICTURE)).setValue(chatRoomPicture, (error, rf) -> {
            if(error == null) {
                Log.i("FRD", "Change chat room meta data success");
            } else {
                Log.e("FRD", "Change chat room meta data fail: " + error);
            }
        });
        ref.child(makePath(CHAT_ROOMS, chatRoomKey,CHAT_ROOM_META, CHAT_ROOM_NAME)).setValue(chatRoomName, (error, rf) -> {
            if(error == null) {
                Log.i("FRD", "Change chat room meta data success");
            } else {
                Log.e("FRD", "Change chat room meta data fail: " + error);
            }
        });
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DataSnapshot userKey : task.getResult().getChildren()) {
                    ref.child(makePath(USER_JOINED, userKey.getKey(), chatRoomKey,CHAT_ROOM_NAME)).setValue(chatRoomName);
                    ref.child(makePath(USER_JOINED, userKey.getKey(), chatRoomKey,CHAT_ROOM_PICTURE)).setValue(chatRoomPicture);
                }
            }
            else {
                Log.e("FRD", "Can not get users");
            }
        });
    }

    public static void getUsersCompleteListener(IEventListener<HashMap<String, User>> listener) {
        ref.child(USERS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, User> item = new HashMap<>();
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    item.put(userSnapshot.getKey(), userSnapshot.getValue(User.class));
                }
                listener.eventListener(item);
            } else {
                Log.e("FRD", "Can not get users");
                listener.eventListener(new HashMap<>());
            }
        });
    }
    public static void setChatRoomCompleteListener(String chatRoomName, ArrayList<AUser> userList, AUser userMe, IEventListener<String> listener) {
        final ChatRoomMeta chatRoomMeta = new ChatRoomMeta(chatRoomName, ChatRoomMeta.Type.BY_USER,"");
        ChatRoom chatRoom = new ChatRoom(new HashMap<>(), chatRoomMeta);
        ref.child(CHAT_ROOMS).push().setValue(chatRoom, (error, rf) -> {
            if (error == null) {
                inviteUserListCompleteListener(rf.getKey(), chatRoomMeta, userList, userMe, listener);
            } else {
                Log.e("FRD", "Make chat room error: " + error.toString());
            }
        });
    }
    public static void setChatRoomCompleteListener(String chatRoomName, String chatPictureURL , ArrayList<AUser> userList, AUser userMe, IEventListener<String> listener) {
        final ChatRoomMeta chatRoomMeta = new ChatRoomMeta(chatRoomName, ChatRoomMeta.Type.BY_USER,chatPictureURL);
        ChatRoom chatRoom = new ChatRoom(new HashMap<>(), chatRoomMeta);
        ref.child(CHAT_ROOMS).push().setValue(chatRoom, (error, rf) -> {
            if (error == null) {
                inviteUserListCompleteListener(rf.getKey(), chatRoomMeta, userList, userMe, listener);
            } else {
                Log.e("FRD", "Make chat room error: " + error.toString());
            }
        });
    }
    public static void setChatRoomCompleteListener(ChatRoomMeta chatRoomMeta, ArrayList<AUser> userList, AUser userMe, IEventListener<String> listener) {
        ChatRoom chatRoom = new ChatRoom(new HashMap<>(), chatRoomMeta);
        ref.child(CHAT_ROOMS).push().setValue(chatRoom, (error, rf) -> {
            if (error == null) {
                inviteUserListCompleteListener(rf.getKey(), chatRoomMeta, userList, userMe, listener);
            } else {
                Log.e("FRD", "Make chat room error: " + error.toString());
            }
        });
    }
    public static void inviteUserListCompleteListener(String chatRoomKey, ChatRoomMeta chatRoomMeta, ArrayList<AUser> userList, AUser userMe, IEventListener<String> listener) {
        HashMap<String, Object> result = new HashMap<>();
        for (AUser item : userList) {
            // chatRoomJoined의 chatRoomKey에 새로운 user들 추가
            result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey, item.getUserKey()), new ChatRoomUser(item.userMeta()));
            // userJoined의 userKey들에 새로운 chatRoom 추가
            result.put(makePath(USER_JOINED, item.getUserKey(), chatRoomKey), chatRoomMeta);
        }
        // 종합한 값들을 최종적으로 update
        ref.updateChildren(result).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder builder = new StringBuilder(userMe.getName() + "님이 ");
                for(AUser user: userList) {
                    if(!user.getUserKey().equals(userMe.getUserKey())) {
                        builder.append(user.getName()).append("님, ");
                    }
                }
                String message = builder.substring(0, builder.length() - 2) + "을 초대하셨습니다.";
                uploadMessage(message, -2, Chat.Type.SYSTEM, chatRoomKey, "SYSTEM", new HashMap<>());
            } else {
                Log.e("FRD", "Can not update data of users and the new chat room");
            }
        });

        listener.eventListener(chatRoomKey);
    }

    public static void uploadMessage(String message, int index, Chat.Type messageType, String chatRoomKey, String userKey, HashMap<String, ChatRoomUser> chatRoomUserList) {
        // upload message
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHATS).push().setValue(new Chat(message, index, userKey, messageType), (error, rf) -> {
            if (error == null && messageType != Chat.Type.SYSTEM) {
                rf.child(DATE).get().addOnCompleteListener(task -> {
                    Object serverTime;
                    if (task.isSuccessful()) {
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
                    for (String key : chatRoomUserList.keySet()) {
                        result.put(makePath(USER_JOINED, key, chatRoomKey, LAST_MESSAGE_INDEX), index);
                        result.put(makePath(USER_JOINED, key, chatRoomKey, LAST_MESSAGE_TIME), serverTime);
                    }
                    // update
                    ref.updateChildren(result);
                });
            } else if (error != null) {
                Log.e("FRD", "Upload message error:" + error.toString());
            }
        });
    }
    public static void userReadLastMessage(String chatRoomKey, String userKey) {
        ref.child(CHAT_ROOMS).child(chatRoomKey).child(CHAT_ROOM_META).child(LAST_MESSAGE_INDEX).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ref.child(makePath(CHAT_ROOM_JOINED, chatRoomKey, userKey, LAST_READ_INDEX)).setValue(task.getResult().getValue(Integer.class));
            } else {
                Log.e("FRD", "Can not get a lastMessageIndex of: " + chatRoomKey);
            }
        });
    }

    public static void getChatRoomUserListCompleteListener(String chatRoomKey, IEventListener<HashMap<String, ChatRoomUser>> listener) {
        ref.child(CHAT_ROOM_JOINED).child(chatRoomKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, ChatRoomUser> chatRoomUserList = new HashMap<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    chatRoomUserList.put(snapshot.getKey(), snapshot.getValue(ChatRoomUser.class));
                }
                listener.eventListener(chatRoomUserList);
            } else {
                Log.e("FRD", "Can not get " + chatRoomKey + "'s user list");
            }
        });
    }
    public static void getLastChatCompleteListener(String chatRoomKey, IKeyValueEventListener<String, Chat> listener) {
        ref.child(makePath(CHAT_ROOMS, chatRoomKey, CHATS)).limitToLast(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String resultKey = null;
                Chat resultValue = null;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    resultKey = snapshot.getKey();
                    resultValue = snapshot.getValue(Chat.class);
                    break;
                }
                listener.eventListener(resultKey, resultValue);
            } else {
                Log.e("FRD", "Can not get last chat of the " + chatRoomKey);
            }
        });
    }
    public static void getPrevChatListCompleteListener(String chatRoomKey, String frontChatKey, int chatLimit, IKeyValueEventListener<String, ArrayList<Chat>> listener) {
        if (frontChatKey != null) {
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
                    listener.eventListener(chatKey, chatList);
                } else {
                    Log.e("FRD", "Can not get chats of the" + chatRoomKey);
                }
            });
        } else {
            listener.eventListener(null, new ArrayList<>());
        }
    }

    public static void messageAddedEventListener(String chatRoomKey, String lastChatKey, int hashCode ,IKeyValueEventListener<String, Chat> listener) {
        class MyChildEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listener.eventListener(snapshot.getKey(), snapshot.getValue(Chat.class));
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
        MyChildEventListener myListener = new MyChildEventListener();
        String path = makePath(CHAT_ROOMS, chatRoomKey, CHATS);

        if (lastChatKey != null) {   // 빈 채팅방이 아닐 때
            ref.child(path).orderByKey().startAfter(lastChatKey).addChildEventListener(myListener);
        } else {                    // 빈 채팅방일 때
            ref.child(path).addChildEventListener(myListener);
        }

        registerEventListener(hashCode, path, myListener);
    }
    public static void userListChangedEventListener(String chatRoomKey, int hashCode, IUserChangedEventListener listener) {
        class MyChildEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listener.onChanged(snapshot.getValue(ChatRoomUser.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listener.onChanged(snapshot.getValue(ChatRoomUser.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                listener.onRemoved(snapshot.getValue(ChatRoomUser.class));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
        MyChildEventListener myListener = new MyChildEventListener();
        String path = makePath(CHAT_ROOM_JOINED, chatRoomKey);

        ref.child(path).addChildEventListener(myListener);
        registerEventListener(hashCode, path, myListener);
    }
    public static void chatRoomListChangedEventListener(String userKey, int hashCode, IChatRoomListChangedListener chatRoomListChangedListener) {
        class MyChildEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatRoomListChangedListener.ChatRoomAdded(snapshot.getKey(), snapshot.getValue(ChatRoomMeta.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatRoomListChangedListener.ChatRoomChanged(snapshot.getKey(), snapshot.getValue(ChatRoomMeta.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                chatRoomListChangedListener.ChatRoomRemoved(snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
        MyChildEventListener myListener = new MyChildEventListener();
        String path = makePath(USER_JOINED, userKey);

        ref.child(path).addChildEventListener(myListener);
        registerEventListener(hashCode, path, myListener);
    }

    public static void getChatRoomMeta(String chatRoomKey, IEventListener<ChatRoomMeta> listener) {
        ref.child(makePath(CHAT_ROOMS,chatRoomKey,CHAT_ROOM_META)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                listener.eventListener(dataSnapshot.getValue(ChatRoomMeta.class));
            } else {
                Log.e("FRD", "Can not get meta data of the" + chatRoomKey);
            }
        });
    }
    public static void chatRoomMetaChangedEventListener(String chatRoomKey, int hashCode, IEventListener<ChatRoomMeta> listener) {
        class MyValueEventListener implements ValueEventListener {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.eventListener(snapshot.getValue(ChatRoomMeta.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
        MyValueEventListener myListener = new MyValueEventListener();
        String path = makePath(CHAT_ROOMS, chatRoomKey, CHAT_ROOM_META);

        ref.child(path).addValueEventListener(myListener);
        registerEventListener(hashCode, path, myListener);
    }

    public static void exitChatRoomCompleteListener(String chatRoomKey, @NonNull ArrayList<AUser> userList, IVoidEventListener listener) {
        HashMap<String, Object> result = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for(AUser user: userList) {
            String userKey = user.getUserKey();
            result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey, userKey, EXIST), false);
            result.put(makePath(USER_JOINED, userKey, chatRoomKey), null);
            builder.append(user.getName()).append("님, ");
        }
        final String exitMessage = builder.substring(0, builder.length() - 2) + "이 채팅방을 나가셨습니다.";
        ref.updateChildren(result, (error, rf) -> {
            if(error == null) {
                uploadMessage(exitMessage, -2, Chat.Type.SYSTEM, chatRoomKey, "SYSTEM", new HashMap<>());
                listener.eventListener();
            } else {
                Log.e("FRD", "Can not delete data: " + error);
            }
        });
    }
    public static void closeChatRoomCompleteListener(String chatRoomKey, IVoidEventListener listener) {
        ref.child(makePath(CHAT_ROOM_JOINED, chatRoomKey)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> result = new HashMap<>();
                // 채팅방 삭제
                result.put(makePath(CHAT_ROOMS, chatRoomKey), null);
                // 채팅방에 속한 유저 정보 삭제
                result.put(makePath(CHAT_ROOM_JOINED, chatRoomKey), null);
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String userKey = snapshot.getValue(User.class).getUserKey();
                    // 유저가 속한 채팅방 정보 삭제
                    result.put(makePath(USER_JOINED, userKey, chatRoomKey), null);
                }
                ref.updateChildren(result, (error, rf) -> {
                    if(error == null) {
                        listener.eventListener();
                    } else {
                        Log.e("FRD", "Can not delete data: " + error);
                    }
                });
            } else {
                Log.e("FRD", "Can not get users");
            }
        });
    }

    @NonNull
    private static String makePath(@NonNull String... strings) {
        StringBuilder ret = new StringBuilder();
        for (String str : strings) {
            ret.append("/").append(str);
        }

        return ret.toString();
    }

    private static void registerEventListener(int key, String path, ChildEventListener listener) {
        if(!childEventListeners.containsKey(key)) {
            childEventListeners.put(key, new ArrayList<>());
        }
        childEventListeners.get(key).add(new Pair<>(path, listener));
    }
    private static void registerEventListener(int key, String path, ValueEventListener listener) {
        if(!valueEventListeners.containsKey(key)) {
            valueEventListeners.put(key, new ArrayList<>());
        }
        valueEventListeners.get(key).add(new Pair<>(path, listener));
    }
    public static void removeEventListener(int hashCode) {
        if(childEventListeners.containsKey(hashCode)) {
            for (Pair<String, ChildEventListener> i : childEventListeners.get(hashCode)) {
                ref.child(i.first).removeEventListener(i.second);
            }
            childEventListeners.get(hashCode).clear();
        }
        if(valueEventListeners.containsKey(hashCode)) {
            for (Pair<String, ValueEventListener> i : valueEventListeners.get(hashCode)) {
                ref.child(i.first).removeEventListener(i.second);
            }
            valueEventListeners.get(hashCode).clear();
        }
    }
}
