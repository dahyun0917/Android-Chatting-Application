package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoom;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.IndexDeque;
import com.example.chat_de.datas.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;


public class RoomActivity extends AppCompatActivity {
    private final int SYSTEM_MESSAGE = -2;
    private final int CHAT_LIMIT = 15;
    private ActivityRoomBinding binding;
    private RoomElementAdapter roomElementAdapter;
    private LinearLayoutManager manager;
    //private ListView chat_view;

    private int GALLEY_CODE = 10;
    private String chatRoomKey;

    private ChatRoomUser currentUser; //TODO LOGIN : 현재 로그인된 사용자
    private String frontChatKey;

    private Chat.Type messageType = Chat.Type.TEXT;

    private ArrayList<String> listenerPath = new ArrayList<>();

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private IndexDeque<Chat> dataList = new IndexDeque<>();
    private HashMap<String, ChatRoomUser> userList  = new HashMap<>(); //

    //private HashMap<String,ChatRoomUser> chatRoomUserList;

    private int lastIndex =-1;
    private boolean isLoading = false;
    private boolean autoScroll = true;
    Uri filePath;

    public static Activity roomActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        roomActivity = RoomActivity.this;
        //화면 기본 설정
        setUpRoomActivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


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

        //floating 버튼에 대한 클릭 리스너 지정
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.recyclerView.scrollToPosition(dataList.size() - 1);
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

                if(!recyclerView.canScrollVertically(-1)){ //최상단에 닿았을 때
                    if (!isLoading){
                        if(frontChatKey != null) {
                            loadMore();
                            isLoading = true;
                        }
                        autoScroll = false;
                        binding.fab.show();
                    }
                }
                else if(!recyclerView.canScrollVertically(1)){ //최하단에 닿았을 때
                    Log.d("TAG",String.valueOf(autoScroll));
                    autoScroll = true;
                    binding.fab.hide();
                }
                else{
                    autoScroll = false;
                    binding.fab.show();
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
                    floatOldMessage(itemList.second);
                    isLoading = false;
                });
                autoScroll = false;
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        //메시지가 새로 올라올 때마다 동작하는 리스너 설정

        frontChatKey = null;
        ChatDB.getChatRoomUserListCompleteListener(chatRoomKey, item -> {
            for(Map.Entry<String, ChatRoomUser> i: item.entrySet()) {
                userList.put(i.getKey(), i.getValue());
            }
            ChatDB.getLastChatKey(chatRoomKey, key -> {
                ChatDB.getPrevChatCompleteListener(chatRoomKey, key, CHAT_LIMIT, itemList -> {
                    frontChatKey = itemList.first;
                    floatOldMessage(itemList.second);
                    ChatDB.messageAddedEventListener(chatRoomKey, key, dataPair -> {
                        floatNewMessage(dataPair.second);
                    });
                });
            });
            ChatDB.userListChangedEventListener(chatRoomKey, userPair -> {
                userList.put(userPair.first, userPair.second);
            });
        });
        ChatDB.userReadLatestMessage(chatRoomKey, currentUser.getUserMeta().getUserKey());
        initScrollListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatDB.removeEventListenerBindOnThis();
        binding.recyclerView.clearOnScrollListeners();
    }
    @Override
    public void onStop() {
        super.onStop();
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

    // 포커스가 키보드를 제외한 다른 곳으로 갔을 때 키보드 내리기
/*    @Override
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
    }*/

    public void initRecyclerView(){
        //binding.RecyclerView.setItemViewCacheSize(50);
        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        manager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(manager);

        //TODO LOGIN : 임시로 현재 사용자 설정함->사용자 인증 도입 후 수정해야됨
        currentUser = new ChatRoomUser(17, new User("이다현","http://t1.daumcdn.net/friends/prod/editor/dc8b3d02-a15a-4afa-a88b-989cf2a50476.jpg",2,"user2"));
        roomElementAdapter = new RoomElementAdapter(dataList, userList, currentUser);

        binding.recyclerView.setAdapter(roomElementAdapter);
        roomElementAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);
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

    private void floatOldMessage(ArrayList<Chat> chatList) {
        ListIterator i = chatList.listIterator(chatList.size());
        int cnt = chatList.size();

        while(i.hasPrevious()) {
            Chat chat = (Chat)i.previous();
            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
            final String DAY = SDF.format(chat.normalDate());
            if(dataList.size() != 0 && !SDF.format(dataList.getFront().normalDate()).equals(DAY)) {
                Chat daySystemChat = new Chat("--------------------------"+DAY+"--------------------------", SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                daySystemChat.setDate(dataList.getFront().unixTime());
                dataList.pushFront(daySystemChat);
                cnt++;
            }
            dataList.pushFront(chat);
        }
        if(cnt != 0)
            roomElementAdapter.notifyItemRangeInserted(0, cnt);
    }

    //파이어베이스에 메세지가 추가되었을때, 메세지를 화면에 띄워줌.(기존 addMessage)
    private void floatNewMessage(Chat dataItem) {
        int cnt = 1;

        if(dataList.size() != 0) {
            //이전 메시지와 비교해서 날짜가 달라지면 시스템 메시지로 현재 날짜를 추가해주는 부분
            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
            final String DAY = SDF.format(dataItem.normalDate());
            if (!SDF.format(dataList.getBack().normalDate()).equals(DAY)) {
                Chat daySystemChat = new Chat("--------------------------" + DAY + "--------------------------", SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                daySystemChat.setDate(dataItem.unixTime());
                dataList.pushBack(daySystemChat);
                cnt++;
            }
        }

        lastIndex = dataItem.getIndex();
        dataList.pushBack(new Chat(dataItem));
        roomElementAdapter.notifyItemRangeInserted(dataList.size() - cnt + 1, cnt);
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

        ChatDB.uploadMessage(binding.chatEdit.getText().toString(), ++lastIndex, messageType, chatRoomKey, currentUser.getUserMeta().getUserKey(), userList);
        binding.chatEdit.setText(""); //입력창 초기화
        autoScroll = true;
    }
    //초대하기->유저추가 액티비티로 보낼 데이터 저장 후 intent
    private void inviteUser(){
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("tag",2);
        intent.putExtra("who", currentUser.getUserMeta().getName());
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
        String filename = sdf.format(new Date()) + "_" + currentUser.getUserMeta().getUserKey() ;

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
                        ChatDB.uploadMessage(uri.toString(), ++lastIndex, Chat.Type.IMAGE, chatRoomKey, currentUser.getUserMeta().getUserKey(), userList);
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
