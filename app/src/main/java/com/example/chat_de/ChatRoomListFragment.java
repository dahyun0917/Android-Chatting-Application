package com.example.chat_de;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.chat_de.databinding.FragmentChatRoomListBinding;
import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoomListFragment extends Fragment {
    private FragmentChatRoomListBinding binding;
    private ChatRoomListAdapter chatRoomListAdapter;
    private String userKey;
    private ArrayList<ChatRoomListItem> chatRoomList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatRoomListBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        userKey = ChatDB.getCurrentUserKey();

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                enterChatRoom(chatRoomList.get(i).getChatRoomKey());
            }
        });
        if(!ChatDB.getAdminMode())
            binding.fab.hide();
        else {
            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selelctUser();
                }
            });
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void showChatRoomList() {
        // 리스트 어댑터 생성 및 세팅
        chatRoomList.clear();
        chatRoomListAdapter = new ChatRoomListAdapter(chatRoomList);
        binding.listview.setAdapter(chatRoomListAdapter);
        ChatDB.chatRoomListChangedEventListener(userKey, chatRoomListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showChatRoomList();
    }

    @Override
    public void onPause() {
        super.onPause();
        ChatDB.removeEventListenerBindOnThis();
    }

    private void enterChatRoom(String chatRoomKey){
        Intent intent = new Intent(getActivity(), RoomActivity.class);
        intent.putExtra("chatRoomKey", chatRoomKey);
        getActivity().startActivity(intent);
    }

    private void selelctUser(){
        //RoomActivity로 넘어간 뒤, 종료
        Intent intent = new Intent(getActivity(), UserListActivity.class);
        intent.putExtra("tag",1);
        HashSet<String> set = new HashSet<>();
        set.add(userKey);
        intent.putExtra("userList", set);
        getActivity().startActivity(intent);
    }

}
