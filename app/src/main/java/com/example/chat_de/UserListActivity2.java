package com.example.chat_de;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_de.databinding.ActivityUserList2Binding;
import com.example.chat_de.databinding.ActivityUserListBinding;
import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class UserListActivity2 extends AppCompatActivity implements TextWatcher {

    private ArrayList<UserListItem> userList = new ArrayList();
    private UserListAdapter userListAdapter;

    private final int NEW_CHAT = 1;
    private final int INVITE_CHAT = 2;
    private int mode=0;
    private User userMe;
    private String chatRoomKey = null;
    private ChatRoomMeta chatRoomMeta = null;
    private String myUserKey;
    private String chatRoomName="";
    private HashSet<String> userKeySet;

    private ActivityUserList2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserList2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        myUserKey = ChatDB.getCurrentUserKey();
        setActionBar();
        showUserList();
    }

    public void setActionBar(){
        //인텐트로 mode값 , 초대/생성하는 User 정보 받아오기기
        Intent intent = getIntent();
        mode = intent.getIntExtra("tag",0);
        userKeySet = (HashSet<String>)intent.getSerializableExtra("userList");
        setSupportActionBar(binding.toolbarUserList);
        getSupportActionBar().setTitle("");
        if(mode== NEW_CHAT){
            //채팅방 만들기
            binding.userListMode.setText("새 채팅방 만들기"); ;
        }
        else if(mode==INVITE_CHAT){
            //초대하기
            binding.userListMode.setText("초대하기");
            chatRoomKey = intent.getStringExtra("chatRoomKey");
            chatRoomMeta = (ChatRoomMeta)intent.getSerializableExtra("chatRoomMeta");
        }
        else{
            Log.e("ERROR MODE","Mode값은 1또는 2만 가능합니다.");
        }
    }
    private void showNewChatDialog(){
        //다이얼로그(대화상자) 띄우기
        final EditText editText = new EditText(UserListActivity2.this);
        AlertDialog.Builder dlg = new AlertDialog.Builder(UserListActivity2.this);
        dlg.setTitle("채팅방 이름 입력"); //제목
        dlg.setMessage("새로 생성할 채팅방 이름을 입력해주세요.");
        dlg.setView(editText);
        dlg.setPositiveButton("입력", (dialogInterface, i) -> {
            chatRoomName = "";
            chatRoomName = editText.getText().toString();
            if(chatRoomName.isEmpty()){
                chatRoomName = changeToString(returnChoose(),false);
            }
            createChatRoom();
        });
        dlg.setNegativeButton("취소",null);
        dlg.show();
    }
    private void showUserList() {
        //초기화 및 데이터 불러오기
//        getAllUserList();
        //리사이클러뷰 설정
        ChatDB.getUsersCompleteEventListener(item -> {
            for(Map.Entry<String, User> i: item.entrySet()) {
                if(!userKeySet.contains(i.getKey())) {
                    userList.add(new UserListItem(i.getValue()));
                }
            }
            userMe = item.get(myUserKey);
            userListAdapter = new UserListAdapter(getApplicationContext(), userList);
            binding.recyclerUserList.setAdapter(userListAdapter);
            binding.recyclerUserList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        });
        /*검색 기능 추가*/
        binding.searchText.addTextChangedListener(this);

        /*취소, 완료 설정*/
        binding.cancel.setOnClickListener(view -> finish());
        binding.complete.setOnClickListener(view -> {
            if(returnChoose().size()==0){
                Toast.makeText(UserListActivity2.this,"초대할 사람을 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
            else{
                if(mode== NEW_CHAT){
                    //채팅방 만들기
                    showNewChatDialog();
                }
                else if(mode==INVITE_CHAT){
                    //초대하기
                    inviteChatRoom();
                }
            }
        });

        /*텍스트뷰 내용 지우기*/
        binding.searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.searchText.setText(null);
            }
        });
    }
    private ArrayList<AUser> returnChoose(){
        ArrayList<AUser> choose = new ArrayList<>();
        for (UserListItem i : userList) {
            if (i.getChecked()) {
                choose.add(i);
            }

        }
        return choose;
    }
    private void createChatRoom(){
        //체크박스로 표시된 유저 정보를 받아옴.
        ArrayList<AUser> list = returnChoose();
        list.add(userMe);
        //채팅방 만들기 누른 유저 정보 : callUserName
        //새 ChatRoom 생성
        //chatRoomJoined에 list의 유저 추가-> list.get(i).getUserKey() 사용
        //list의 user의 userJoined에 생성된 채팅방 정보 추가
        //생성메세지(message) 현재 채팅방에 시스템 메세지로 추가
        ChatDB.setChatRoomCompleteListener(chatRoomName, list, userMe, generatedKey -> {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("chatRoomKey", generatedKey);
            startActivity(intent);
            finish(); //액티비티 종료
        });
    }
    private void inviteChatRoom(){
        //체크박스로 표시된 유저 정보를 받아옴
        ArrayList<AUser> list = returnChoose();
        //초대하기 누른 유저 정보 : callUserName

        ChatDB.inviteUserListCompleteListener(chatRoomKey, chatRoomMeta, list, userMe, dummyKey -> finish());
    }
    private String changeToString(ArrayList<AUser> list, boolean formal){
        //유저리스트를 ~님, 형식으로 바꿔서 String으로 반환해줌.
        StringBuilder result = new StringBuilder();
        if(formal){
            for(AUser i : list){
                result.append(i.getName()).append("님, ");
            }
            return result.substring(0, result.length() - 3);
        }
        else {
            for (AUser i : list) {
                result.append(i.getName()).append(", ");
            }
            return result.substring(0, result.length() - 2);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /*검색 설정*/
        userListAdapter.getFilter().filter(charSequence);
    }
    @Override
    public void afterTextChanged(Editable editable) {  }
}
