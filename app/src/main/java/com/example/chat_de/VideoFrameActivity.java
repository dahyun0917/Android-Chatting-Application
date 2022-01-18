package com.example.chat_de;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SimpleExpandableListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityImageFrameBinding;
import com.example.chat_de.databinding.ActivityUserListBinding;
import com.example.chat_de.databinding.ActivityVideoFrameBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoFrameActivity extends AppCompatActivity {

    private ActivityVideoFrameBinding binding;
    private String fromName;
    private String passDate;
    private String videoViewUrl;
    private int touchnum=0;

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
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("");

        Intent getintent = getIntent();
        fromName = getintent.getStringExtra("fromName");
        passDate = getintent.getStringExtra("passDate");
        videoViewUrl = getintent.getStringExtra("imageView");
        videoUri=Uri.parse(videoViewUrl);


        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);

        //테스트해보기
        binding.videoView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(touchnum==0) {
                    touchnum=1;
                    binding.fromName.setVisibility(View.GONE);
                    binding.passDate.setVisibility(View.GONE);
                } else {
                    touchnum=0;
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
        getMenuInflater().inflate(R.menu.menu_down_video, menu);
        return true;
    }
    //유저 추가 메뉴바 설정
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch(curId){
            case R.id.down_video:
                downVideo();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downVideo(){

    }
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
