package com.example.chat_de;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.databinding.NavigationJoinUserBinding;
import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.IndexDeque;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;

public class RoomActivity extends AppCompatActivity implements IUploadFileEventListener {
    private final int HASH_CODE = hashCode();
    private final int SYSTEM_MESSAGE = -2;
    private final int CHAT_LIMIT = 15;
    private ActivityRoomBinding binding;
    private NavigationJoinUserBinding binding_temp;
    //private NavigationJoinUserBinding binding;
    private RoomElementAdapter roomElementAdapter;
    private LinearLayoutManager manager;

    private final int IMAGE_CODE = 10;
    private final int VIDEO_CODE = 20;
    private final int FILE_CODE = 30;
    private int requestCode;
    private String chatRoomKey;

    private ChatRoomUser currentUser; //TODO LOGIN : 현재 로그인된 사용자
    private String frontChatKey = null;
    private Chat.Type messageType = null;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private IndexDeque<Chat> dataList = new IndexDeque<>();
    private HashMap<String, ChatRoomUser> userList = new HashMap<>();

    private int lastIndex = -1;
    private boolean isLoading = false;
    private boolean autoScroll = true;
    private boolean isActionMove = false;
    private ChatRoomMeta chatRoomMeta;
    private ProgressDialog progressDialog;
    private Uri filePath;

    private boolean isDrawerOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        //binding = NavigationJoinUserBinding.inflate(getLayoutInflater());
        binding_temp = binding.drawerView;
        //binding_temp =NavigationJoinUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //화면 기본 설정
        setUpRoomActivity();

        /*리스너 설정*/
        // 처음 CHAT_LIMIT + 1개의 채팅 불러오고 채팅에 대한 리스너 설정
        ChatDB.getChatRoomUserListCompleteListener(chatRoomKey, joinedUserList -> {
            userList.putAll(joinedUserList);
            currentUser = joinedUserList.get(ChatDB.getCurrentUserKey());
            ChatDB.setCurrentUser(currentUser);
            roomElementAdapter.setCurrentUser(currentUser);
            ChatDB.getLastChatCompleteListener(chatRoomKey, (chatKey, chatValue) -> {
                frontChatKey = chatKey;
                ChatDB.getPrevChatListCompleteListener(chatRoomKey, chatKey, CHAT_LIMIT, (prevChatListKey, prevChatList) -> {
                    if (prevChatListKey != null) {
                        frontChatKey = prevChatListKey;
                    }
                    if (chatKey != null) {
                        prevChatList.add(chatValue);
                    }
                    floatOldMessage(prevChatList);
                    ChatDB.messageAddedEventListener(chatRoomKey, chatKey, HASH_CODE, (newChatKey, newChat) -> {
                        floatNewMessage(newChat);
                    });
                });
            });
            ChatDB.userListChangedEventListener(chatRoomKey, HASH_CODE, (changedUserKey, changedUser) -> {
                // 강퇴당했거나 방이 사라진 등의 사유로 더 이상 자신이 채팅방에 존재하지 않는 경우 액티비티 종료
                if(changedUserKey.equals(currentUser.getUserKey()) && !changedUser.getExist()) {
                    Toast.makeText(RoomActivity.this, "방에서 퇴장당하셨습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                userList.put(changedUserKey, changedUser);
            });
        });
        ChatDB.userReadLastMessage(chatRoomKey, ChatDB.getCurrentUserKey());

    }

    @Override
    protected void onStart() {
        super.onStart();

        //리사이클러뷰 스크롤 리스너 설정
        initScrollListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpRoomActivity() {
        /*view, 변수 할당, click listener 등 한번만 설정되는 화면 구성 설정*/
        //플로팅버튼 숨기기
        binding.fab.setVisibility(View.GONE);

        //리사이클러뷰 설정
        initRecyclerView();

        //채팅방 설정
        chatRoomKey = getIntent().getStringExtra("chatRoomKey");
        getChatRoomMeta();

        //액션바 타이틀 바 이름 설정
        setSupportActionBar(binding.toolbarRoom);
        getSupportActionBar().setTitle("");

        //메시지 전송 버튼에 대한 클릭 리스너 지정
        binding.chatSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //파일 이미지 버튼에 대한 클릭 리스너 지정
        binding.fileSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryAccess();
            }
        });

        //floating 버튼에 대한 클릭 리스너 지정
        binding.fab.setOnClickListener(view -> binding.recyclerView.scrollToPosition(dataList.size() - 1));

