package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.chat_de.datas.Chat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    //private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private ImageButton file_send;
    private RecyclerView recyclerView;
    private int GALLEY_CODE = 10;

    private FirebaseStorage storage=FirebaseStorage.getInstance();;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private ArrayList<Chat> dataList;
    private int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        recyclerView=findViewById(R.id.RecyclerView);
        chat_edit =  findViewById(R.id.chat_edit);
        chat_send =  findViewById(R.id.chat_sent);
        file_send =  findViewById(R.id.file_send);
//        add_Button=(Button)findViewById(R.id.add_button);

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

        /*add_Button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                inviteUser();
            }
        });*/

        //파일 이미지 버튼에 대한 클릭 리스너 지정
        file_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendimageMessage();
            }
        });

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

        getChatRoomMeta("D");
    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayList<Chat> adapter) {
        Chat dataItem = dataSnapshot.getValue(Chat.class);
        Chat item = new Chat(dataItem.getText(), dataItem.getIndex(), dataItem.getFrom(), dataItem.getType());
        item.setDate(dataItem.getDate());
        index = dataItem.getIndex();
        adapter.add(item);
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayList<Chat> adapter) {
        Chat dataItem = dataSnapshot.getValue(Chat.class);
        Chat item = new Chat(dataItem.getText(), dataItem.getIndex(), dataItem.getFrom(), dataItem.getType());
        item.setDate(dataItem.getDate());
        adapter.remove(dataItem);
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
        //Date today=new Date();

        Chat chat = new Chat( chat_edit.getText().toString(),index,"user2",Chat.Type.TEXT); //ChatDTO를 이용하여 데이터를 묶는다.
        databaseReference.child("pre_1").child("chatRooms").child("chatRoom1").child("chats").push().setValue(chat); // 데이터 푸쉬
    }

    private void inviteUser(){
        Intent intent = new Intent(this, ChatUserListAcitivity.class);
        startActivity(intent);
        finish();
    }

    private void sendimageMessage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent,GALLEY_CODE);
     }
}
