package com.example.chat_de;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemListJoinUserBinding;
import com.example.chat_de.datas.AUser;

import java.util.ArrayList;

public class JoinUserListAdapter extends BaseAdapter {
    private ArrayList<AUser> myUserList;

    // 생성할 클래스
    JoinUserListAdapter(ArrayList<AUser> userList){
        myUserList = userList;
    }

    @Override
    public int getCount() {
        return myUserList.size();
    }

    @Override
    public Object getItem(int i) {
        return myUserList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        AUser item = myUserList.get(position);
        ItemListJoinUserBinding itemListJoinUserBinding;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_join_user, viewGroup,false);
        }
        itemListJoinUserBinding = ItemListJoinUserBinding.bind(view);
        itemListJoinUserBinding.me.setVisibility(View.VISIBLE);
        itemListJoinUserBinding.alertDialogItemTextView.setText(item.getName());

        Glide.with(context)
                .load(item.getPictureURL())
                .circleCrop()
                .error(R.drawable.knu_mark)
                .into(itemListJoinUserBinding.alertDialogItemImageView);

        //사용자 자신을 알려주는 '(나)' text를 보이지 않도록 설정
        if(!item.getUserKey().equals(ChatDB.getCurrentUserKey())) {
            itemListJoinUserBinding.me.setVisibility(View.GONE);
        }

        return view;
    }


}
