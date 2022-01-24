package com.example.chat_de;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
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
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private long latestId = -1;


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

        //파일 이름 :날짜_시간_확장자
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String extension = getExtension(fileUrl);
        String originalFileName = getFileName(fileUrl);
        // Log.d("extension",extension);
        Log.d("name",originalFileName);
        //String filename = String.valueOf(sdf.format(day))+"."+ extension;
        String filename = String.valueOf(sdf.format(day))+"_"+originalFileName;


        String localPath = "/KNU_AMP/file/" + filename;


        urlToDownload = Uri.parse(fileUrl);
        request = new DownloadManager.Request(urlToDownload);
        request.setTitle(filename); //제목
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //알림창에 다운로드 중 , 다운로드 완료 창이 보이게 설정
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,localPath); //다운로드한 파일을 저장할 경로를 지정
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs(); //디렉토리가 존재하지 않을 경우 디렉토리를 생성하도록 구현
        latestId = downloadManager.enqueue(request); //latestID : 다운로드매니저 큐에 잘 들어갔는지 확인하는 변수로 사용하는 것으로 추정



    }

    //파일 확장자 가져오기
    public static String getExtension(String fileStr){
        //String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
        //uri 스트링의 마지막 . 뒤부터 마지막 ? 까지의 스트링을 받아옴
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.lastIndexOf("?"));
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }
    //파일 이름 가져오기
    public static String getFileName(String fileStr){
        String fileName = null;
        fileName = fileStr.substring(fileStr.lastIndexOf("_")+1);

        return fileName;
    }


    //다운로드 완료되었을 때 작동
    private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "download/KNU_AMP에 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
        }

    };
}
