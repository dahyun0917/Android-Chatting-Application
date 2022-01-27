package com.example.chat_de;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemRecyclerSelectedUserBinding;
import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.User;

import java.util.ArrayList;

public class SelectedListAdapter extends RecyclerView.Adapter<SelectedListAdapter.SelectedListViewHolder> {
    Context context;
    private ArrayList<UserListItem> selectedUsers;
    UserSelectListener listener;

    //생성자
    public SelectedListAdapter(Context context, ArrayList<UserListItem> userList) {
        super();
        this.context = context;
        this.selectedUsers = userList;
    }

    public void setListener(UserSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_selected_user, parent, false);
        return new SelectedListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedListViewHolder holder, int position) {
        holder.bind(this.selectedUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }
    public int getItemViewType(int position) {
        return position;
    }

    public class SelectedListViewHolder extends RecyclerView.ViewHolder{
        protected ItemRecyclerSelectedUserBinding itemBinding;
        public SelectedListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemRecyclerSelectedUserBinding.bind(itemView);
        }
        void bind(UserListItem user) {
            itemBinding.selectedUserName.setText(user.getName());
            Glide.with(this.itemView.getContext())
                    .load(user.getPictureURL())
                    .circleCrop()
                    .placeholder(R.drawable.knu_mark)
                    .into(itemBinding.selectedProfileImage);
            itemBinding.selectCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.setChecked(false);
                    Log.d("TAG","Click!"+String.valueOf(user.getChecked()));
                    listener.onUnCheckedClick(user.getUserKey());
                }
            });
        }
    }
}
