package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chat_de.databinding.ActivityMainBinding;
import com.example.chat_de.databinding.ActivityUserListBinding;
import com.example.chat_de.datas.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListActivity extends AppCompatActivity implements TextWatcher {
    String[] items = {"전체","1-10기","11-20기","21-30기","31-40기","41-50기","51-60기","61기-70기","71기-"};

    private ArrayList<UserListItem>[] userList = new ArrayList[9];
    private UserListAdapter userListAdapter;

    private final int NEW_CAHT = 1;
    private final int INVITE_CHAT = 2;
    private int mode=0;
    private String callUserName;
    private String receivedKey;
    private String chatRoomName="";

    private ActivityUserListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        for(int i = 0; i < userList.length; ++i)
            userList[i] = new ArrayList<>();
        setActionBar();
        showUserList();
    }

    public void setActionBar(){
        //인텐트로 mode값 , 초대/생성하는 User 정보 받아오기기
        Intent getintent = getIntent();
        mode = getintent.getIntExtra("tag",0);
        callUserName = getintent.getStringExtra("who");

        if(mode==NEW_CAHT){
            //채팅방 만들기
            ActionBar ab = getSupportActionBar() ;
            ab.setTitle("새 채팅방 만들기") ;
        }
        else if(mode==INVITE_CHAT){
            //초대하기
            ActionBar ab = getSupportActionBar() ;
            ab.setTitle("초대하기") ;
            receivedKey = getintent.getStringExtra("where");
        }
        else{
            Log.e("ERROR MODE","Mode값은 1또는 2만 가능합니다.");
        }
    }
    private void inputChatRoomName(){
        //다이얼로그(대화상자) 띄우기
        final EditText editText = new EditText(UserListActivity.this);
        AlertDialog.Builder dlg = new AlertDialog.Builder(UserListActivity.this);
        dlg.setTitle("채팅방 이름 입력"); //제목
        dlg.setMessage("새로 생성할 채팅방 이름을 입력해주세요.");
        dlg.setView(editText);
        dlg.setPositiveButton("입력", (dialogInterface, i) -> {
            chatRoomName = "";
            chatRoomName = editText.getText().toString();
            if(chatRoomName.isEmpty()){
                chatRoomName = changeToString(returnChoose(),false);
            }
            finish(); //액티비티 종료
        });
        dlg.setNegativeButton("취소",null);
        dlg.show();
    }
    private void getAllUserList(){
        // TODO : 유저 리스트 받아오기
        ArrayList<User> users = new ArrayList<>();
        //firebase에서 users데이터 받아오기

        for (int i = 0; i<users.size() ;i++){
            //usermeta를 userList에 넣기
            //user클래스를 userItem 생성자에 넣으면  userItem형식으로 객체 생성가능
            //userList.add(new UserListItem(users.get(i)));
        }

        //테스트용 데이터 - 나중에 삭제 해야됨
        classifyAdd(new UserListItem("user1","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",81,"hje"));
        classifyAdd(new UserListItem("user2","",10,"whs"));
        classifyAdd(new UserListItem("user3","",30,"rke"));
        classifyAdd(new UserListItem("user4","https://t1.daumcdn.net/cfile/blog/2455914A56ADB1E315",20,"df"));
    }
    private void classifyAdd(@NonNull UserListItem item){
        userList[(item.getGeneration()-1)/10].add(item);
    }
    private void showUserList() {

        //초기화 및 데이터 불러오기
        getAllUserList();

        //리사이클러뷰 설정
        binding.recyclerUserList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        userListAdapter = new UserListAdapter(getApplicationContext(),userList);
        binding.recyclerUserList.setAdapter(userListAdapter);


        //스피너 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,items);

        //항목 선택시 보이는 별도창의 각 아이템을 위한 레이아웃 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //아이템이 선택되면
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // int i : item의 순서대로 0번부터 n-1번까지
                // userList[0]: 1-10기 ...
                if(i == 0) {
                    userListAdapter.serUserList(userList);
                } else {
                    userListAdapter.setUserList(userList[i-1]);
                }
            }
            //스피너에서 아무것도 선택되지 않은 상태일때
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userListAdapter.serUserList(userList);
            }
        });

        /*검색 기능 추가*/
        binding.searchText.addTextChangedListener(this);

        /*텍스트뷰 내용 지우기*/
        binding.searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.searchText.setText(null);
            }
        });
    }
    private ArrayList<UserListItem> returnChoose(){
        ArrayList<UserListItem> choose = new ArrayList<>();
        for(ArrayList<UserListItem> list : userList) {
            for (UserListItem i : list) {
                if (i.getChecked())
                    choose.add(i);
            }
        }
        return choose;
    }
    private void createChatRoom(){
        //TODO : 새 채팅방 생성
        //체크박스로 표시된 유저 정보를 받아옴.
        ArrayList<UserListItem> list = returnChoose();
        //채팅방 만들기 누른 유저 정보 : callUserName
        String message = callUserName+"님이 채팅방"+""+"를 생성하셨습니다.";
        //새 ChatRoom 생성
        //chatRoomJoined에 list의 유저 추가-> list.get(i).getUserKey() 사용
        //list의 user의 userJoined에 생성된 채팅방 정보 추가
        //생성메세지(message) 현재 채팅방에 시스템 메세지로 추가
        ChatDB.setChatRoom("noname", list, "나", chatRoomKey -> {
            Log.d("asdfasdf", chatRoomKey);
        });
    }
    private void inviteChatRoom(){
        //TODO : 초대한 유저를 해당 채팅방에 추가
        //체크박스로 표시된 유저 정보를 받아옴
        ArrayList<UserListItem> list = returnChoose();
        //초대하기 누른 유저 정보 : callUserName
        //changeToString : 유저리스트를 ~님, 형식으로 바꿔줌.
        String message = callUserName+"님이 "+changeToString(list,true)+"님을 초대하셨습니다.";
        //chatRoomJoined에 list의 유저 추가-> list.get(i).getUserKey() 사용
        //list의 user의 userJoined에 현재 채팅방 정보 추가->receivedKey 사용
        //초대메세지(message) 현재 채탕방에 시스템 메세지로 추가.
    }
    private String changeToString(ArrayList<UserListItem> list, boolean formal){
        //유저리스트를 ~님, 형식으로 바꿔서 String으로 반환해줌.
        StringBuilder result = new StringBuilder();
        if(formal){
            for(UserListItem i : list){
                result.append(i.getName() + "님, ");
            }
            return result.substring(0, result.length() - 3);
        }
        else {
            for (UserListItem i : list) {
                result.append(i.getName() + ", ");
            }
            return result.substring(0, result.length() - 2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.meun_user_list,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //액션바의 "완료" 클릭했을 때
        int curId = item.getItemId();
        switch(curId){
            case R.id.action_complete:
                if(returnChoose().size()==0){
                    Toast.makeText(UserListActivity.this,"초대할 사람을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(mode==NEW_CAHT){
                        //채팅방 만들기
                        //TODO 현재는 비동기적이라서 inputChatRoomName()이 끝나기 전에 createChatRoom()가 실행되는 문제가 있다
                        //inputChatRoomName();
                        createChatRoom();
                    }
                    else if(mode==INVITE_CHAT){
                        //초대하기
                        inviteChatRoom();
                        finish();//액티비티 종료
                    }
                }
                break;
            case R.id.action_cancel:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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