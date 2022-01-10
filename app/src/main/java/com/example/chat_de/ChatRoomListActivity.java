package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ChatRoomListActivity extends AppCompatActivity {
    ChatRoomListFragment mainFragment;

    //private String CHAT_NAME;
    //private String USER_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatDB.setReference("pre_2");
        setContentView(R.layout.activity_chat_room_list);

        mainFragment = new ChatRoomListFragment();
        Intent intent = getIntent();
        //CHAT_NAME = intent.getStringExtra("chatName");
        //USER_NAME = intent.getStringExtra("userName");

        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수
        //bundle.putString("chat_name", CHAT_NAME);
        //bundle.putString("user_name", USER_NAME);
        mainFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainFragment).commit();

    }
}
