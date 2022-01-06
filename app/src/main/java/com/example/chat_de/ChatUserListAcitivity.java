package com.example.chat_de;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    String[] items = {"전체","1-10기","11-20기","21-30기","31-40기","41-50기","51-60기","61기-70기","71기~"};
    ArrayList<UserItem> userList = new ArrayList<>();

    private UserListAdapter userListAdapter;
    private RecyclerView recyclerView;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);

        /*리사이클러뷰 설정*/

        //데이터 받아오기
        // TODO: userList = getAllUserList();
        userList.add(new UserItem("user1","",81,"hje"));
        userList.add(new UserItem("user2","",10,"whs"));
        userList.add(new UserItem("user3","",30,"rke"));
        userList.add(new UserItem("user4","",20,"df"));
        userList.add(new UserItem("user5","",40,"rkwere"));
        //어댑터 인스턴스 생성
        userListAdapter = new UserListAdapter(getApplicationContext(),userList);
        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerUserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(userListAdapter);

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


        /*스피너 설정*/
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,items
        );
        //항목 선택시 보이는 별도창의 각 아이템을 위한 레이아웃 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //아이템이 선택되면
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // int i : item의 순서대로 0번부터 n-1번까지
                //userListAdapter.setUserList(this.userList);
            }
            //아무것도 선택되지 않은 상태일때
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /*검색 설정*/
        userListAdapter.getFilter().filter(charSequence);
        Log.d("TEST","Text Changed");
    }

    @Override
    public void afterTextChanged(Editable editable) {  }


    private ArrayList<UserItem> getAllUserList(){
        // TODO : 유저 리스트 받아오기
        ArrayList<UserItem> userList = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();


        for (int i = 0; i<users.size() ;i++){
            //userList.add();
        }

        return userList;
    }

    private void showUserList(ArrayList<UserItem> userList){

    }

    private void createChatRoom(){}

    private void createChatRoomToF(){}

    private void updateChatRoom(){}

    private void updateChatRoomToF(){}
}
