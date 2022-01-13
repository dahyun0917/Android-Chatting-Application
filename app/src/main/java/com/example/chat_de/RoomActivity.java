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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomMeta;
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
    private RoomElementAdapter roomElementAdapter;
    private LinearLayoutManager manager;
    //private ListView chat_view;

    private int GALLEY_CODE = 10;
    private String chatRoomKey;
    private String userKey = "user2";
    private String userName = "user2";
    private Chat.Type messageType = Chat.Type.TEXT;

    private ArrayList<String> listenerPath = new ArrayList<>();

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private ArrayList<Chat> dataList = new ArrayList<>();

    private HashMap<String, ChatRoomUser> userList  = new HashMap<>(); //

    //private HashMap<String,ChatRoomUser> chatRoomUserList;
    private ChatRoom chatRoomUserList;

    private int index=-1;
    private boolean isLoading = false;
    private boolean autoScroll = true;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //화면 기본 설정
        setUpRoomActivity();

/*        //test용 데이터
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
        }};*/
        Chat chat1 = new Chat("hi", 0,"user1", Chat.Type.TEXT);
        Chat chat2 = new Chat("ho", 1,"user2", Chat.Type.TEXT);
        Chat chat3 = new Chat("ha", 2,"user3", Chat.Type.TEXT);
        HashMap<String,Chat> chats1 = new HashMap<String,Chat>(){{
            put("111",chat1);
            put("112",chat2);
            put("113",chat3);
        }};
        ChatRoomMeta chatRoomMeta1 = new ChatRoomMeta("chatRoomTest", ChatRoomMeta.Type.BY_USER);
        chatRoomUserList = new ChatRoom(chats1,chatRoomMeta1);

    }
    public void setUpRoomActivity(){
        //리사이클러뷰 설정
        initRecyclerView();
        //populateData();
        //initScrollListener();

        chatRoomKey = getIntent().getStringExtra("chatRoomKey");

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
                galleryAccess();
            }
        });
 }
    private void initScrollListener() {
        binding.RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if(!recyclerView.canScrollVertically(-1)){
                        loadMore();
                        isLoading = true;
                        autoScroll = false;
                    }
                }
                if(!recyclerView.canScrollVertically(1)){
                    autoScroll = true;
                }
                /*if (!isLoading) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == dataList.size() - 1) {
                        //리스트 마지막
                        loadMore();
                        //isLoading = true;
                    }
                }*/
            }
        });
    }

    private void loadMore() {
        binding.RecyclerView.post(new Runnable() {
            public void run() {
                dataList.add(null);
                roomElementAdapter.notifyItemInserted(dataList.size() - 1);
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataList.remove(dataList.size() - 1);
                int scrollPosition = dataList.size();
                roomElementAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    dataList.add(new Chat("ww",0L, currentSize,"user1", Chat.Type.TEXT));
                    currentSize++;
                }

                roomElementAdapter.notifyDataSetChanged();
                isLoading = false;
                autoScroll = false;
            }
        }, 1000);
    }
    @Override
    public void onResume() {
        super.onResume();
        //메시지가 새로 올라올 때마다 동작하는 리스너 설정

        dataList = new ArrayList<>();
        userList = new HashMap<>();
        ChatDB.getChatRoomUserListCompleteListener(chatRoomKey, item -> {
            userList = item;
            ChatDB.messageAddedEventListener(chatRoomKey, this::floatMessage);
            ChatDB.userListChangedEventListener(chatRoomKey, userPair -> {
                userList.put(userPair.first, userPair.second);
            });
        });
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
                showJoinedUserList();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initRecyclerView(){
        //binding.RecyclerView.setItemViewCacheSize(50);
        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        binding.RecyclerView.setLayoutManager(manager);
        roomElementAdapter = new RoomElementAdapter(dataList,userList);
        binding.RecyclerView.setAdapter(roomElementAdapter);
    }

    private void showJoinedUserList(){
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

    //파이어베이스에 메세지가 추가되었을때, 메세지를 화면에 띄워줌.(기존 addMessage)
    private void floatMessage(Chat dataItem) {
        //이전 메시지와 비교해서 날짜가 달라지면 시스템 메시지로 현재 날짜를 추가해주는 부분
        ListIterator i = dataList.listIterator(dataList.size());
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
        final String DAY = SDF.format(dataItem.normalDate());
        while(i.hasPrevious()) {
            final Chat chat = (Chat)i.previous();
            //시스템 메시지가 아닐때만 비교
            if(chat.getType() != Chat.Type.SYSTEM) {
                if (!SDF.format(chat.normalDate()).equals(DAY)) {
                    Chat daySystemChat = new Chat("--------------------------"+DAY+"--------------------------", SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                    daySystemChat.setDate(dataItem.unixTime());
                    dataList.add(daySystemChat);
                }
                break;
            }
        }

        //TODO: 수정필요
        if(dataItem.getType() != Chat.Type.SYSTEM)
            index = dataItem.getIndex();

        dataList.add(new Chat(dataItem));
        roomElementAdapter.setUserList(dataList, userList);
        binding.RecyclerView.scrollToPosition(dataList.size() - 1);
    }
    //채팅방 정보 불러옴
    private void getChatRoomMeta() {
        //TODO : 채팅방 정보 불러오기
    }

    //사용자가 send 버튼 눌렀을때, 메세지를 보내고 메세지 내용 파이어베이스에 저장
    private void sendMessage(){
        if (binding.chatEdit.getText().toString().equals(""))
            return;

        // USER_NAME 나중에 userKey로 바꿔줘야함
        ChatDB.uploadMessage(binding.chatEdit.getText().toString(), ++index, messageType, chatRoomKey, userKey, userList);
        binding.chatEdit.setText(""); //입력창 초기화
    }
    //초대하기->유저추가 액티비티로 보낼 데이터 저장 후 intent
    private void inviteUser(){
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("tag",2);
        intent.putExtra("who", userName);
        intent.putExtra("where", chatRoomKey);
        startActivity(intent);
    }

    //사용자 갤러리로 접근
    private void galleryAccess(){

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
        //TODO : chatroom key로 폴더명을 바꾸는 것이 좋을 것으로 생각
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
                        ChatDB.uploadMessage(uri.toString(), ++index, Chat.Type.IMAGE, chatRoomKey, userKey, userList);
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
