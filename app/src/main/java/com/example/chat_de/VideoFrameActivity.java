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
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityVideoFrameBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    Uri videoUri;

    //임의의 동영상 url
    //Uri videoUri=Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
    //Uri sample=Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

    //실제 비디오를 플레이하는 객체의 참조 변수

    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //메뉴바 안뜨게
        setContentView(view);



        Intent getIntent = getIntent();
        fromName = getIntent.getStringExtra("fromName");
        passDate = getIntent.getStringExtra("passDate");
        videoViewUrl = getIntent.getStringExtra("imageView");
        videoUri=Uri.parse(videoViewUrl);


        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);


        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

        //TODO:누르기 개선
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

        binding.downloads.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(downPushed ==0){
                    downPushed =1;
                    downVideo();
                }
                else
                    Toast.makeText(getApplicationContext(),"다운로드중입니다.",Toast.LENGTH_SHORT).show();

            }
        });
       /* String extension = getExtension(videoViewUrl);
        Log.d("extension",videoViewUrl);
        Log.d("extension",extension);*/

    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }

    public void downVideo(){
        Toast.makeText(this, "다운로드 시작되었습니다.",Toast.LENGTH_SHORT).show();


        //파일 이름 :날짜_시간
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);

        String extension = getExtension(videoViewUrl);
        //Log.d("extension",extension);
        String filename = String.valueOf(sdf.format(day))+"."+ extension;


        String localPath = "/KNU_AMP/video/" + filename;

        urlToDownload = Uri.parse(videoViewUrl);
        request = new DownloadManager.Request(videoUri);
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

    //다운로드 완료되었을 때 작동
    private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "download/KNU_AMP에 다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
            downPushed =0;
        }

    };

    //화면에 보이기 시작할때!!
    @Override
    protected void onStart() {
        super.onStart();

        loading = new ProgressDialog(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);  //로딩 중 화면 눌렀을 때 로딩바 취소되지 않음
        //loading.setCancelable(false);  //로딩 중 뒤로가기 버튼 눌렀을 때 로딩방 취소되지 않음
        loading.show();

        player = new ExoPlayer.Builder(this).build();
        //플레이어뷰에게 플레이어 설정
        binding.videoView.setPlayer(player);



        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        player.setMediaItem(mediaItem);
        player.prepare();
        player.addListener(new Player.Listener() {
           @Override
           public void onPlaybackStateChanged(int playbackState) {
               if(playbackState==Player.STATE_READY){
                   loading.dismiss();
                   player.play();
               }
           }
       });
                //player.play();
                //로딩이 완료되어 준비가 되었을 때
                //자동 실행되도록..
                //player.setPlayWhenReady(true);

        /*if(player.setPlayWhenReady()) {
            loading.dismiss();
            player.play();
        }*/

                //웹 주소 에러 관련 리스너
        /*player.addListener(new Player.Listener() {

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
            }
        });*/
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
