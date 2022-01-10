package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chat_de.datas.Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private int GALLEY_CODE = 10;
    private String chatRoomKey;
    private String userKey = "user2";
    private String userName = "user2";
    private Chat.Type messageType = Chat.Type.TEXT;

    private ArrayList<String> listenerPath = new ArrayList<>();

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

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

        //리사이클러뷰에 일부 데이터를 저장 후 화면에 띄우기
        recyclerView.setItemViewCacheSize(10);

        chat_edit =  findViewById(R.id.chat_edit);
        chat_send =  findViewById(R.id.chat_sent);
        file_send =  findViewById(R.id.file_send);

        //액션바 타이틀 바 이름 설정
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle(chatRoomKey.toString()) ;

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
                StorageReference rootRef = firebaseStorage.getReference();
                gallery_access();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //메시지가 새로 올라올 때마다 동작하는 리스너 설정
        ChatDB.messageAddEventListener(chatRoomKey, item -> {
            addMessage(item, dataList);
            recyclerView.scrollToPosition(dataList.size() - 1);
            recyclerView.setAdapter(new Adapter(dataList));
        });
        getMessageList(10);
        ChatDB.userReadLatestMessage(chatRoomKey, userKey);
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatDB.removeEventListenerBindOnThis();
    }

    //현재 액티비티의 메뉴바를 메뉴바.xml과 붙이기
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_chatting_addfr, menu);
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

    //메세지를 보내고 메세지 내용 파이어베이스에 저장
    private void sendMessage(){
        if (chat_edit.getText().toString().equals(""))
            return;

        // USER_NAME 나중에 userKey로 바꿔줘야함
        ChatDB.uploadMessage(chat_edit.getText().toString(), ++index, messageType, chatRoomKey, userKey);
        chat_edit.setText(""); //입력창 초기화
    }
    //유저추가 액티비티로 보낼 데이터 저장 후 intent
    private void inviteUser(){
        Intent intent = new Intent(this, ChatUserListAcitivity.class);
        intent.putExtra("tag",2);
        intent.putExtra("who", userName);
        intent.putExtra("where", chatRoomKey);
        startActivity(intent);
    }
    //사용자 갤러리로 접근
    private void gallery_access(){

        //갤러리만
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent,10);*/

        //드롭박스, 구글드라이브, 갤러리 등 모든 파일
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), GALLEY_CODE);


    }

    //갤러리 액티비티에서 결과값을 제대로 받았는지 확인
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLEY_CODE &&resultCode == RESULT_OK){
            filePath = data.getData();
            //Log.d("TAG", "uri:" + String.valueOf(filePath));
            if(filePath!=null)
                uploadFile();
        }
    }

    //firebase storage에 업로드하기
    public void uploadFile() {

        //파일 명이 중복되지 않도록 날짜를 이용 (현재시간 + 사용자 키)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");
        String filename = sdf.format(new Date()) + "_" + userKey + ".jpg";

        //uploads라는 폴더가 없으면 자동 생성
        //chatroom key로 폴더명을 바꾸는 것이 좋을 것으로 생각
        StorageReference imgRef = firebaseStorage.getReference("uploads/" + filename);

        //이미지 파일 업로드
        UploadTask uploadTask = imgRef.putFile(filePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(ChatActivity.this, "success upload", Toast.LENGTH_SHORT).show();
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //userKey="user2";
                        ChatDB.uploadMessage(uri.toString(), ++index, Chat.Type.IMAGE, chatRoomKey, userKey);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "upload 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
