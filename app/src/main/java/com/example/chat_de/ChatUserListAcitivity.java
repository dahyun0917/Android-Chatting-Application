package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class ChatUserListAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);
    }

    private ArrayList<FirebaseDataStructure.User> getAllUserList(){
        ArrayList<FirebaseDataStructure.User> userList = new ArrayList<>();
        return userList;
    }

    private void showUserList(ArrayList<FirebaseDataStructure.User> userList){

    }

    private void createChatRoom(){}

    private void createChatRoomToF(){}

    private void updateChatRoom(){}

    private void updateChatRoomToF(){}
}
