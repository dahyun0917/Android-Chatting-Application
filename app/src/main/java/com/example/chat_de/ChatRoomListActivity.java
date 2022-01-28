package com.example.chat_de;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityChatRoomListBinding;

public class ChatRoomListActivity extends AppCompatActivity {
    ChatRoomListFragment mainFragment;
    private final String USER_KEY = "user2";
    private ActivityChatRoomListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomListBinding.inflate(getLayoutInflater());

        if(ChatDB.getChatMode() == 0) {
            binding.textMode.setVisibility(View.GONE);
        }
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbaTitle.setOnClickListener(view1 -> {
            Intent intent = new Intent(ChatRoomListActivity.this, ChatRoomListActivity.class);
            ChatDB.changeRef();
            startActivity(intent);
            finish();
        });

        setSupportActionBar(binding.toolbarChatRoomList);
        setTitle("");

        mainFragment = new ChatRoomListFragment();


        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수

        mainFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainFragment).commit();
    }
}