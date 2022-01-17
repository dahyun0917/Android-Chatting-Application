package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.IndexDeque;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RoomActivity extends AppCompatActivity {
    private final int SYSTEM_MESSAGE = -2;
    private final int CHAT_LIMIT = 15;
    private ActivityRoomBinding binding;
    private RoomElementAdapter roomElementAdapter;
    private LinearLayoutManager manager;
    //private ListView chat_view;

    private int GALLEY_CODE = 10;
    private String chatRoomKey;
    private String userKey = "user2";
    private String userName = "user2";
    private String frontChatKey;
    private Chat.Type messageType = Chat.Type.TEXT;

    private ArrayList<String> listenerPath = new ArrayList<>();

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private IndexDeque<Chat> dataList = new IndexDeque<>();

    private HashMap<String, ChatRoomUser> userList  = new HashMap<>(); //

    //private HashMap<String,ChatRoomUser> chatRoomUserList;

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
    }

    public void setUpRoomActivity(){
        //리사이클러뷰 설정
        initRecyclerView();
        initScrollListener();

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
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(-1)) { //최상단에 닿았을 때
                    if(!isLoading){
                        if(frontChatKey != null) {
                            loadMore();
                            isLoading = true;
                        }
                        autoScroll = false;
                    }
                }
                else if(!recyclerView.canScrollVertically(1)){ //최하단에 닿았을 때
                    autoScroll = true;
                }
                else{
                    autoScroll = false;
                }
            }
        });
    }

    private void loadMore() {
        binding.recyclerView.post(new Runnable() {
            public void run() {
                dataList.pushFront(null);
                roomElementAdapter.notifyItemInserted(0);
            }
        });
        binding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataList.popFront();
                roomElementAdapter.notifyItemRemoved(0);

                ChatDB.getPrevChatCompleteListener(chatRoomKey, frontChatKey, CHAT_LIMIT, itemList -> {
                    frontChatKey = itemList.first;
                    dataList.appendFront(itemList.second);
                    roomElementAdapter.notifyItemRangeInserted(0, itemList.second.size());
                    isLoading = false;
                });
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        //메시지가 새로 올라올 때마다 동작하는 리스너 설정

        dataList = new IndexDeque<>();
        userList = new HashMap<>();
        frontChatKey = null;
        ChatDB.getChatRoomUserListCompleteListener(chatRoomKey, item -> {
            userList = item;
            ChatDB.getLastChatKey(chatRoomKey, key -> {
                ChatDB.getPrevChatCompleteListener(chatRoomKey, key, CHAT_LIMIT, dataItem -> {
                    frontChatKey = dataItem.first;
                    for(Chat i: dataItem.second) {
                        floatMessage(i);
                    }
                    ChatDB.messageAddedEventListener(chatRoomKey, key, dataPair -> {
                        floatMessage(dataPair.second);
                    });
                });
            });
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
        binding.recyclerView.clearOnScrollListeners();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View vw = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (vw == null) {
            vw = new View(this);
        }
        imm.hideSoftInputFromWindow(vw.getWindowToken(), 0);

        return super.dispatchTouchEvent(motionEvent);
    }

    public void initRecyclerView(){
        //binding.RecyclerView.setItemViewCacheSize(50);
        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        manager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(manager);
        roomElementAdapter = new RoomElementAdapter(dataList, userList);
        binding.recyclerView.setAdapter(roomElementAdapter);
    }

    private void showJoinedUserList(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1);
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
        final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
        final String DAY = SDF.format(dataItem.normalDate());
        for(int i = dataList.size() - 1; i >= 0; i--) {
            final Chat chat = dataList.get(i);
            if(dataList.get(i).getType() != Chat.Type.SYSTEM) {
                if (!SDF.format(chat.normalDate()).equals(DAY)) {
                    Chat daySystemChat = new Chat("--------------------------"+DAY+"--------------------------", SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                    daySystemChat.setDate(dataItem.unixTime());
                    dataList.add(daySystemChat);
                }
                break;
            }
        }

        //TODO: 수정필요
        if(dataItem.getType() != Chat.Type.SYSTEM && index < dataItem.getIndex())
            index = dataItem.getIndex();

        dataList.add(new Chat(dataItem));
        roomElementAdapter.setUserList(dataList, userList);
        if(autoScroll)
            binding.recyclerView.scrollToPosition(dataList.size() - 1);
    }
    //채팅방 정보 불러옴
    private void getChatRoomMeta() {
        //TODO : 채팅방 정보 불러오기
    }

    //사용자가 send 버튼 눌렀을때, 메세지를 보내고 메세지 내용 파이어베이스에 저장
    private void sendMessage(){
        if (binding.chatEdit.getText().toString().equals(""))
            return;

        ChatDB.uploadMessage(binding.chatEdit.getText().toString(), ++index, messageType, chatRoomKey, userKey, userList);
        binding.chatEdit.setText(""); //입력창 초기화
        autoScroll = true;
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
        intent.setType("image/* video/*");
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
            try{
                InputStream in = getContentResolver().openInputStream(filePath);
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //firebase storage에 업로드하기
    public void uploadFile() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업로드중...");
        progressDialog.show();

        //파일 명이 중복되지 않도록 날짜를 이용 (현재시간 + 사용자 키)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");
        //TODO:파일에 맞는 확장자 추가
        String filename = sdf.format(new Date()) + "_" + userKey ;

        //uploads라는 폴더가 없으면 자동 생성
        //TODO : chatroom key로 폴더명을 바꾸는 것이 좋을 것으로 생각
        StorageReference imgRef = firebaseStorage.getReference("pre_2/"+chatRoomKey+"/" + filename);

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
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //Toast.makeText(RoomActivity.this, "upload 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                //dialog에 진행률을 퍼센트로 출력해 준다
                progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
            }
        });
    }
}
