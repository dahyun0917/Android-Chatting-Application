package com.example.chat_de;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityChatRoomListBinding;

public class ChatRoomListActivity extends AppCompatActivity {
    ChatRoomListFragment mainFragment;
    private ActivityChatRoomListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(!ChatDB.isGenAccessPossible()) { //003
            binding.textMode.setVisibility(View.GONE);
            binding.upImage.setVisibility(View.GONE);
            binding.downImage.setVisibility(View.GONE);
            binding.toolbarChatRoomList.setClickable(false);
        }
        else {
            binding.toolbaTitle.setOnClickListener(view1 -> {
                Intent intent = new Intent(ChatRoomListActivity.this, ChatRoomListActivity.class);
                ChatDB.changeRef();
                startActivity(intent);
                finish();
            });
            if (ChatDB.getChatMode() == 0) { //전체모드
                binding.ampLogo.setVisibility(View.GONE);
                binding.generationNumber.setVisibility(View.GONE);
                binding.textMode.setVisibility(View.GONE);
                binding.downImage.setVisibility(View.GONE);
            }
            else {
                binding.knuLogo.setVisibility(View.GONE);
                binding.generationNumber.setText(String.valueOf(ChatDB.getChatMode()));
                binding.upImage.setVisibility(View.GONE);
            }
        }

        setSupportActionBar(binding.toolbarChatRoomList);
        setTitle("");

        mainFragment = new ChatRoomListFragment();
        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수

        mainFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainFragment).commit();
    }
}
