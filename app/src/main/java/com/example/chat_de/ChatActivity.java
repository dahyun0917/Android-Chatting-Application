package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.chat_de.datas.Chat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    //private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private RecyclerView recyclerView;
    private Button add_Button;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private ArrayList<Chat> dataList;
    private int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        recyclerView=findViewById(R.id.RecyclerView);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);
        add_Button=(Button)findViewById(R.id.add_button);

        //Intent intent = getIntent();
        //CHAT_NAME = intent.getStringExtra("chat_name");
        //USER_NAME = intent.getStringExtra("user_name");

        getMessageList(10);

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        add_Button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                inviteUser();
            }
        });

    }

    private void getMessageList(int max){//max에 지정된 개수까지 메세지 내용을 불러옴
        dataList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.scrollToPosition(dataList.size()-1);
        recyclerView.setAdapter(new Adapter(dataList));

        getChatRoomMeta("D");
    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayList<Chat> adapter) {
        Chat DataItem = dataSnapshot.getValue(Chat.class);
        index=DataItem.getIndex();
        adapter.add(new Chat(DataItem.getText(),DataItem.getIndex(), DataItem.getDate(), DataItem.getFrom(),DataItem.getType()));
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayList<Chat> adapter) {
        Chat DataItem = dataSnapshot.getValue(Chat.class);
        adapter.remove(new Chat(DataItem.getText(),DataItem.getIndex(), DataItem.getDate(), DataItem.getFrom(),DataItem.getType()));
    }

    private void getChatRoomMeta(String chatRoomKey){//chatRoomKey에 따른 채팅방 정보 불러옴
    // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("pre_1").child("chatRooms").child("chatRoom1").child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot, dataList);
                recyclerView.scrollToPosition(dataList.size()-1);
                recyclerView.setAdapter(new Adapter(dataList));
                Log.e("LOG", "s:"+s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMessage(dataSnapshot, dataList);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(){
        if (chat_edit.getText().toString().equals(""))
            return;
        index=index+1;
        sendMessageToF();
        chat_edit.setText(""); //입력창 초기화
    }
    private void sendMessageToF(){
        Chat chat = new Chat( chat_edit.getText().toString(),index,++index,"user2","TEXT"); //ChatDTO를 이용하여 데이터를 묶는다.
        databaseReference.child("pre_1").child("chatRooms").child("chatRoom1").child("chats").push().setValue(chat); // 데이터 푸쉬
    }

    private void inviteUser(){
        Intent intent = new Intent(this, ChatUserListAcitivity.class);
        startActivity(intent);
        finish();
    }
}
