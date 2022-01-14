package com.example.chat_de;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityImageFrameBinding;
import com.example.chat_de.databinding.ActivityUserListBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFrameActivity extends AppCompatActivity {

    private ActivityImageFrameBinding binding;
    private String fromName;
    private String passDate;
    private String imageViewUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent getintent = getIntent();
        fromName = getintent.getStringExtra("fromName");
        passDate = getintent.getStringExtra("passDate");
        imageViewUrl = getintent.getStringExtra("imageView");

       /* SimpleDateFormat passDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String str= passDateFormat.format(passDate);  //TODO : 수정해야함*/

        Glide.with(this).load(imageViewUrl).into(binding.photoView);
        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);

    }
}
