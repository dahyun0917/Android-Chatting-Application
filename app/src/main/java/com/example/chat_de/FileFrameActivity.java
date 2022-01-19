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
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityFileFrameBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileFrameActivity extends AppCompatActivity {
    private ActivityFileFrameBinding binding;

    private String fileUrl;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private long latestId = -1;


    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("");

        Intent getIntent = getIntent();
        fileUrl = getIntent.getStringExtra("file");

        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

        Toast.makeText(this, "다운로드 시작되었습니다.",Toast.LENGTH_SHORT).show();

        downFile();
        finish();

    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }
    public void downFile() {
        String filename;

        /*loading = new ProgressDialog(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);
        //loading.setCancelable(false);
        loading.show();*/

        //파일 이름 :날짜_시간_확장자
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String extension = getExtension(fileUrl);
        Log.d("extension",extension);
        filename = String.valueOf(sdf.format(day))+"."+ extension;


        String localPath = "/KNU_AMP/file/" + filename;


        urlToDownload = Uri.parse(fileUrl);
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

    private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "download/KNU_AMP에 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            //loading.dismiss();
        }

    };
}
