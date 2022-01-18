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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityVideoFrameBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoFrameActivity extends AppCompatActivity {

    private ActivityVideoFrameBinding binding;
    private String fromName;
    private String passDate;
    private String videoViewUrl;
    private int screenTouchNum =0;

    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private long latestId = -1;

    private int downPushed =0;
    ProgressDialog loading;
    Uri videoUri=Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
    Uri sample=Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

    //PlayerView videoView;
    //실제 비디오를 플레이하는 객체의 참조 변수

    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //메뉴바 안뜨게
        setContentView(view);

        Intent getintent = getIntent();
        fromName = getintent.getStringExtra("fromName");
        passDate = getintent.getStringExtra("passDate");
        videoViewUrl = getintent.getStringExtra("imageView");
        videoUri=Uri.parse(videoViewUrl);


        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);


        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

        //테스트해보기
        binding.videoView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(screenTouchNum ==0) {
                    screenTouchNum =1;
                    binding.fromName.setVisibility(View.GONE);
                    binding.passDate.setVisibility(View.GONE);
                } else {
                    screenTouchNum =0;
                    binding.fromName.setVisibility(View.VISIBLE);
                    binding.passDate.setVisibility(View.VISIBLE);
                }
            }
        });
        //pv=findViewById(R.id.pv);

    }
    //현재 액티비티의 메뉴바를 메뉴바.xml과 붙이기
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_down_file, menu);
        return true;
    }
    //유저 추가 메뉴바 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.down_file:
                if(downPushed ==0){
                    downPushed =1;
                    downVideo();
                }
                else
                    Toast.makeText(this, "다운로드중입니다.",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }

    public void downVideo(){
        String filename;

        /*String StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String savePath = StoragePath + "/KNU_AMP";
        File f = new File(savePath);
        if (!f.isDirectory()) f.mkdirs();*/
        Toast.makeText(this, "다운로드 시작되었습니다.",Toast.LENGTH_SHORT).show();
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

        String localPath = "/KNU_AMP"+ "/video/" + filename + ".mp4";

        urlToDownload = Uri.parse(videoViewUrl);
        List<String> pathSegments = videoUri.getPathSegments();
        request = new DownloadManager.Request(videoUri);
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
            loading.dismiss();
            downPushed =0;
        }

    };

    //화면에 보이기 시작할때!!
    @Override
    protected void onStart() {
        super.onStart();

        player = new ExoPlayer.Builder(this).build();
        //플레이어뷰에게 플레이어 설정
        binding.videoView.setPlayer(player);



        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        player.setMediaItem(mediaItem);
        player.prepare();
        //player.play();
        //로딩이 완료되어 준비가 되었을 때
        //자동 실행되도록..
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {

            @Override
            public void onPlayerError(PlaybackException error) {
                Throwable cause = error.getCause();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // This is the request for which the error occurred.
                    DataSpec requestDataSpec = httpError.dataSpec;
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code,
                        // message and headers.
                        Log.d("Dahyun","yes");
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.
                        Log.d("Dahyun","no");
                    }
                }
                //Log.d("Dahyun","dahyun");
            }
        });
    }

    //화면에 안보일 때..
    @Override
    protected void onStop() {
        super.onStop();
        //플레이어뷰 및 플레이어 객체 초기화
        binding.videoView.setPlayer(null);
        player.release();
        player=null;
    }




}

