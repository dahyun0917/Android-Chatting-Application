package com.example.chat_de;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.chat_de.databinding.FragmentChatRoomListBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatRoomListFragment extends Fragment {
    //private String userKey = "user2";
    //private String userName = "user2";

    private ChatRoomListActivity ChatActivity;
    private FragmentChatRoomListBinding binding;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    //private String CHAT_NAME;
    //private String USER_NAME;

    private String userKey;


    ArrayList<String> cList = new ArrayList<>();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ChatActivity = (ChatRoomListActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ChatActivity = null;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatRoomListBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        //ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_chat_room_list,container,false);

        Bundle bundle = getArguments();

        /*if (bundle != null) {
            CHAT_NAME = bundle.getString("chat_name");
            USER_NAME = bundle.getString("user_name");
        }*/
        if (bundle != null) {
            userKey=bundle.getString("userKey");
        }

        showChatRoomList();
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enterChatRoom(cList.get(i));
            }
        });
        /*binding.makeChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selelctUser();
            }
        });*/



        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void showChatRoomList() {
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter= new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        binding.listview.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        // USER_NAME을 나중에 실제 user의 primary key로 변경해야 함
        databaseReference.child("pre_2").child("userJoined").child(userKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add(dataSnapshot.getKey());
                cList.add(dataSnapshot.getKey());
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
    private void enterChatRoom(String chatRoomKey){
        Intent intent = new Intent(getActivity(), RoomActivity.class);
        intent.putExtra("chatRoomKey", chatRoomKey);
        getActivity().startActivity(intent);
    }
    /*private void selelctUser(){
        //ChatUserListActivity로 넘어간 뒤, 종료
        Intent intent = new Intent(getActivity(), UserListActivity.class);
        intent.putExtra("tag",1);
        intent.putExtra("who",userName);
        getActivity().startActivity(intent);
    }*/



}
