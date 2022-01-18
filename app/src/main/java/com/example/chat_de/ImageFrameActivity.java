package com.example.chat_de;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityImageFrameBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageFrameActivity extends AppCompatActivity {

    private ActivityImageFrameBinding binding;
    private String fromName;
    private String passDate;
    private String imageViewUrl;
    private int touchnum = 0;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private long latestId = -1;

    private int downPushed =0;

    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("");
        Intent getintent = getIntent();
        fromName = getintent.getStringExtra("fromName");
        passDate = getintent.getStringExtra("passDate");
        imageViewUrl = getintent.getStringExtra("imageView");

        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);


       /* SimpleDateFormat passDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String str= passDateFormat.format(passDate);  //TODO : 수정해야함*/

        Glide.with(this).load(imageViewUrl).thumbnail(Glide.with(this).load(R.drawable.loading)).into(binding.photoView);
        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);

        binding.photoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (touchnum == 0) {
                    touchnum = 1;
                    binding.fromName.setVisibility(View.GONE);
                    binding.passDate.setVisibility(View.GONE);
                } else {
                    touchnum = 0;
                    binding.fromName.setVisibility(View.VISIBLE);
                    binding.passDate.setVisibility(View.VISIBLE);
                }
            }
        });


    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }


    //현재 액티비티의 메뉴바를 메뉴바.xml과 붙이기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_down_file, menu);
        return true;
    }

    //유저 추가 메뉴바 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId) {
            case R.id.down_file:
                if(downPushed ==0){
                    downPushed =1;
                    downImage();
                }
                else
                    Toast.makeText(this, "다운로드중입니다.",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downImage() {
        String filename;

        /*String StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String savePath = StoragePath + "/KNU_AMP";
        File f = new File(savePath);
        if (!f.isDirectory()) f.mkdirs();*/

        loading = new ProgressDialog(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);
        //loading.setCancelable(false);
        loading.show();

        //파일 이름 :날짜_시간
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        filename = String.valueOf(sdf.format(day));

        //String fileUrl = imageViewUrl[0];

        /*//다운로드 폴더에 동일한 파일명이 존재하는지 확인
        if (new File(savePath + "/" + filename).exists() == false) {
        } else {
        }*/

        String localPath = "/KNU_AMP"+ "/" + filename + ".jpg";

        urlToDownload = Uri.parse(imageViewUrl);
        List<String> pathSegments = urlToDownload.getPathSegments();
        request = new DownloadManager.Request(urlToDownload);
        request.setTitle(filename); //제목
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,localPath); //다운로드한 파일을 저장할 경로를 지정
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs(); //디렉토리가 존재하지 않을 경우 디렉토리를 생성하도록 구현
        latestId = downloadManager.enqueue(request);



    }
    private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "download/KNU_AMP에 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            downPushed =0;
            loading.dismiss();
        }

    };


}