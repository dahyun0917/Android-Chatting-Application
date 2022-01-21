package com.example.chat_de;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemRecyclerSelectedUserBinding;
import com.example.chat_de.datas.User;

import java.util.ArrayList;

public class SelectedListAdapter extends RecyclerView.Adapter<SelectedListAdapter.SelectedListViewHolder> {
    Context context;
    private ArrayList<User> selectedUsers;

    //생성자
    public SelectedListAdapter(Context context, ArrayList<User> userList) {
        super();
        this.context = context;
        this.selectedUsers = userList;
    }

    public void setSelectedUsers(ArrayList<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
        this.notifyDataSetChanged();
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
        void bind(User user) {
            itemBinding.selectedUserName.setText(user.getName());
            Glide
                    .with(this.itemView.getContext())
                    .load(user.getPictureURL())
                    .circleCrop()
                    .placeholder(R.drawable.knu_mark)
                    .into(itemBinding.selectedProfileImage);
        }
    }
}
