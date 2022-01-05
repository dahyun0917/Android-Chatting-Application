package com.example.chat_de;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_de.datas.UserMeta;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> implements Filterable {

    private ArrayList<UserItem> userList;

    //뷰홀더가 생성 됐을 때
    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new UserListViewHolder(view);
    }

    //아이템 개수를 조회
    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

    //뷰와 뷰홀더가 묶였을 때
    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        final UserItem item = userList.get(position);

        holder.checkBox.setOnCheckedChangeListener(null);

        //모델 클래스의 getter로 체크박스 상태값을 가져옴.
        holder.checkBox.setChecked(item.getChecked());

        //체크상태의 상태값을 알기위한 리스너
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //체크상태가 바뀌면 item의 checked값도 바뀜.
                item.setChcked(compoundButton.isChecked());
            }
        });
        holder.bind(this.userList.get(position));
        //Log.d("TAG","position"+String.valueOf(position));
    }

    //외부에서 데이터 넘기기
    public void setUserList(ArrayList<UserItem> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
