package com.example.chat_de;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    private Dialog dialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //getSupportActionBar().hide();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
/*        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(view);

        Intent getIntent = getIntent();
        String imageSrc = getIntent.getStringExtra("image");
        String userName = getIntent.getStringExtra("name");
        Glide
                .with(this)
                .load(imageSrc)
                .into(binding.profileImage);
        binding.userName.setText(userName);
        binding.makeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : 1대1 채팅방 만들기'
                finish();
            }
        });
    }
}
