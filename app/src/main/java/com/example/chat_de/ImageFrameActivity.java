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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //메뉴바 안뜨게
        setContentView(view);


        Intent getIntent = getIntent();
        fromName = getIntent.getStringExtra("fromName");
        passDate = getIntent.getStringExtra("passDate");
        imageViewUrl = getIntent.getStringExtra("imageView");



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

        binding.downloads.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(downPushed ==0){
                    downPushed =1;
                    downImage();
                }
                else
                    Toast.makeText(getApplicationContext(),"다운로드중입니다.",Toast.LENGTH_SHORT).show();

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

        loading = new ProgressDialog(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);
        //loading.setCancelable(false);
        loading.show();

        //파일 이름 :날짜_시간_확장자
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String extension = getExtension(imageViewUrl);
        Log.d("extension",extension);
        filename = String.valueOf(sdf.format(day))+"."+ extension;


        String localPath = "/KNU_AMP/image" + filename;


        urlToDownload = Uri.parse(imageViewUrl);
        request = new DownloadManager.Request(urlToDownload);
        request.setTitle(filename); //제목
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,localPath); //다운로드한 파일을 저장할 경로를 지정
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs(); //디렉토리가 존재하지 않을 경우 디렉토리를 생성하도록 구현
        latestId = downloadManager.enqueue(request);



    }

    //파일 확장자 가져오기
    public static String getExtension(String fileStr){
        //String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.lastIndexOf("?"));
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }

    //TODO: 토스트 메세지 수정
    private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "download/KNU_AMP에 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            downPushed =0;
            loading.dismiss();
        }

    };


}