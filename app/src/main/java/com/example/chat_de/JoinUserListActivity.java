package com.example.chat_de;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityJoinUserBinding;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.User;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinUserListActivity extends AppCompatActivity {
    private ActivityJoinUserBinding binding;
    private Dialog dialog;
    private User userOther = new User();
    private User userMe;
    private HashMap<String, ChatRoomUser> userList = new HashMap<>();
    private ArrayList<User> joinUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityJoinUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        //선택한 사용자 정보 전송
        //userOther = (User)getIntent().getSerializableExtra("userlist");
        //로그인된 사용자 정보 전송
        userMe = (User)getIntent().getSerializableExtra("userMe");

        /*userList.add(userMe);
        userList.add(userOther);*/
        userList = (HashMap<String,ChatRoomUser>)getIntent().getSerializableExtra("userlist");


        joinUser.add(userList.get(userMe.getUserKey()).userMeta());
        for (String i : userList.keySet()) {
            if(!userList.get(i).userMeta().getUserKey().equals(userMe.getUserKey()))
                joinUser.add(userList.get(i).userMeta());


        }

        JoinUserListAdapter joinUserListAdapter;
        joinUserListAdapter = new JoinUserListAdapter(joinUser,userMe);
        binding.listview.setAdapter(joinUserListAdapter);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userOther=joinUser.get(i);
                //current user를 제외한 다른 사용자을 클릭 시 일대일 채팅을 할 수 있도록 설정
                if(!userMe.userMeta().getUserKey().equals(userOther.userMeta().getUserKey())) {
                    userProFile();
                }
            }
        });
    }

    private void userProFile(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        //선택한 사용자 정보 전송
        intent.putExtra("userOther", userOther);
        //로그인된 사용자 정보 전송
        intent.putExtra("userMe", userMe);
        startActivity(intent);
    }



}
