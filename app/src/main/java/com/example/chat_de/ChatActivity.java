package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chat_de.datas.Chat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private final int GALLEY_CODE = 10;
    private final int SYSTEM_MESSAGE = -2;

    //private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private ImageButton file_send;
    private RecyclerView recyclerView;
    private String chatRoomKey;
    private String userKey = "user2";
    private Chat.Type messageType = Chat.Type.TEXT;

    private ArrayList<String> listenerPath = new ArrayList<>();

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ArrayList<Chat> dataList;
    private int index=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatRoomKey = getIntent().getStringExtra("chatRoomKey");
        setContentView(R.layout.chat);
        recyclerView=findViewById(R.id.RecyclerView);

        chat_edit =  findViewById(R.id.chat_edit);
        chat_send =  findViewById(R.id.chat_sent);
        file_send =  findViewById(R.id.file_send);
//        add_Button=(Button)findViewById(R.id.add_button);

        //Intent intent = getIntent();
        //CHAT_NAME = intent.getStringExtra("chat_name");
        //USER_NAME = intent.getStringExtra("user_name");

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //파일 이미지 버튼에 대한 클릭 리스너 지정
        file_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendImageMessage();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //메시지가 새로 올라올 때마다 동작하는 리스너 설정
        listenerPath.add(ChatDB.messageAddEventListener(chatRoomKey, item -> {
            addMessage(item, dataList);
            recyclerView.scrollToPosition(dataList.size() - 1);
            recyclerView.setAdapter(new Adapter(dataList));
        }));
        getMessageList(10);
        ChatDB.readLatestMessage(chatRoomKey, userKey);
    }

    @Override
    public void onPause() {
        super.onPause();
        for(String path : listenerPath)
            ChatDB.removeEventListener(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_chatting_addfr,menu);
        return true;
    }

    //유저 추가 메뉴바 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.add_fr:
                inviteUser();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //max에 지정된 개수까지 메세지 내용을 불러옴
    private void getMessageList(int max){
        dataList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.scrollToPosition(dataList.size()-1);
        recyclerView.setAdapter(new Adapter(dataList));
    }

    private void addMessage(Chat dataItem, ArrayList<Chat> adapter) {
        if(dataItem.getIndex() != SYSTEM_MESSAGE)
            index = dataItem.getIndex();
        adapter.add(new Chat(dataItem));
    }

    private void getChatRoomMeta() { //채팅방 정보 불러옴

    }

    private void sendMessage(){
        if (chat_edit.getText().toString().equals(""))
            return;

        // USER_NAME 나중에 userKey로 바꿔줘야함
        ChatDB.uploadMessage(chat_edit.getText().toString(), ++index, messageType, chatRoomKey, userKey);
        chat_edit.setText(""); //입력창 초기화
    }

    private void inviteUser(){
        Intent intent = new Intent(this, ChatUserListAcitivity.class);
        startActivity(intent);
    }

    private void sendImageMessage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent,GALLEY_CODE);
    }
}
