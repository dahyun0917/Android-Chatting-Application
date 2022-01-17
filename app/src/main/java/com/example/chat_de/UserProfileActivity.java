package com.example.chat_de;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityUserProfileBinding;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.User;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    private Dialog dialog;
    private ChatRoomUser userOther;
    private ChatRoomUser userMe;
    ArrayList<ChatRoomUser> userList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        //인텐트 설정
        Intent getIntent = getIntent();

        //선택한 사용자 정보 전송
        userOther = new ChatRoomUser();
        userOther.setUserMeta(new User());
        userOther.getUserMeta().setName(getIntent.getStringExtra("otherName"));
        userOther.getUserMeta().setPictureURL(getIntent.getStringExtra("otherPictureURL"));
        userOther.getUserMeta().setGeneration(getIntent.getIntExtra("otherGeneration",0));
        userOther.getUserMeta().setUserKey(getIntent.getStringExtra("otherUserKey"));
        userOther.setLastReadIndex(getIntent.getIntExtra("otherLastReadIndex",0));
        //로그인된 사용자 정보 전송
        userMe = new ChatRoomUser();
        userMe.setUserMeta(new User());
        userMe.setLastReadIndex(getIntent.getIntExtra("myLastReadIndex",0));
        userMe.getUserMeta().setName(getIntent.getStringExtra("myName"));
        userMe.getUserMeta().setPictureURL(getIntent.getStringExtra("myPictureURL"));
        userMe.getUserMeta().setGeneration(getIntent.getIntExtra("myGeneration",0));
        userMe.getUserMeta().setUserKey(getIntent.getStringExtra("myUserKey"));

        userList.add(userMe);
        userList.add(userOther);

        //프로필 사진 설정
        Glide
                .with(this)
                .load(userOther.takePictureURL())
                .into(binding.profileImage);
        //이름 설정
        binding.userName.setText(userOther.takeName());
        //채팅방 만들기 버튼 설정
        binding.makeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatDB.setPersonalChatRoom(userMe, userOther, chatRoomKey -> {
                    Intent chat = new Intent(UserProfileActivity.this, RoomActivity.class);
                    chat.putExtra("chatRoomKey",chatRoomKey);
                    startActivity(chat);
                });
                finish();
            }
        });
    }
}
