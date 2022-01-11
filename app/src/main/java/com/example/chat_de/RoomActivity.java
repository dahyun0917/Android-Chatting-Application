package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;

public class RoomActivity extends AppCompatActivity {
    private final int SYSTEM_MESSAGE = -2;
    private ActivityRoomBinding binding;
    //private ListView chat_view;

    private int GALLEY_CODE = 10;
    private String chatRoomKey;
    private String userKey = "user2";
    private String userName = "user2";
    private Chat.Type messageType = Chat.Type.TEXT;

    private ArrayList<String> listenerPath = new ArrayList<>();

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private ArrayList<Chat> dataList;

    private HashMap<String, ChatRoomUser> userList;

    private int index=-1;

    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        chatRoomKey = getIntent().getStringExtra("chatRoomKey");

        //리사이클러뷰에 일부 데이터를 저장 후 화면에 띄우기
        binding.RecyclerView.setItemViewCacheSize(50);

        //액션바 타이틀 바 이름 설정
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle(chatRoomKey.toString()) ;

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        binding.chatSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //파일 이미지 버튼에 대한 클릭 리스너 지정
        binding.fileSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StorageReference rootRef = firebaseStorage.getReference();
                gallery_access();
            }
        });
        User test1 = new User("양선아","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",81,"user1");
        User test2 = new User("이다현","https://www.codingfactory.net/wp-content/uploads/abc.jpg",81,"user2");
        User test3 = new User("김규래","https://www.codingfactory.net/wp-content/uploads/abc.jpg",81,"user3");
        ChatRoomUser usertest1= new ChatRoomUser(1,test1);
        ChatRoomUser usertest2= new ChatRoomUser(2,test2);
        ChatRoomUser usertest3= new ChatRoomUser(2,test3);
        userList= new HashMap<String,ChatRoomUser>(){{
            put("user1",usertest1);
            put("user2",usertest2);
            put("user3",usertest3);
        }};
    }

    @Override
    public void onResume() {
        super.onResume();
        //메시지가 새로 올라올 때마다 동작하는 리스너 설정
        ChatDB.messageAddEventListener(chatRoomKey, item -> {
            addMessage(item, dataList);
            binding.RecyclerView.scrollToPosition(dataList.size() - 1);
            binding.RecyclerView.setAdapter(new RoomElementAdapter(dataList,userList));
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
            case R.id.user_list:
                showjoinuserlist();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showjoinuserlist(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1);
        AlertDialog.Builder dlg = new AlertDialog.Builder(RoomActivity.this);
        dlg.setTitle("참가자"); //제목
        for(String i : userList.keySet()){
            adapter.add(userList.get(i).getUserMeta().getName());
        }
        dlg.setAdapter(adapter,null);
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }
    //max에 지정된 개수까지 메세지 내용을 불러옴
    private void getMessageList(int max){
        dataList = new ArrayList<Chat>();
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.RecyclerView.setLayoutManager(manager);
        binding.RecyclerView.scrollToPosition(dataList.size()-1);
        binding.RecyclerView.setAdapter(new RoomElementAdapter(dataList,userList));
    }

    private void addMessage(Chat dataItem, ArrayList<Chat> adapter) {
        //이전 메시지와 비교해서 날짜가 달라지면 시스템 메시지로 현재 날짜를 추가해주는 부분
        ListIterator i = adapter.listIterator(adapter.size());
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
        final String DAY = SDF.format(dataItem.normalDate());
        while(i.hasPrevious()) {
            final Chat chat = (Chat)i.previous();
            //시스템 메시지가 아닐때만 비교
            if(chat.getType() != Chat.Type.SYSTEM) {
                if (!SDF.format(chat.normalDate()).equals(DAY)) {
                    Chat daySystemChat = new Chat("--------------------------"+DAY+"--------------------------", SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                    daySystemChat.setDate(dataItem.unixTime());
                    adapter.add(daySystemChat);
                }
                break;
            }
        }

        if(dataItem.getType() != Chat.Type.SYSTEM)
            index = dataItem.getIndex();

        adapter.add(new Chat(dataItem));
    }

    private void getChatRoomMeta() { //채팅방 정보 불러옴

    }

    //메세지를 보내고 메세지 내용 파이어베이스에 저장
    private void sendMessage(){
        if (binding.chatEdit.getText().toString().equals(""))
            return;

        // USER_NAME 나중에 userKey로 바꿔줘야함
        ChatDB.uploadMessage(binding.chatEdit.getText().toString(), ++index, messageType, chatRoomKey, userKey);
        binding.chatEdit.setText(""); //입력창 초기화
    }
    //유저추가 액티비티로 보낼 데이터 저장 후 intent
    private void inviteUser(){
        Intent intent = new Intent(this, UserListActivity.class);
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
                //Toast.makeText(RoomActivity.this, "success upload", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RoomActivity.this, "upload 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
