package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private int mode = 0;
    public Button completeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);
        context = this;

        showUserList();

        if(mode==1){
            //채팅방 만들기
            createChatRoom();
            createChatRoomToF();
        }
        else if(mode==2){
            //초대하기
            updateChatRoom();
            updateChatRoom();
        }
    }

    private void getAllUserList(){
        // TODO : 유저 리스트 받아오기
        userList = new ArrayList<UserItem>();
        ArrayList<User> users = new ArrayList<User>();
        //firebase에서 users데이터 받아오기

        for (int i = 0; i<users.size() ;i++){
            //usermeta를 userList에 넣기
            userList.add(new UserItem(users.get(i).getUserMeta()));
        }
        //임시방편
        userList.add(new UserItem("user1","",81,"hje"));
        userList.add(new UserItem("user2","",10,"whs"));
        userList.add(new UserItem("user3","",30,"rke"));
        userList.add(new UserItem("user4","",20,"df"));
        userList.add(new UserItem("user5","",40,"rkwere"));
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
                Log.d("TAG", String.valueOf(i));
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
            //아무것도 선택되지 않은 상태일때
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userListAdapter.setUserList(userList);
                Log.d("TAG", "선택안됨");
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

    private void createChatRoom(){

    }

    private void createChatRoomToF(){}

    private void updateChatRoom(){}

    private void updateChatRoomToF(){}

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
