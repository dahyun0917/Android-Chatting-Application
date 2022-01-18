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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.chat_de.databinding.FragmentChatRoomListBinding;
import com.example.chat_de.datas.ChatRoomMeta;
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

    private ArrayList<ChatRoomListItem> chatRoomList = new ArrayList<>();

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
                enterChatRoom(chatRoomList.get(i).getChatRoomKey());
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void showChatRoomList() {
        // 리스트 어댑터 생성 및 세팅
        ChatRoomListAdapter chatRoomListAdapter;
        chatRoomListAdapter = new ChatRoomListAdapter(chatRoomList);
        binding.listview.setAdapter(chatRoomListAdapter);

        // TODO LOGIN : userKey를 실제 로그인된 사용자의 키로 변경해야함.
        ChatDB.chatRoomListChangedEventListener(userKey, chatRoomListAdapter);
    }

    private void enterChatRoom(String chatRoomKey){
        Intent intent = new Intent(getActivity(), RoomActivity.class);
        intent.putExtra("chatRoomKey", chatRoomKey);
        getActivity().startActivity(intent);
    }

}
