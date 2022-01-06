package com.example.chat_de;

import static java.lang.String.valueOf;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_de.datas.UserMeta;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    //private ImageView userProfileImage;
    private TextView userNameTextView;
    private TextView userGenerationText;
    protected CheckBox checkBox;

    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
        //userProfileImage = itemView.findViewById(R.id.userProfileImage);
        userNameTextView = itemView.findViewById(R.id.userNameText);
        userGenerationText = itemView.findViewById(R.id.userGenerationText);
        checkBox = itemView.findViewById(R.id.checkBox);
    }

    //데이터와 뷰를 묶음
    void bind(UserItem userItem){

        userNameTextView.setText(userItem.getName());
        userGenerationText.setText(String.valueOf(userItem.getGeneration()));
        checkBox.setChecked(userItem.getChecked());

        // TODO 사진 설정 해줘야됨
        //userMeta.setPictureURL(String.valueOf(userProfileImage.getResources()));
    }
}
