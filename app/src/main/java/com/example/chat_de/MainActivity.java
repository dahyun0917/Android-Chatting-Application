package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chat_de.datas.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("pre_1");

        myRef.child("chatRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatRoom newChatRoom = snapshot.getValue(ChatRoom.class);
                myRef.child("chatRoom4").setValue(newChatRoom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomMeta(new ChatRoomMeta("test",2, "byUser"));
        HashMap<String, Chat> chats = new HashMap<>();
        chats.put("123", new Chat("Hi", 0, 1, "user1", "Text"));
        chats.put("124", new Chat("Hello", 1, 2, "user1", "Text"));
        chats.put("125", new Chat("Whoo", 2, 3, "user1", "Text"));
        chatRoom.setChats(chats);
        HashMap<String, ChatRoomUser> users = new HashMap<>();
        users.put("user1", new ChatRoomUser(2));
        users.put("user2", new ChatRoomUser(0));
        chatRoom.setUsers(users);
        myRef.child("chatRoom").setValue(chatRoom);
    }
}