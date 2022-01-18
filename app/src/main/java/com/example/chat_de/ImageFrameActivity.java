package com.example.chat_de;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.chat_de.databinding.ActivityImageFrameBinding;
import com.example.chat_de.databinding.ActivityUserListBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFrameActivity extends AppCompatActivity {

    private ActivityImageFrameBinding binding;
    private String fromName;
    private String passDate;
    private String imageViewUrl;
    private int touchnum=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageFrameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent getintent = getIntent();
        fromName = getintent.getStringExtra("fromName");
        passDate = getintent.getStringExtra("passDate");
        imageViewUrl = getintent.getStringExtra("imageView");

       /* SimpleDateFormat passDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String str= passDateFormat.format(passDate);  //TODO : 수정해야함*/

        Glide.with(this).load(imageViewUrl).thumbnail(Glide.with(this).load(R.drawable.loading)).into(binding.photoView);
        binding.fromName.setText(fromName);
        binding.passDate.setText(passDate);

        binding.photoView.setOnClickListener(new View.OnClickListener(){

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
                downImage();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downImage(){
        /*SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");
                String fileName = sdf.format(new Date());

                //서버로부터 받아온 bitmap 을 blackJin 이름의 jpg 로 변환해 캐시에 저장합니다.
                saveBitmapToJpeg(bitmap, fileName);
            }
        };*/
        //MakeCache();
        /*OutputStream outStream = null;
        String extStorageDirectory =
                Environment.getExternalStorageDirectory().toString();

        File file = new File(extStorageDirectory, "downimage.PNG");
        try {
            outStream = new FileOutputStream(file);
            mSaveBm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

            Toast.makeText(ImageSdcardSaveActivity.this,
                    "Saved", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ImageSdcardSaveActivity.this,
                    e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ImageSdcardSaveActivity.this,
                    e.toString(), Toast.LENGTH_LONG).show();
        }*/

    }
    public String  saveBitmapToJpeg(Bitmap bitmap , String name) {
        /**
         * 캐시 디렉토리에 비트맵을 이미지파일로 저장하는 코드입니다.
         *
         * @version target API 28 ★ API29이상은 테스트 하지않았습니다.★
         * @param Bitmap bitmap - 저장하고자 하는 이미지의 비트맵
         * @param String fileName - 저장하고자 하는 이미지의 비트맵
         *
         * File storage = 저장이 될 저장소 위치
         *
         * return = 저장된 이미지의 경로
         *
         * 비트맵에 사용될 스토리지와 이름을 지정하고 이미지파일을 생성합니다.
         * FileOutputStream으로 이미지파일에 비트맵을 추가해줍니다.
         */

        File storage = getCacheDir(); //내부저장소 캐시 경로를 받아옵니다.
        String fileName = name + ".jpg"; //저장할 파일 이름
        File imgFile = new File(storage, fileName);
        try {
            imgFile.createNewFile();  // 자동으로 빈 파일을 생성합니다.
            FileOutputStream out = new FileOutputStream(imgFile); // 파일을 쓸 수 있는 스트림을 준비합니다.
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            out.close();        // 스트림 사용후 닫아줍니다.
            Toast.makeText(this, "download success", Toast.LENGTH_SHORT).show();
            Log.d("Dahyun","Dahyun");
        } catch (FileNotFoundException e) {
            Log.e("saveBitmapToJpg","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapToJpg","IOException : " + e.getMessage());
        }
        Log.d("imgPath" , getCacheDir() + "/" +fileName);
        return getCacheDir() + "/" +fileName;
    }
    /*static public void MakeCache(View v,String filename){

        String StoragePath =
                Environment.getExternalStorageDirectory().getAbsolutePath();
        String savePath = StoragePath + "/적당한곳";
        File f = new File(savePath);
        if (!f.isDirectory())f.mkdirs();

        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        FileOutputStream fos;
        try{
            fos = new FileOutputStream(savePath+"/"+filename);
            bitmap.compress(Bitmap.CompressFormat cherche viagra a vendre.JPEG,80,fos);

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
