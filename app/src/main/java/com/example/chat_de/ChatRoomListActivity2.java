package com.example.chat_de;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityChatRoomList2Binding;
import com.example.chat_de.databinding.ActivityChatRoomListBinding;


public class ChatRoomListActivity2 extends AppCompatActivity {
    ChatRoomListFragment mainFragment;
    private final String USER_KEY = "user2";
    private ActivityChatRoomList2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatDB.setReference("pre_3", USER_KEY);
        binding = ActivityChatRoomList2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomListActivity2.this, ChatRoomListActivity.class);
                startActivity(intent);
                ChatMode.generalMode();
                finish();
            }
        });

        setSupportActionBar(binding.toolbarChatRoomList);
        setTitle("");


        mainFragment = new ChatRoomListFragment();


        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수

        mainFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainFragment).commit();

    }

}
