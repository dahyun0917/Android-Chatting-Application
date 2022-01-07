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

import com.example.chat_de.datas.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatUserListAcitivity extends AppCompatActivity implements TextWatcher {
    public static Context context;

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

    private int mode = 1;
    private String callUserName;
    private String receivedKey;
    private String chatRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);
        context = this;

        //인텐트로 mode값 , 초대/생성하는 User 정보 받아오기기
        Intent getintent = getIntent();
        callUserName = getintent.getStringExtra("who");

        if(mode==1){
            //채팅방 만들기
            ActionBar ab = getSupportActionBar() ;
            ab.setTitle("새 채팅방 만들기") ;
        }
        else if(mode==2){
            //초대하기
            ActionBar ab = getSupportActionBar() ;
            ab.setTitle("초대하기") ;
            receivedKey = getintent.getStringExtra("where");
        }
        else{
            Log.e("ERROR MODE","Mode값은 1또는 2만 가능합니다.");
        }
        showUserList();
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
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
        userList.add(new UserItem("user2","data:image/jpeg;base64,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",10,"whs"));
        userList.add(new UserItem("user3","http://t1.daumcdn.net/friends/prod/editor/dc8b3d02-a15a-4afa-a88b-989cf2a50476.jpg",30,"rke"));
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /*검색 설정*/
        userListAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {  }
}