        //리사이클러뷰 터치리스너(키보드 내려가게)
        binding.recyclerView.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                isActionMove = true;
            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if(!isActionMove) {
                    InputMethodManager imm = (InputMethodManager) RoomActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.chatEdit.getWindowToken(), 0);
                }
                isActionMove = false;
            }
            return false;
        });

        //startActivityForResult를 대체하는 ActivityResultLauncher
        //ActivityResultLauncher의 경우 onResume이 실행되기전에 초기화 되어야 한다
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Uri filePath = result.getData().getData();
                String fileName = getName(filePath);
                if (filePath != null){
                    if(requestCode==FILE_CODE)
                        uploadFile(requestCode,filePath,fileName);
                    else
                        uploadFile(requestCode,filePath,FileDB.getFileType(this, filePath));
                }
                try {
                    InputStream in = getContentResolver().openInputStream(filePath);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //유저 목록 보기 버튼에 대한 클릭 리스너 지정
        binding.userListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ChatDB.getAdminMode())
                    binding.drawerView.functions.setVisibility(View.VISIBLE);
                else
                    binding.drawerView.functions.setVisibility(View.GONE);
                //drawerView에 채팅 참가자 리스트 띄워주기
                ArrayList<AUser> joinUser = new ArrayList<>();
                AUser userMe= currentUser.userMeta();
                joinUser.add(userList.get(userMe.getUserKey()));
                for (ChatRoomUser e : userList.values()) {
                    if(e.getExist() && !e.getUserKey().equals(userMe.getUserKey())) {
                        joinUser.add(e);
                    }
                }

                JoinUserListAdapter joinUserListAdapter;
                joinUserListAdapter = new JoinUserListAdapter(joinUser, userMe);
                binding.drawerView.joinUser.setAdapter(joinUserListAdapter);

                binding.drawerView.joinUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //current user를 제외한 다른 사용자를 클릭 시 일대일 채팅을 할 수 있도록 설정
                        if(!userMe.getUserKey().equals(joinUser.get(i).getUserKey())) {
                            Intent intent = new Intent(RoomActivity.this, UserProfileActivity.class);
                            //선택한 사용자 정보 전송
                            intent.putExtra("userOther", joinUser.get(i));
                            //로그인된 사용자 정보 전송
                            intent.putExtra("userMe", currentUser.userMeta());
                            startActivity(intent);
                        }
                    }
                });

                binding.drawerView.settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RoomActivity.this, CreateRoomMetaActivity.class);
                        intent.putExtra("chatRoomKey",chatRoomKey);
                        intent.putExtra("chatRoomName",chatRoomMeta.getName());
                        intent.putExtra("chatRoomPicture",chatRoomMeta.getPictureURL());
                        startActivity(intent);
                    }
                });

                binding.drawerLayout.openDrawer(binding_temp.getRoot());
                binding.drawerLayout.setDrawerListener(drawerL);

                //showJoinedUserList();
            }
        });



        //초대하기 버튼에 대한 클릭 리스너 지정
        if(!ChatDB.getAdminMode()) { //adminmode가 아니면
            binding.addUserButton.setVisibility(View.GONE);
        } else {
            binding.addUserButton.setOnClickListener(view -> inviteUser());
        }

        //채팅방 이름이 길 경우, 회전하도록 설정
        binding.chatTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.chatTitle.setSelected(true);
    }

    //drawerlayout의 리스너 : 네비게이션 드로워가 열려있는지 닫혀있는지를 구분하여 backpress를 눌렀을 때 다른 동작
    DrawerLayout.DrawerListener drawerL = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            isDrawerOpened=true;
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            isDrawerOpened=false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    @Override
    public void onBackPressed(){
        if(isDrawerOpened){
            binding.drawerLayout.closeDrawer(Gravity.RIGHT);
        }
        else
            finish();
    }


    private void initScrollListener() {
        /*리사이클러뷰(채팅창)의 스크롤 리스너 설정*/
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) { //최하단에 닿았을 때
                    Log.d("TAG", String.valueOf(autoScroll));
                    autoScroll = true;
                    //binding.fab.hide();
                    if(binding.fab.getVisibility() == View.VISIBLE) {
                        Animation animation = AnimationUtils.loadAnimation(RoomActivity.this, R.anim.scale_down);
                        binding.fab.startAnimation(animation);
                        binding.fab.setVisibility(View.GONE);
                    }
                } else if (!recyclerView.canScrollVertically(-1)) { //최상단에 닿았을 때
                    if (!isLoading) {
                        if (frontChatKey != null) {
                            loadMore();
                            isLoading = true;
                        }
                        autoScroll = false;
                        //binding.fab.show();
                        if(binding.fab.getVisibility() != View.VISIBLE) {
                            Animation animation = AnimationUtils.loadAnimation(RoomActivity.this, R.anim.scale_up);
                            binding.fab.startAnimation(animation);
                            binding.fab.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    autoScroll = false;
                    //binding.fab.show();
                    if(binding.fab.getVisibility() != View.VISIBLE) {
                        Animation animation = AnimationUtils.loadAnimation(RoomActivity.this, R.anim.scale_up);
                        binding.fab.startAnimation(animation);
                        binding.fab.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void loadMore() {
        /*최상단에 닿았을때, 데이터를 더 가져오는 함수*/
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

                ChatDB.getPrevChatListCompleteListener(chatRoomKey, frontChatKey, CHAT_LIMIT, (prevChatListKey, prevChatListValue) -> {
                    frontChatKey = prevChatListKey;
                    floatOldMessage(prevChatListValue);
                    isLoading = false;
                });
                autoScroll = false;
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        //onCreate를 통해 만들어 진 것이 아니면 messageAddedEventListener를 붙임
        initScrollListener();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.recyclerView.clearOnScrollListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatDB.removeEventListener(HASH_CODE);
    }

    public void initRecyclerView() {
        //binding.RecyclerView.setItemViewCacheSize(50);
        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        manager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(manager);

        roomElementAdapter = new RoomElementAdapter(dataList, userList, currentUser);

        binding.recyclerView.setAdapter(roomElementAdapter);
        roomElementAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);
    }


    private void floatOldMessage(ArrayList<Chat> chatList) {
        /*불러와진 예전 메세지를 화면에 보여주는 함수*/
        ListIterator i = chatList.listIterator(chatList.size());
        int cnt = chatList.size();

        while (i.hasPrevious()) {
            Chat chat = (Chat) i.previous();
            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
            final String DAY = SDF.format(chat.normalDate());
            if (dataList.size() != 0 && !SDF.format(dataList.getFront().normalDate()).equals(DAY)) {
                Chat daySystemChat = new Chat(DAY, SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                daySystemChat.setDate(dataList.getFront().unixTime());
                dataList.pushFront(daySystemChat);
                cnt++;
            }
            if(lastIndex < chat.getIndex()) {
                lastIndex = chat.getIndex();
            }
            dataList.pushFront(chat);
        }
        if (cnt != 0)
            roomElementAdapter.notifyItemRangeInserted(0, cnt);
    }

    private void floatNewMessage(Chat chat) {
        /*새 채팅 보냇을 때, 또는 새 채팅을 받았을때, 메세지를 화면에 보여주는 함수*/
        int cnt = 1;

        if (dataList.size() != 0) {
            //이전 메시지와 비교해서 날짜가 달라지면 시스템 메시지로 현재 날짜를 추가해주는 부분
            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
            final String DAY = SDF.format(chat.normalDate());
            if (!SDF.format(dataList.getBack().normalDate()).equals(DAY)) {
                Chat daySystemChat = new Chat(DAY, SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                daySystemChat.setDate(chat.unixTime());
                dataList.pushBack(daySystemChat);
                cnt++;
            }
        }

        if(lastIndex < chat.getIndex()) {
            lastIndex = chat.getIndex();
        }
        dataList.pushBack(new Chat(chat));
        roomElementAdapter.notifyItemRangeInserted(dataList.size() - cnt + 1, cnt);
        if (autoScroll)
            binding.recyclerView.scrollToPosition(dataList.size() - 1);
    }

    private void getChatRoomMeta() {
        /*채팅방 정보(ChatRoomMeta)를 불러오는 함수*/
        ChatDB.getChatRoomMeta(chatRoomKey, item -> {
            chatRoomMeta = item;
            binding.chatTitle.setText(chatRoomMeta.getName());
        });
    }

    private void sendMessage() {
        /*사용자가 send 버튼을 눌렀을 때 동작하는 함수로, 메세지 내용을 파이어베이스에 저장*/
        messageType = Chat.Type.TEXT;
        if (binding.chatEdit.getText().toString().equals(""))
            return;

        ChatDB.uploadMessage(binding.chatEdit.getText().toString(), ++lastIndex, messageType, chatRoomKey, currentUser.userMeta().getUserKey(), userList);
        binding.chatEdit.setText(""); //입력창 초기화
        autoScroll = true;
    }

    private void inviteUser() {
        /*초대하기->유저추가 액티비티로 보낼 데이터 저장 후 intent*/
        Intent intent;
        intent = new Intent(this, UserListActivity.class);

        intent.putExtra("tag", 2);
        intent.putExtra("chatRoomKey", chatRoomKey);
        intent.putExtra("chatRoomMeta", chatRoomMeta);
        //TODO: 현재 채팅방에 있는 유저리스트를 항상 새로 만들지 않게 변경 필요
        HashSet<String> set = new HashSet<>();
        for(ChatRoomUser e : userList.values()) {
            if(e.getExist()) {
                set.add(e.getUserKey());
            }
        }
        intent.putExtra("userList", set);

        startActivity(intent);
    }

    private void galleryAccess() {
        /*사진 전송시 사용자 갤러리로 접근하는 함수*/
        final String[] fileKind = {"image", "video", "file"};

        //갤러리만
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent,10);*/



        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        AlertDialog.Builder dlg = new AlertDialog.Builder(RoomActivity.this);
        dlg.setTitle("파일 종류") //제목
                .setItems(fileKind, (dialogInterface, position) -> {
                    Intent sendIntent = new Intent();
                    switch (position) {
                        case 0:  //image
                            sendIntent = Intent.createChooser(FileDB.openImage(), "이미지를 선택하세요.");
                            requestCode = IMAGE_CODE;
                            break;
                        case 1:  //video
                            sendIntent = Intent.createChooser(FileDB.openVideo(), "video를 선택하세요.");
                            requestCode = VIDEO_CODE;
                            break;
                        case 2 :  //file
                            sendIntent = Intent.createChooser(FileDB.openFile(), "파일를 선택하세요.");
                            requestCode = FILE_CODE;
                            break;
                    }
                    //activityResultLauncher를 sendIntent로 실행
                    activityResultLauncher.launch(sendIntent);
                });
        dlg.setIcon(R.drawable.file_blue);  //대화창 아이콘 설정
        dlg.setNegativeButton("cancel", null);

        dlg.show();

    }

    //FileDB로 못 옮김! AppCompatActivity를 상속해야지 사용할 수 있는 함수가 있어서!!
    private String getName(Uri uri) {
        /*파일명 찾기*/
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void uploadFile(int requestCode,Uri filePath,String FileNameOrExtension) {
        /*firebase storage에 파일(이미지, 비디오, 파일)을 업로드 하는 함수*/
        //Todo: 파이어베이스에 올리는 코드 수정 (node에서 링크 받아오기)

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업로드중...");
        progressDialog.show();

        //파이어베이스에 push할 메세지 타입 정하기(이미지, 비디오)
        if (requestCode == IMAGE_CODE)
            messageType = Chat.Type.IMAGE;
        else if (requestCode == VIDEO_CODE)
            messageType = Chat.Type.VIDEO;
        else
            messageType = Chat.Type.FILE;

        //파일 명이 중복되지 않도록 날짜를 이용 (현재시간 + 사용자 키)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");
        String filename;
        if(requestCode==FILE_CODE)
            filename = sdf.format(new Date()) + "_" + currentUser.userMeta().getUserKey() +"_"+ FileNameOrExtension;
        else
            filename = sdf.format(new Date()) + "_" + currentUser.userMeta().getUserKey()+"."+FileNameOrExtension;
        //폴더가 없으면 자동 생성
        String rootPath = "KNU_AMP/"+ChatDB.getRootPath()+"/"+chatRoomMeta.getName()+"/" + filename;

        FileDB.setReference(rootPath);
        FileDB.uploadFile(filePath,this);

    }

    @Override
    public void SuccessUpload(Uri uri) {
        ChatDB.uploadMessage(uri.toString(), ++lastIndex, messageType, chatRoomKey, currentUser.userMeta().getUserKey(), userList);
        progressDialog.dismiss();
    }

    @Override
    public void FailUpload(Exception e) {
        progressDialog.dismiss();
        Toast.makeText(RoomActivity.this, "upload 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        Log.d("exception", String.valueOf(e));
    }

    @Override
    public void ProgressUpload(double progress) {
        //dialog에 진행률을 퍼센트로 출력해 준다
        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
    }
}
