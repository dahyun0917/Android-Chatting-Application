package com.example.chat_de;

import static java.lang.String.valueOf;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemRecyclerUserListBinding;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    protected ItemRecyclerUserListBinding itemBinding;

    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
        itemBinding = ItemRecyclerUserListBinding.bind(itemView);

    }

    //데이터와 뷰를 묶음
    void bind(UserListItem userListItem){

        itemBinding.userNameText.setText(userListItem.getName());
        itemBinding.userGenerationText.setText(String.valueOf(userListItem.getGeneration()));
        itemBinding.checkBox.setChecked(userListItem.getChecked());

        // 이미지뷰와 실제 이미지 데이터를 묶는다 .
        Glide
            .with(this.itemView.getContext())
            .load(userListItem.getPictureURL())
//            .centerCrop()
            .placeholder(R.drawable.knu_mark)
            .into(itemBinding.userProfileImage);
    }
}
