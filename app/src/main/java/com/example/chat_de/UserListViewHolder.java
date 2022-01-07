package com.example.chat_de;

import static java.lang.String.valueOf;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    //private ImageView userProfileImage;
    private TextView userNameTextView;
    private TextView userGenerationText;
    protected CheckBox checkBox;
    protected ImageView userProfileImage;

    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
        //userProfileImage = itemView.findViewById(R.id.userProfileImage);
        userNameTextView = itemView.findViewById(R.id.userNameText);
        userGenerationText = itemView.findViewById(R.id.userGenerationText);
        checkBox = itemView.findViewById(R.id.checkBox);
        userProfileImage = itemView.findViewById(R.id.userProfileImage);
    }

    //데이터와 뷰를 묶음
    void bind(UserItem userItem){

        userNameTextView.setText(userItem.getName());
        userGenerationText.setText(String.valueOf(userItem.getGeneration()));
        checkBox.setChecked(userItem.getChecked());

        // 이미지뷰와 실제 이미지 데이터를 묶는다 .
        Glide
            .with(this.itemView.getContext())
            .load(userItem.getPictureURL())
//            .centerCrop()
            .placeholder(R.drawable.knu_mark)
            .into(userProfileImage);
    }
}
