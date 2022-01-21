package com.example.chat_de;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chat_de.databinding.ActivityChatRoomListBinding;


public class ChatRoomListActivity extends AppCompatActivity {
    ChatRoomListFragment mainFragment;
    private final String USER_KEY = "user2";
    private ActivityChatRoomListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatDB.setReference("pre_1", USER_KEY);
        binding = ActivityChatRoomListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomListActivity.this, ChatRoomListActivity2.class);
                ChatMode.groupMode();
                startActivity(intent);
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
