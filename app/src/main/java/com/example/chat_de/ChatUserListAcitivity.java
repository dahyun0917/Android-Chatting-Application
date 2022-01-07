package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.chat_de.datas.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatUserListAcitivity extends AppCompatActivity implements TextWatcher {

    String[] items = {"전체","1-10기","11-20기","21-30기","31-40기","41-50기","51-60기","61기-70기","71기-"};

    private ArrayList<UserItem> userList; //전체
    private ArrayList<UserItem> userList1; //1-10기
    private ArrayList<UserItem> userList2; //11-20기
    private ArrayList<UserItem> userList3; //21-30기
    private ArrayList<UserItem> userList4; //31-40기
    private ArrayList<UserItem> userList5; //41-50기
    private ArrayList<UserItem> userList6; //51-60기
    private ArrayList<UserItem> userList7; //61기-70기
    private ArrayList<UserItem> userList8; //71기-

    private UserListAdapter userListAdapter;
    private RecyclerView recyclerView;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private final int NEW_CAHT = 1;
    private final int INVITE_CHAT = 2;
    private int mode=0;
    private String callUserName;
    private String receivedKey;
    private String chatRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);
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
        final EditText editText = new EditText(ChatUserListAcitivity.this);
        AlertDialog.Builder dlg = new AlertDialog.Builder(ChatUserListAcitivity.this);
        dlg.setTitle("채팅방 이름 입력"); //제목
        dlg.setMessage("새로 생성할 채팅방 이릅을 입력해주세요.");
        dlg.setView(editText);
        dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chatRoomName = editText.getText().toString();
                finish(); //액티비티 종료
            }
        });
        dlg.setNegativeButton("취소",null);
        dlg.show();
    }
    private void getAllUserList(){
        // TODO : 유저 리스트 받아오기
        userList = new ArrayList<UserItem>();
        ArrayList<User> users = new ArrayList<User>();
        //firebase에서 users데이터 받아오기

        for (int i = 0; i<users.size() ;i++){
            //usermeta를 userList에 넣기
            //user클래스를 userItem 생성자에 넣으면  userItem형식으로 객체 생성가능
            //userList.add(new UserItem(users.get(i)));
        }
        //테스트용 데이터 - 나중에 삭제 해야됨
        userList.add(new UserItem("user1","https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png",81,"hje"));
        userList.add(new UserItem("user2","",10,"whs"));
        userList.add(new UserItem("user3","",30,"rke"));
        userList.add(new UserItem("user4","https://t1.daumcdn.net/cfile/blog/2455914A56ADB1E315",20,"df"));
        userList.add(new UserItem("user5","https://t1.daumcdn.net/cfile/blog/216CB83A54295C1C0E",40,"rkwere"));

        //기수별로 분리
        categorization();
    }
    private void categorization(){
        //기수별로 분리
        userList1 = new ArrayList<UserItem>();
        userList2 = new ArrayList<UserItem>();
        userList3 = new ArrayList<UserItem>();
        userList4 = new ArrayList<UserItem>();
        userList5 = new ArrayList<UserItem>();
        userList6 = new ArrayList<UserItem>();
        userList7 = new ArrayList<UserItem>();
        userList8 = new ArrayList<UserItem>();
        for(UserItem i : userList){
            switch ((i.getGeneration()-1)/10){ //casting
                case 0 :
                    userList1.add(i);
                    break;
                case 1 :
                    userList2.add(i);
                    break;
                case 2 :
                    userList3.add(i);
                    break;
                case 3 :
                    userList4.add(i);
                    break;
                case 4 :
                    userList5.add(i);
                    break;
                case 5 :
                    userList6.add(i);
                    break;
                case 6 :
                    userList7.add(i);
                    break;
                default :
                    userList8.add(i);
                    break;
            }
        }
    }
    private void showUserList() {

        //초기화 및 데이터 불러오기
        getAllUserList();

        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerUserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        userListAdapter = new UserListAdapter(getApplicationContext(),userList);
        recyclerView.setAdapter(userListAdapter);

        //스피너 설정
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,items);

        //항목 선택시 보이는 별도창의 각 아이템을 위한 레이아웃 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //아이템이 선택되면
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // int i : item의 순서대로 0번부터 n-1번까지
                switch (i){
                    case 1: //1-10기
                        userListAdapter.setUserList(userList1);
                        break;
                    case 2: //11-20기
                        userListAdapter.setUserList(userList2);
                        break;
                    case 3: //21-30기
                        userListAdapter.setUserList(userList3);
                        break;
                    case 4: //31-40기
                        userListAdapter.setUserList(userList4);
                        break;
                    case 5: //41-50기
                        userListAdapter.setUserList(userList5);
                        break;
                    case 6: //51-60기
                        userListAdapter.setUserList(userList6);
                        break;
                    case 7: //61기-70기
                        userListAdapter.setUserList(userList7);
                        break;
                    case 8: //71기-
                        userListAdapter.setUserList(userList8);
                        break;
                    default: //전체
                        userListAdapter.setUserList(userList);
                        break;
                }
            }
            //스피너에서 아무것도 선택되지 않은 상태일때
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userListAdapter.setUserList(userList);
            }
        });

        /*검색 기능 추가*/
        EditText contents = (EditText)findViewById(R.id.searchText);
        contents.addTextChangedListener(this);

        /*텍스트뷰 내용 지우기*/
        ImageButton search = (ImageButton) findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                contents.setText(null);
            }
        });
    }
    private ArrayList<UserItem> returnChoose(){
        ArrayList<UserItem> choose = new ArrayList<>();
        for(UserItem i : userList){
            if(i.getChecked())
                choose.add(i);
        }
        return choose;
    }
    private void createChatRoom(){
        //TODO : 새 채팅방 생성
        //체크박스로 표시된 유저 정보를 받아옴.
        ArrayList<UserItem> list = returnChoose();
        //채팅방 만들기 누른 유저 정보 : callUserName
        String message = callUserName+"님이 채팅방"+""+"를 생성하셨습니다.";
        //새 ChatRoom 생성
        //chatRoomJoined에 list의 유저 추가-> list.get(i).getUserKey() 사용
        //list의 user의 userJoined에 생성된 채팅방 정보 추가
        //생성메세지(message) 현재 채팅방에 시스템 메세지로 추가
    }
    private void inviteChatRoom(){
        //TODO : 초대한 유저를 해당 채팅방에 추가
        //체크박스로 표시된 유저 정보를 받아옴
        ArrayList<UserItem> list = returnChoose();
        //초대하기 누른 유저 정보 : callUserName
        //changeToString : 유저리스트를 ~님, 형식으로 바꿔줌.
        String message = callUserName+"님이 "+changeToString(list)+"님을 초대하셨습니다.";
        //chatRoomJoined에 list의 유저 추가-> list.get(i).getUserKey() 사용
        //list의 user의 userJoined에 현재 채팅방 정보 추가->receivedKey 사용
        //초대메세지(message) 현재 채탕방에 시스템 메세지로 추가.
    }
    private String changeToString(ArrayList<UserItem> list){
        //유저리스트를 ~님, 형식으로 바꿔서 String으로 반환해줌.
        String result="";
        for(UserItem i : list){
            result = result + i.getName()+"님, ";
        }
        result=result.substring(0,result.length()-3);
        return result;
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
                    Toast.makeText(ChatUserListAcitivity.this,"초대할 사람을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(mode==1){
                        //채팅방 만들기
                        inputChatRoomName();
                        createChatRoom();
                    }
                    else if(mode==2){
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
