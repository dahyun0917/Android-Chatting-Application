package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityCreateRoomMetaBinding;

public class CreateRoomMetaActivity extends AppCompatActivity {
    ActivityCreateRoomMetaBinding binding;
    String chatRoomPictureUrl;
    String chatRoomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateRoomMetaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        chatRoomPictureUrl = "";

        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : gallery access, fire storage upload, and get link
                Glide
                        .with(CreateRoomMetaActivity.this)
                        .load(chatRoomPictureUrl)
                        .circleCrop()
                        .error(R.drawable.knu_mark_white)
                        .into(binding.chatImage);
            }
        });
        binding.createComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoomName = binding.chatNameText.getText().toString();
                Intent finishIntent = new Intent(CreateRoomMetaActivity.this, UserListActivity.class);
                finishIntent.putExtra("chatRoomName",chatRoomName);
                finishIntent.putExtra("chatRoomPicture", chatRoomPictureUrl);
                setResult(9001, finishIntent);
                finish();
            }
        });
    }
}
