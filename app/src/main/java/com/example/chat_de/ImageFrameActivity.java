package com.example.chat_de;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chat_de.databinding.ActivityImageFrameBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageFrameActivity extends AppCompatActivity {

    private ActivityImageFrameBinding binding;
    private String fromName;
    private String passDate;
    private String imageViewUrl;
    private int touchNum = 0;
    private DownloadManager downloadManager;
    private long latestId = -1;
    private boolean downloadCancle = false;

    private int downPushed =0;

    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //메뉴바 안뜨게
        setContentView(view);


        Intent getIntent = getIntent();
        fromName = getIntent.getStringExtra("fromName");
        passDate = getIntent.getStringExtra("passDate");
        imageViewUrl = getIntent.getStringExtra("imageView");



        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);


        loading = new ProgressDialog(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);  //로딩 중 화면 눌렀을 때 로딩바 취소되지 않음
        //loading.setCancelable(false);  //로딩 중 뒤로가기 버튼 눌렀을 때 로딩바 취소되지 않음
        loading.show();



        Glide.with(this)
                .load(imageViewUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        binding.downloads.setVisibility(View.GONE);
                        loading.dismiss();
                        Toast.makeText(ImageFrameActivity.this, "이미지 로딩 실패",Toast.LENGTH_SHORT).show();
                       // Glide.with(ImageFrameActivity.this).load(R.drawable.no).into(binding.photoView);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loading.dismiss();
                        return false;
                    }
                })
                .into(binding.photoView);


        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);

        binding.photoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (touchNum == 0) {
                    touchNum = 1;
                    binding.fromName.setVisibility(View.GONE);
                    binding.passDate.setVisibility(View.GONE);
                    binding.toolbar.setVisibility(View.GONE);
                } else {
                    touchNum = 0;
                    binding.fromName.setVisibility(View.VISIBLE);
                    binding.passDate.setVisibility(View.VISIBLE);
                    binding.toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.downloads.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                binding.downloads.setVisibility(View.GONE);
                binding.downloadCancle.setVisibility(View.VISIBLE);
                downImage();

            }
        });

        binding.downloadCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.downloads.setVisibility(View.VISIBLE);
                binding.downloadCancle.setVisibility(View.GONE);
                downloadCancle =true;
                //Toast.makeText(view.getContext(), "다운로드 취소",Toast.LENGTH_SHORT).show();
                downloadManager.remove(latestId);
            }
        });

        /*String extension = getExtension(imageViewUrl);
        Log.d("extension",imageViewUrl);
        Log.d("extension",extension);*/
    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }



    public void downImage() {
        String filename;

        Toast.makeText(this, "다운로드 시작되었습니다.",Toast.LENGTH_SHORT).show();


        //파일 이름 :날짜_시간_확장자
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String extension = FileDB.getExtension(imageViewUrl);
        // Log.d("extension",extension);
        filename = String.valueOf(sdf.format(day))+"."+ extension;


        String localPath = "/KNU_AMP/image" + filename;


        latestId=FileDB.downloadFile(downloadManager,imageViewUrl,filename,localPath);

    }


    private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            binding.downloads.setVisibility(View.VISIBLE);
            binding.downloadCancle.setVisibility(View.GONE);
            if(!downloadCancle) Toast.makeText(context, "다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            else if( downloadCancle ) {
                downloadCancle =false;
                Toast.makeText(context, "다운로드 취소.",Toast.LENGTH_SHORT).show();
            }
        }

    };


}