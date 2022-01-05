package com.example.chat_de;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_de.datas.UserMeta;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    private TextView userProfileImage = itemView.findViewById(R.id.userProfileImage);
    private TextView userNameTextView = itemView.findViewById(R.id.userNameText);
    private TextView userGenerationText = itemView.findViewById(R.id.userGenerationText);


    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    void bind(UserMeta userMeta){
        //데이터와 뷰를 묶음
        userMeta.setName((String) userNameTextView.getText());
    }
}
