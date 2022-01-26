package com.example.chat_de;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityFileFrameBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileFrameActivity extends AppCompatActivity {
    private ActivityFileFrameBinding binding;

    private String fileUrl;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //메뉴바 안뜨게
        setContentView(view);


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
        //파일 이름 :날짜_시간_확장자포함 원래 파일 이름
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        //String extension = getExtension(fileUrl);
        String originalFileName = FileDB.getFileName(fileUrl);
        Log.d("name",originalFileName);
        //String filename = String.valueOf(sdf.format(day))+"."+ extension;

        //파일을 다운로드 받는 시간 + 원래 파일 이름 or 다운로드 받은 시간_원래 파일 이름
        String filename = sdf.format(day)+"_"+originalFileName;
        String localPath = "/KNU_AMP/file/" + filename;

        FileDB.downloadFile(downloadManager,fileUrl,filename,localPath);
    }

    //다운로드 완료되었을 때 작동
    private BroadcastReceiver completeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "download/KNU_AMP에 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
        }
    };
}
