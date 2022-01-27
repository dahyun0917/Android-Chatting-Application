package com.example.chat_de;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityJoinUserBinding;
import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.User;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinUserListActivity extends AppCompatActivity {
    private ActivityJoinUserBinding binding;
    private AUser userOther = new User();
    private AUser userMe;
    private HashMap<String, ChatRoomUser> userList = new HashMap<>();
    private ArrayList<AUser> joinUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityJoinUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        //선택한 사용자 정보 받아옴
        //userOther = (User)getIntent().getSerializableExtra("userlist");
        //로그인된 사용자 정보 받아옴
        userMe = ChatDB.getCurrentUser();

        /*userList.add(userMe);
        userList.add(userOther);*/
        userList = (HashMap<String, ChatRoomUser>)getIntent().getSerializableExtra("userList");


        joinUser.add(userList.get(userMe.getUserKey()).userMeta());
        for (ChatRoomUser e : userList.values()) {
            if(e.getExist() && !e.getUserKey().equals(userMe.getUserKey())) {
                joinUser.add(e);
            }
        }

        JoinUserListAdapter joinUserListAdapter;
        joinUserListAdapter = new JoinUserListAdapter(joinUser);
        binding.listview.setAdapter(joinUserListAdapter);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userOther = joinUser.get(i);
                //current user를 제외한 다른 사용자를 클릭 시 일대일 채팅을 할 수 있도록 설정
                if(!userMe.getUserKey().equals(userOther.getUserKey())) {
                    userProFile();
                }
            }
        });
    }

    private void userProFile(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        //선택한 사용자 정보 전송
        intent.putExtra("userOther", userOther);
        startActivity(intent);
    }
}
