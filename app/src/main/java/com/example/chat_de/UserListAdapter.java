package com.example.chat_de;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> implements Filterable{
    Context context;
    private ArrayList<UserItem> filteredUsers = new ArrayList<>(); //필터링된 리스트 -> 보여줄 리스트
    private ArrayList<UserItem> unFilteredUsers = new ArrayList<>(); //필터링되지않은 리스트

    //뷰홀더가 생성 됐을 때
    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new UserListViewHolder(view);
    }

    //아이템 개수를 조회
    @Override
    public int getItemCount() {
        return (this.filteredUsers.size());
    }

    public int getItemViewType(int position) {
        return position;
    }

    //뷰와 뷰홀더가 묶였을 때
    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        final UserItem item = filteredUsers.get(position);

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
        holder.bind(this.filteredUsers.get(position));
        //Log.d("TAG","position"+String.valueOf(position));
    }

    //외부에서 데이터 넘기기
    public void setUserList(ArrayList<UserItem> userList){
        this.unFilteredUsers = userList;
        notifyDataSetChanged();
    }
    //생성자
    public UserListAdapter(Context context, ArrayList<UserItem> list) {
        super();
        this.context = context;
        this.unFilteredUsers = list;
        this.filteredUsers = list;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString(); //입력된 스트링
                if(charString.isEmpty()){
                    //비어있다면 필터링 되지 않은 리스트를 필터링 된 리스트로 사용
                    filteredUsers = unFilteredUsers;
                }else{
                    ArrayList<UserItem> filteringList = new ArrayList<>(); //필터링중인 리스트
                    for(int i=0;i<unFilteredUsers.size();i++) {
                        String name = unFilteredUsers.get(i).getName();
                        if(name.toLowerCase().contains(charString.toLowerCase())){
                            filteringList.add(unFilteredUsers.get(i));
                        }
                    }
                    filteredUsers = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredUsers;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //리사이클러뷰 업데이트
                filteredUsers = (ArrayList<UserItem>)filterResults.values;
                notifyDataSetChanged();
            }
        };

    }
}
