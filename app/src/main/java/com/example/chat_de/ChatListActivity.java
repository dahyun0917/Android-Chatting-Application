package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatListActivity extends AppCompatActivity {
    ChatListFragment mainFragment;

    //private String CHAT_NAME;
    //private String USER_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_chat);

        mainFragment = new ChatListFragment();
        Intent intent = getIntent();
        //CHAT_NAME = intent.getStringExtra("chatName");
        //USER_NAME = intent.getStringExtra("userName");

        Bundle bundle = new Bundle(); // 파라미터의 숫자는 전달하려는 값의 갯수
        //bundle.putString("chat_name", CHAT_NAME);
        //bundle.putString("user_name", USER_NAME);
        mainFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, mainFragment).commit();
    }
}
