package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
                FirebaseDataStructure.ChatRoom newChatRoom = snapshot.getValue(FirebaseDataStructure.ChatRoom.class);
                myRef.child("chatRoom4").setValue(newChatRoom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDataStructure.ChatRoom chatRoom = new FirebaseDataStructure.ChatRoom();
        chatRoom.setChatRoomMeta(new FirebaseDataStructure.ChatRoomMeta("test",2, "byUser"));
        HashMap<String, FirebaseDataStructure.Chat> chats = new HashMap<>();
        chats.put("123", new FirebaseDataStructure.Chat("Hi", 0, 1, "user1", "Text"));
        chats.put("124", new FirebaseDataStructure.Chat("Hello", 1, 2, "user1", "Text"));
        chats.put("125", new FirebaseDataStructure.Chat("Whoo", 2, 3, "user1", "Text"));
        chatRoom.setMessages(chats);
        HashMap<String, FirebaseDataStructure.ChatRoomUser> users = new HashMap<>();
        users.put("user1", new FirebaseDataStructure.ChatRoomUser(2));
        users.put("user2", new FirebaseDataStructure.ChatRoomUser(0));
        chatRoom.setUsers(users);
        myRef.child("chatRoom").setValue(chatRoom);
    }
}