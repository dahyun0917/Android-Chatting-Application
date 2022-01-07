package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chat_de.datas.Chat;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private final int SYSTEM_MESSAGE = -2;

    //private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private ImageButton file_send;
    private RecyclerView recyclerView;
    //private int GALLEY_CODE = 10;
    private String chatRoomKey;
    private String userKey = "user2";
    private Chat.Type messageType = Chat.Type.TEXT;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ArrayList<Chat> dataList;
    private int index=-1;

    Uri filePath;
    ImageView ivPreview;
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

        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("채팅방") ;

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
                StorageReference rootRef = storage.getReference();
                gallery_access();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageList(10);
        ChatDB.readLatestMessage(chatRoomKey, userKey);
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

        getChatRoomMeta();
    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayList<Chat> adapter) {
        Chat dataItem = dataSnapshot.getValue(Chat.class);
        if(dataItem.getIndex() != SYSTEM_MESSAGE)
            index = dataItem.getIndex();
        adapter.add(new Chat(dataItem));
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayList<Chat> adapter) {
        Chat dataItem = dataSnapshot.getValue(Chat.class);
        adapter.remove(new Chat(dataItem));
    }

    private void getChatRoomMeta() { //채팅방 정보 불러옴
        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        DatabaseReference ref = ChatDB.getReference();
        ref.child(ChatDB.CHAT_ROOMS).child(chatRoomKey).child(ChatDB.CHATS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot, dataList);
                recyclerView.scrollToPosition(dataList.size() - 1);
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

        // USER_NAME 나중에 userKey로 바꿔줘야함
        ChatDB.uploadMessage(chat_edit.getText().toString(), ++index, messageType, chatRoomKey, userKey);
        chat_edit.setText(""); //입력창 초기화
    }
    private void sendMessageToF(){
        Chat chat = new Chat(chat_edit.getText().toString(), index,"user2", Chat.Type.TEXT);
        databaseReference.child("chats").push().setValue(chat); // 데이터 푸쉬
    }

    private void inviteUser(){
        Intent intent = new Intent(this, ChatUserListAcitivity.class);
        intent.putExtra("tag",2);
        intent.putExtra("who","user1");  //todo : userkey를 전달해야함
        intent.putExtra("where","chatRoom1"); //todo :  chatRoomkey를 전달해야함
        startActivity(intent);
    }

    private void gallery_access(){
        //갤러리만
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent,10);*/
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 10);

        //드롭박스, 구글드라이브, 갤러리 등 모든 파일
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);*/
    }
    //사진 고른 후
    //로컬 파일에서 업로드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//            if(requestCode == 0 && resultCode == RESULT_OK){
        if(requestCode == 10&&resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("TAG", "uri:" + String.valueOf(filePath));
            if(filePath!=null)
                uploadFile();
            /*Glide.with(this).load(filePath).into(iv);
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        }

    public void uploadFile() {
        //firebase storage에 업로드하기

        //1. FirebaseStorage을 관리하는 객체 얻어오기
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        //2. 업로드할 파일의 node를 참조하는 객체
        //파일 명이 중복되지 않도록 날짜를 이용
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String filename = sdf.format(new Date()) + ".jpg";//현재 시간으로 파일명 지정 20191023142634
        //원래 확장자는 파일의 실제 확장자를 얻어와서 사용해야함. 그러려면 이미지의 절대 주소를 구해야함.

        StorageReference imgRef = firebaseStorage.getReference("uploads/" + filename);
        //uploads라는 폴더가 없으면 자동 생성

        //참조 객체를 통해 이미지 파일 업로드
        //업로드 결과를 받고 싶다면..
        UploadTask uploadTask = imgRef.putFile(filePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChatActivity.this, "success upload", Toast.LENGTH_SHORT).show();
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "failed upload", Toast.LENGTH_SHORT).show();
            }
        });
        /*Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });*/
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("hihi",String.valueOf(uri));
                index=index+1;
                Chat chat = new Chat(uri.toString(), index,"user2", Chat.Type.IMAGE);
                databaseReference.child("chats").push().setValue(chat); // 데이터 푸쉬
            }
        });
    }

}
