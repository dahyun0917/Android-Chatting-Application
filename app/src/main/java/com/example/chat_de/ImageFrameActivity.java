package com.example.chat_de;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityImageFrameBinding;
import com.example.chat_de.databinding.ActivityUserListBinding;

public class ImageFrameActivity extends AppCompatActivity {

    private ActivityImageFrameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}
