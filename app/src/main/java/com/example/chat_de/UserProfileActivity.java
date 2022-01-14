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

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    private Dialog dialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        //인텐트 설정
        Intent getIntent = getIntent();
        String imageSrc = getIntent.getStringExtra("image");
        String userMe = getIntent.getStringExtra("me");
        String userName = getIntent.getStringExtra("name");
        ArrayList<UserListItem> userList = new ArrayList<>();
        //프로필 사진 설정
        Glide
                .with(this)
                .load(imageSrc)
                .into(binding.profileImage);
        //이름 설정
        binding.userName.setText(userName);
        //채팅방 만들기 버튼 설정
        binding.makeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : intent로 사용자 객체 받게 되면 그때 수정
                /*ChatDB.setChatRoom(userMe + ", "+userName, userList, userMe,chatRoomKey -> {
                    Intent chat = new Intent(UserProfileActivity.this, RoomActivity.class);
                    chat.putExtra("chatRoomKey",chatRoomKey);
                    startActivity(chat);
                });*/
                finish();
            }
        });
    }
}
