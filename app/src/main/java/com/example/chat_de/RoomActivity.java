package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.chat_de.databinding.ActivityRoomBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.IndexDeque;
import com.example.chat_de.datas.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;

public class RoomActivity extends AppCompatActivity {
    private final int SYSTEM_MESSAGE = -2;
    private final int CHAT_LIMIT = 15;
    private ActivityRoomBinding binding;
    private RoomElementAdapter roomElementAdapter;
    private LinearLayoutManager manager;
    //private ListView chat_view;

    private int IMAGE_CODE = 10;
    private int VIDEO_CODE = 20;
    private int FILE_CODE = 30;
    private String chatRoomKey;

    private ChatRoomUser currentUser; //TODO LOGIN : 현재 로그인된 사용자
    private String frontChatKey = null;
    private String lastChatKey = null;
    private Chat.Type messageType = null;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private IndexDeque<Chat> dataList = new IndexDeque<>();
    private HashMap<String, ChatRoomUser> userList = new HashMap<>(); //

    private int lastIndex = -1;
    private boolean isLoading = false;
    private boolean autoScroll = true;
    private boolean isFirstRun = true;
    private boolean isActionMove = false;
    private ChatRoomMeta chatRoomMeta;
    private Uri filePath;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //바인딩 설정
        binding = ActivityRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //화면 기본 설정
        setUpRoomActivity();
        // 처음 CHAT_LIMIT + 1개의 채팅 불러오고 리스너 설정
        ChatDB.getChatRoomUserListCompleteListener(chatRoomKey, joinedUserList -> {
            userList.putAll(joinedUserList);
            currentUser = joinedUserList.get(ChatDB.getCurrentUserKey());
            roomElementAdapter.setCurrentUser(currentUser);
            ChatDB.getLastChatCompleteListener(chatRoomKey, (chatKey, chatValue) -> {
                lastChatKey = frontChatKey = chatKey;
                ChatDB.getPrevChatListCompleteListener(chatRoomKey, chatKey, CHAT_LIMIT, (prevChatListKey, prevChatList) -> {
                    if (prevChatListKey != null) {
                        frontChatKey = prevChatListKey;
                    }
                    if (chatKey != null) {
                        prevChatList.add(chatValue);
                    }
                    floatOldMessage(prevChatList);
                    ChatDB.messageAddedEventListener(chatRoomKey, chatKey, (newChatKey, newChat) -> {
                        lastChatKey = newChatKey;
                        floatNewMessage(newChat);
                    });
                });
            });
            ChatDB.userListChangedEventListener(chatRoomKey, (changedUserKey, changedUser) -> {
                userList.put(changedUserKey, changedUser);
            });
        });
        ChatDB.userReadLatestMessage(chatRoomKey, ChatDB.getCurrentUserKey());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpRoomActivity() {
        //리사이클러뷰 설정
        initRecyclerView();
        initScrollListener();

        //채팅방 설정
        chatRoomKey = getIntent().getStringExtra("chatRoomKey");
        getChatRoomMeta();

        //액션바 타이틀 바 이름 설정
        ab = getSupportActionBar();
        ab.setTitle("");

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
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


                if (!recyclerView.canScrollVertically(1)) { //최하단에 닿았을 때
                    Log.d("TAG", String.valueOf(autoScroll));
                    autoScroll = true;
                    binding.fab.hide();
                } else if (!recyclerView.canScrollVertically(-1)) { //최상단에 닿았을 때
                    if (!isLoading) {
                        if (frontChatKey != null) {
                            loadMore();
                            isLoading = true;
                        }
                        autoScroll = false;
                        binding.fab.show();
                    }
                } else {
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

                ChatDB.getPrevChatListCompleteListener(chatRoomKey, frontChatKey, CHAT_LIMIT, (prevChatListKey, prevChatListValue) -> {
                    frontChatKey = prevChatListKey;
                    floatOldMessage(prevChatListValue);
                    isLoading = false;
                });
                autoScroll = false;
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        //onCreate를 통해 만들어 진 것이 아니면 messageAddedEventListener를 붙임
        if (!isFirstRun) {
            ChatDB.messageAddedEventListener(chatRoomKey, lastChatKey, (newChatKey, newChat) -> {
                lastChatKey = newChatKey;
                floatNewMessage(newChat);
            });
        }
        isFirstRun = false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatting_addfr, menu);
        return true;
    }

    //유저 추가 메뉴바 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
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

