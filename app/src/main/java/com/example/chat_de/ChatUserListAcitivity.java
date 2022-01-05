package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.chat_de.datas.User;
import com.example.chat_de.datas.UserMeta;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatUserListAcitivity extends AppCompatActivity {
    String[] items = {"전체","1-10기","11-20기","21-30기","31-40기","41-50기","51-60기","61기-70기","71기~"};
    ArrayList<UserMeta> userList = new ArrayList<UserMeta>();

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_user_list);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
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
            }
            //아무것도 선택되지 않은 상태일때
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //리사이클러뷰 설정
        userList = getAllUserList();
    }

    private ArrayList<UserMeta> getAllUserList(){
        // TODO : 유저 리스트 받아오기
        ArrayList<UserMeta> userList = new ArrayList<UserMeta>();
        ArrayList<User> users = new ArrayList<User>();


        for (int i = 0; i<users.size() ;i++){
            //userList.add();
        }

        return userList;
    }

    private void showUserList(ArrayList<UserMeta> userList){

    }

    private void createChatRoom(){}

    private void createChatRoomToF(){}

    private void updateChatRoom(){}

    private void updateChatRoomToF(){}
}
