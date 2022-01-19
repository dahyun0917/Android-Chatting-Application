package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ChatRoomListActivity extends AppCompatActivity {
    ChatRoomListFragment mainFragment;

    //private String CHAT_NAME;
    private final String USER_KEY = "user2";
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatDB.setReference("pre_2", USER_KEY);
        setContentView(R.layout.activity_chat_room_list);

        userKey = ChatDB.getCurrentUserKey();

        mainFragment = new ChatRoomListFragment();
        Intent intent = getIntent();
        //CHAT_NAME = intent.getStringExtra("chatName");
        //USER_NAME = intent.getStringExtra("userName");

        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수
        //bundle.putString("chat_name", CHAT_NAME);
        //bundle.putString("user_name", USER_NAME);
        bundle.putString("userKey", userKey);
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
        intent.putExtra("who", userKey);
        startActivity(intent);
    }
}