    public void initRecyclerView() {
        //binding.RecyclerView.setItemViewCacheSize(50);
        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        manager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(manager);

        roomElementAdapter = new RoomElementAdapter(dataList, userList, currentUser);

        binding.recyclerView.setAdapter(roomElementAdapter);
        roomElementAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);
    }

    private void showJoinedUserList() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        AlertDialog.Builder dlg = new AlertDialog.Builder(RoomActivity.this);
        dlg.setTitle("참가자"); //제목
        for (String i : userList.keySet()) {
            adapter.add(userList.get(i).getUserMeta().getName());
        }
        dlg.setAdapter(adapter, null);
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }

    private void floatOldMessage(ArrayList<Chat> chatList) {
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
            dataList.pushFront(chat);
        }
        if (cnt != 0)
            roomElementAdapter.notifyItemRangeInserted(0, cnt);
    }

    //파이어베이스에 메세지가 추가되었을때, 메세지를 화면에 띄워줌.(기존 addMessage)
    private void floatNewMessage(Chat dataItem) {
        int cnt = 1;

        if (dataList.size() != 0) {
            //이전 메시지와 비교해서 날짜가 달라지면 시스템 메시지로 현재 날짜를 추가해주는 부분
            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일");
            final String DAY = SDF.format(dataItem.normalDate());
            if (!SDF.format(dataList.getBack().normalDate()).equals(DAY)) {
                Chat daySystemChat = new Chat(DAY, SYSTEM_MESSAGE, "SYSTEM", Chat.Type.SYSTEM);
                daySystemChat.setDate(dataItem.unixTime());
                dataList.pushBack(daySystemChat);
                cnt++;
            }
        }

        lastIndex = dataItem.getIndex();
        dataList.pushBack(new Chat(dataItem));
        roomElementAdapter.notifyItemRangeInserted(dataList.size() - cnt + 1, cnt);
        if (autoScroll)
            binding.recyclerView.scrollToPosition(dataList.size() - 1);
    }

    //채팅방 정보 불러옴
    private void getChatRoomMeta() {
        ChatDB.getChatRoomMeta(chatRoomKey, item -> {
            chatRoomMeta = item;
            ab.setTitle(chatRoomMeta.getName());
        });
    }

    //사용자가 send 버튼 눌렀을때, 메세지를 보내고 메세지 내용 파이어베이스에 저장
    private void sendMessage() {
        messageType = Chat.Type.TEXT;
        if (binding.chatEdit.getText().toString().equals(""))
            return;

        ChatDB.uploadMessage(binding.chatEdit.getText().toString(), ++lastIndex, messageType, chatRoomKey, currentUser.getUserMeta().getUserKey(), userList);
        binding.chatEdit.setText(""); //입력창 초기화
        autoScroll = true;
    }

    //초대하기->유저추가 액티비티로 보낼 데이터 저장 후 intent
    private void inviteUser() {
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("tag", 2);
        intent.putExtra("where", chatRoomKey);
        intent.putExtra("myUserKey", currentUser.getUserMeta().getUserKey());
        HashSet<String> set = new HashSet<>(userList.size());
        set.addAll(userList.keySet());
        intent.putExtra("userList", set);

        startActivity(intent);
    }

    //사용자 갤러리로 접근
    private void galleryAccess() {
        /*final int[] image = {R.drawable.image_red,R.drawable.video_red};*/
        final String[] fileKind = {"image", "video","file"};

        Intent intent = new Intent();
        //갤러리만
        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        startActivityForResult(intent,10);*/

        /*final List<Map<String,Object>> dialogItemList = new ArrayList<>();
        for(int i =0;i>image.length;i++){
            Map<String,Object> itemMap = new HashMap<>();
            itemMap.put("image",image[i]);
            itemMap.put("text",fileKind[i]);

            dialogItemList.add(itemMap);
        }*/

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        AlertDialog.Builder dlg = new AlertDialog.Builder(RoomActivity.this);
        dlg.setTitle("파일 종류") //제목
                .setItems(fileKind, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0:  //image
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), IMAGE_CODE);
                                break;
                            case 1:  //video
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "video를 선택하세요."), VIDEO_CODE);
                                break;
                            case 2 :  //file
                                intent.setType("application/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "파일를 선택하세요."), FILE_CODE);
                                break;
                        }
                    }
                });
        dlg.setIcon(R.drawable.file_blue);  //대화창 아이콘 설정
        //dlg.setAdapter(adapter,null);

        /*adapter.add("image");
        adapter.add("video");
        dlg.setAdapter(adapter,new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(fileKind[position].equals("image")){
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), IMAGE_CODE);
                } else if(fileKind[position].equals("video")){
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "video를 선택하세요."), VIDEO_CODE);
                }
            }
        });*/

        dlg.setNegativeButton("cancle", null);

        dlg.show();

    }


    //갤러리 액티비티에서 결과값을 제대로 받았는지 확인
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == IMAGE_CODE || requestCode == VIDEO_CODE || requestCode == FILE_CODE) && resultCode == RESULT_OK) {
            filePath = data.getData();
            String extension =getMimeType(this,filePath);
            Log.d("filePath", String.valueOf(filePath));
            Log.d("확장자", getMimeType(this,filePath));
            if (filePath != null)
                uploadFile(requestCode,extension);
            try {
                InputStream in = getContentResolver().openInputStream(filePath);
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //파일 확장자 가져오기
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


    //firebase storage에 업로드하기
    public void uploadFile(int requestCode,String extension) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업로드중...");
        progressDialog.show();

        //파이어베이스에 push할 메세지 타입 정하기(이미지, 비디오)
        //TODO:파일 타입도 정하기
        if (requestCode == IMAGE_CODE)
            messageType = Chat.Type.IMAGE;
        else if (requestCode == VIDEO_CODE)
            messageType = Chat.Type.VIDEO;
        else
            messageType = Chat.Type.FILE;

        //파일 명이 중복되지 않도록 날짜를 이용 (현재시간 + 사용자 키)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");
        //TODO:파일에 맞는 확장자 추가
        String filename = sdf.format(new Date()) + "_" + currentUser.getUserMeta().getUserKey()+"."+extension;

        //uploads라는 폴더가 없으면 자동 생성
        //TODO : chatroom key로 폴더명을 바꾸는 것이 좋을 것으로 생각 pre_2빼
        StorageReference imgRef = firebaseStorage.getReference("KNU_AMP/"+chatRoomKey+"/" + filename);

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
                        ChatDB.uploadMessage(uri.toString(), ++lastIndex, messageType, chatRoomKey, currentUser.getUserMeta().getUserKey(), userList);
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
                Toast.makeText(RoomActivity.this, "upload 실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                //dialog에 진행률을 퍼센트로 출력해 준다
                progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
            }
        });
    }
}
