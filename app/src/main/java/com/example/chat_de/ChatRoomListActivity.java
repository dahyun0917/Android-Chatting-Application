package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.chat_de.databinding.ActivityChatRoomListBinding;

import java.util.HashSet;

public class ChatRoomListActivity extends AppCompatActivity {
    ChatRoomListFragment mainFragment;
    //private String CHAT_NAME;
    private final String USER_KEY = "user2";
    private String userKey;
    private ActivityChatRoomListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatDB.setReference("pre_3", USER_KEY);
        binding = ActivityChatRoomListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarChatRoomList);
        setTitle("");

        userKey = ChatDB.getCurrentUserKey();

        mainFragment = new ChatRoomListFragment();


        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수

        mainFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainFragment).commit();

    }
    //현재 액티비티의 메뉴바를 메뉴바.xml과 붙이기
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_chat_list_add, menu);
        return true;
    }
    //유저 추가 메뉴바 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.add_chat:
                selelctUser();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void selelctUser(){
        //RoomActivity로 넘어간 뒤, 종료
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("tag",1);
        HashSet<String> set = new HashSet<>();
        set.add(userKey);
        intent.putExtra("userList", set);
        startActivity(intent);
    }
}
