package com.example.chat_de;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityUserProfileBinding;
import com.example.chat_de.datas.AUser;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;
    private AUser userOther;
    private AUser userMe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        //선택한 사용자 정보 전송
        userOther = (AUser)getIntent().getSerializableExtra("userOther");
        //로그인된 사용자 정보 전송
        userMe = ChatDB.getCurrentUser();


        binding.makeChat.setVisibility(View.VISIBLE);
        //프로필 사진 설정
        Glide
                .with(this)
                .load(userOther.getPictureURL())
                .error(R.drawable.knu_mark_white)
                .into(binding.profileImage);
        //이름 설정
        binding.userName.setText(userOther.getName());

        if(userMe.getUserKey().equals(userOther.getUserKey()))
            binding.makeChat.setVisibility(View.GONE);
        //채팅방 만들기 버튼 설정
        binding.makeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<AUser> userList = new ArrayList<>();
                userList.add(userMe);
                userList.add(userOther);
                String chatRoomName = userMe.getName() + ", " + userOther.getName();
                ChatDB.setChatRoomCompleteListener(chatRoomName, userList, userMe, chatRoomKey -> {
                    Intent chat = new Intent(UserProfileActivity.this, RoomActivity.class);
                    chat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    chat.putExtra("chatRoomKey",chatRoomKey);
                    startActivity(chat);
                });
            }
        });
    }
}
