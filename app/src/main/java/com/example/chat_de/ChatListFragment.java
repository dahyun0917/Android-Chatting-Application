package com.example.chat_de;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    ChatListActivity ChatActivity;



    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    //private String CHAT_NAME;
    //private String USER_NAME;

    private ListView chatList;
    private Button makeChat;

    ArrayList<String> clist = new ArrayList<>();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ChatActivity = (ChatListActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ChatActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.chat_list,container,false);

        Bundle bundle = getArguments();

        /*if (bundle != null) {
            CHAT_NAME = bundle.getString("chat_name");
            USER_NAME = bundle.getString("user_name");
        }*/


        chatList = (ListView) rootview.findViewById(R.id.listview);
        makeChat=(Button) rootview.findViewById(R.id.makeChat);

        showChatRoomList();
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enterChatRoom(i);
            }
        });
        makeChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selelctUser();
            }
        });



        return rootview;
    }
    private void showChatRoomList() {
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter= new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        chatList.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("pre_1").child("users").child("user1").child("joined").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                adapter.add(dataSnapshot.getKey());
                clist.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    //key=childSnapshot.getKey();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void enterChatRoom(int chatRoomnum){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        //CHAT_NAME= clist.get(chatRoomnum);
        //intent.putExtra("chat_name",CHAT_NAME);
        //intent.putExtra("user_name",USER_NAME);
        getActivity().startActivity(intent);
        //getActivity().startActivity(new Intent(getActivity(), ChatActivity.class));
        getActivity().finish();
    }
    private void selelctUser(){
        //TODO("ChatUserListActivity로 넘어간 뒤, 종료")
        Intent intent = new Intent(getActivity(), ChatUserListAcitivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }



}
