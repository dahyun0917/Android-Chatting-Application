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
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_de.databinding.ActivityVideoFrameBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerControlView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoFrameActivity extends AppCompatActivity {

    private ActivityVideoFrameBinding binding;
    private String fromName;
    private String passDate;
    private String videoViewUrl;
    private boolean screenTouchNum =true; 
    private boolean downloadCancle = false;
    private boolean fail = true;

    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private Uri urlToDownload;
    private long latestId = -1;

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

        //컨트롤바 바뀔때마다 이름, 날짜, 툴바도 보이도록 지정
        binding.videoView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if(!screenTouchNum) {
                    screenTouchNum =true;
                    binding.fromName.setVisibility(View.GONE);
                    binding.passDate.setVisibility(View.GONE);
                    binding.toolbar.setVisibility(View.GONE);
                    //binding.videoView.setControllerAutoShow();
                    //binding.videoView.hideController();

                } else {
                    screenTouchNum =false;
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
                downVideo();

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
       /* String extension = getExtension(videoViewUrl);
        Log.d("extension",videoViewUrl);
        Log.d("extension",extension);*/

    }
    @Override
    public void onPostResume(){
        super.onPostResume();
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
    /*// 다운로드 상태조회
    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(latestId == reference){
                DownloadManager.Query query = new DownloadManager.Query();  // 다운로드 항목 조회에 필요한 정보 포함
                query.setFilterById(reference);
                Cursor cursor = downloadManager.query(query);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                int status = cursor.getInt(columnIndex);
                int reason = cursor.getInt(columnReason);

                cursor.close();

                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL :
                        binding.downloads.setVisibility(View.VISIBLE);
                        binding.downloadCancle.setVisibility(View.GONE);
                        Toast.makeText(context, "download/KNU_AMP에 다운로드를 완료하였습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_PAUSED :
                        Toast.makeText(context, "다운로드가 중단되었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_FAILED :
                        Toast.makeText(context, "다운로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };*/

    //파일 확장자 가져오기
    public static String getExtension(String fileStr){
        //String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
        //uri 스트링의 마지막 . 뒤부터 마지막 ? 까지의 스트링을 받아옴
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.lastIndexOf("?"));
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
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


    /*@Override
    protected void onPostResume() {
        super.onPostResume();

        // 브로드캐스트 리시버 등록
        // ACTION_DOWNLOAD_COMPLETE : 다운로드가 완료되었을 때 전달
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadCompleteReceiver, completeFilter);
    }*/

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(completeReceiver);
    }

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
                   fail=false;
                   loading.dismiss();
                   player.play();
               }
               /*if(playbackState==Player.STATE_ENDED){
                   screenTouchNum =0;
                   binding.fromName.setVisibility(View.VISIBLE);
                   binding.passDate.setVisibility(View.VISIBLE);
                   binding.toolbar.setVisibility(View.VISIBLE);
                   //binding.videoView.setUseController(true);
               }*/
               //비디오 로딩 시간 측정 (현재 15초)
               CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
                   public void onTick(long millisUntilFinished) {
                   }

                   public void onFinish() {
                       if(fail){
                       binding.downloads.setVisibility(View.GONE);
                       failLoading();
                       }
                   }
               }.start();
           }
            @Override
            public void onPlayerError(PlaybackException error) {
                Throwable cause = error.getCause();
                Log.d("error", String.valueOf(error.errorCode));
                //PlayerControlView.INVISIBLE;
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
    //로딩이 실패했을 경우 비디오, 로딩바 멈춤
    private void failLoading(){
        loading.dismiss();
        //binding.videoView.setPlayer(null);
        player.stop();
        player=null;
        Toast.makeText(VideoFrameActivity.this, "비디오 로딩 실패",Toast.LENGTH_SHORT).show();
    }

    //화면에 안보일 때..
    @Override
    protected void onStop() {
        super.onStop();
        //플레이어뷰 및 플레이어 객체 초기화
        if(!fail){
        binding.videoView.setPlayer(null);
        player.release();
        player=null;
        }
        fail=false;
        //Toast.makeText(getApplication(), "비디오 로딩 실패",Toast.LENGTH_SHORT).show();
    }





}
