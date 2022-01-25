package com.example.chat_de;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemListChatRoomBinding;
import com.example.chat_de.datas.ChatRoomMeta;

import java.util.ArrayList;

public class ChatRoomListAdapter extends BaseAdapter implements IChatRoomListChangedListener{
    ArrayList<ChatRoomListItem> chatRoomList;

    public ChatRoomListAdapter(ArrayList<ChatRoomListItem> chatRoomList){ //생성자
        this.chatRoomList = chatRoomList;
    }
    @Override
    public int getCount() {
        return chatRoomList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatRoomList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void ChatRoomAdded(String key, ChatRoomMeta chatRoomMeta) {
        ChatRoomListItem item = new ChatRoomListItem(key, chatRoomMeta.getPictureURL(), chatRoomMeta.getName());
        chatRoomList.add(item);
        this.notifyDataSetChanged();
    }
    @Override
    public void ChatRoomChanged(String key, ChatRoomMeta chatRoomMeta) {
        ChatRoomListItem item = new ChatRoomListItem(key, chatRoomMeta.getPictureURL(), chatRoomMeta.getName());
        int size=chatRoomList.size();
        int pos = 0;
        while (pos < size){
            if(chatRoomList.get(pos).getChatRoomKey().equals(key)){
                chatRoomList.set(pos,item);
                break;
            }
            pos++;
        }
        this.notifyDataSetChanged();
    }
    @Override
    public void ChatRoomRemoved(String key) {
        int size=chatRoomList.size();
        int pos = 0;
        while (pos < size){
            if(chatRoomList.get(pos).getChatRoomKey().equals(key)){
                chatRoomList.remove(pos);
                break;
            }
            pos++;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) { //position에 위치한 데이터를 화면에 출력하는데 사용할 view를 리턴
        final Context context = viewGroup.getContext();
        ChatRoomListItem item = chatRoomList.get(i);
        ItemListChatRoomBinding itemListChatRoomBinding;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_chat_room, viewGroup,false);
        }
        itemListChatRoomBinding = ItemListChatRoomBinding.bind(view);
        itemListChatRoomBinding.chatRoomName.setText(item.getChatRoomName());

        Glide
                .with(context)
                .load(item.getChatRoomPictureURL())
                .circleCrop()
                .placeholder(R.drawable.knu_mark)
                .into(itemListChatRoomBinding.chatRoomImage);

        return view;
    }
}
