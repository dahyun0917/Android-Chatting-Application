package com.example.chat_de;

import android.app.AlertDialog;
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
import com.example.chat_de.datas.AUser;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoomListFragment extends Fragment {
    private final int HASH_CODE = hashCode();
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
                    createChatRoom();
                }
            });
        }

        binding.listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                chatRoomSettingDialog(chatRoomList.get(i));
                return true;
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
        chatRoomList.clear();
        chatRoomListAdapter = new ChatRoomListAdapter(chatRoomList);
        binding.listview.setAdapter(chatRoomListAdapter);
        ChatDB.chatRoomListChangedEventListener(userKey, HASH_CODE, chatRoomListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        showChatRoomList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        ChatDB.removeEventListener(HASH_CODE);
    }

    private void enterChatRoom(String chatRoomKey){
        Intent intent = new Intent(getActivity(), RoomActivity.class);
        intent.putExtra("chatRoomKey", chatRoomKey);
        getActivity().startActivity(intent);
    }

    private void chatRoomSettingDialog(ChatRoomListItem chatRoomListItem) {
        String[] settings;
        if(!ChatDB.getAdminMode()) {
            settings = new String[]{"채팅방 나가기"};
        } else {
            settings = new String[]{"채팅방 나가기", "채팅방 정보 바꾸기", "채팅방 폐쇄하기"};
        }
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        dlg.setTitle(chatRoomListItem.getName()).setItems(settings, (dialogInterface, position) -> {
            switch(position) {
                case 0: //채팅방 나가기
                    ArrayList<AUser> user = new ArrayList<>();
                    user.add(ChatDB.getCurrentUser());
                    ChatDB.exitChatRoomCompleteListener(chatRoomListItem.getChatRoomKey(), user, () -> {});
                    break;
                case 1: //채팅방 이름 바꾸기
                    Intent intent = new Intent(getActivity(), CreateRoomMetaActivity.class);
                    intent.putExtra("chatRoomKey",chatRoomListItem.getChatRoomKey());
                    intent.putExtra("chatRoomName",chatRoomListItem.getName());
                    intent.putExtra("chatRoomPicture",chatRoomListItem.getPictureURL());
                    getActivity().startActivity(intent);
                    break;
                case 2: //채팅방 폐쇄하기
                    AlertDialog.Builder cautionDialog = new AlertDialog.Builder(getActivity());
                    cautionDialog.setTitle("경고").setMessage("채팅방을 폐쇄하면 모든 사용자가 해당 채팅방에서 강제퇴장 당하고, 채팅방의 모든 정보가 영구적으로 삭제됩니다. 계속 진행하시겠습니까?");
                    cautionDialog.setPositiveButton("예", (dialog, id) -> {
                        ChatDB.closeChatRoomCompleteListener(chatRoomListItem.getChatRoomKey(), () -> {});
                    });
                    cautionDialog.setNegativeButton("아니오", (a, b) -> {});
                    cautionDialog.show();
            }
        }).show();
    }

    private void createChatRoom(){
        //UserListActivity로 넘어간 뒤, 종료
        Intent intent = new Intent(getActivity(), UserListActivity.class);
        intent.putExtra("tag",1);
        HashSet<String> set = new HashSet<>();
        set.add(userKey);
        intent.putExtra("userList", set);
        getActivity().startActivity(intent);
    }
}
