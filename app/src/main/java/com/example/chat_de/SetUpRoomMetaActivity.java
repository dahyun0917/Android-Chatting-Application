package com.example.chat_de;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ActivityCreateRoomMetaBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SetUpRoomMetaActivity extends AppCompatActivity {
    private ActivityCreateRoomMetaBinding binding;
    private String changedChatRoomPictureUrl;
    private String changedChatRoomName;
    private String originalChatRoomKey;
    private String originalChatRoomPictureUrl;
    private String originalChatRoomName;
    private android.app.ProgressDialog progressDialog;
    private boolean isNewRoom = true;
    Uri imageUri;
    private ActivityResultLauncher<Intent> galleryResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateRoomMetaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        createRoomMetaInitializer();
        setView();
    }
    private void createRoomMetaInitializer() {
        Intent getIntent = getIntent();

        if(!TextUtils.isEmpty(getIntent.getStringExtra("chatRoomKey"))) {//채팅방 정보 수정 모드
            isNewRoom = false;
            originalChatRoomKey = getIntent.getStringExtra("chatRoomKey");
            originalChatRoomName = getIntent.getStringExtra("chatRoomName");
            originalChatRoomPictureUrl = getIntent.getStringExtra("chatRoomPicture");
            changedChatRoomPictureUrl = originalChatRoomPictureUrl;
            changedChatRoomName = originalChatRoomName;
            binding.chatNameText.setText(originalChatRoomName);
            Glide
                    .with(SetUpRoomMetaActivity.this)
                    .load(originalChatRoomPictureUrl)
                    .placeholder(R.drawable.knu_mark_white)
                    .into(binding.chatImage);
        }
        else{//채팅방 만드는 모드
            isNewRoom = true;
            changedChatRoomPictureUrl = "";
            changedChatRoomName = "";
            originalChatRoomName = "";
            originalChatRoomPictureUrl = "";
            originalChatRoomKey = "";
            binding.descriptionText.setText("공백만 입력하시거나 아무것도 입력하시지 않으면 기본 이름으로 설정됩니다.");
        }

        galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                imageUri = result.getData().getData();
                //binding.chatImage.setImageURI(imageUri);
                Glide
                        .with(SetUpRoomMetaActivity.this)
                        .load(imageUri)
                        .into(binding.chatImage);
                changedChatRoomPictureUrl = imageUri.toString();
            }
        });
    }
    private void setView() {
        binding.originalImage.setOnClickListener(view13 -> {
            changedChatRoomPictureUrl = "";
            binding.chatImage.setImageResource(R.drawable.knu_mark_white);
        });

        binding.selectImage.setOnClickListener(view1 -> {
            Intent sendIntent = new Intent();
            sendIntent = Intent.createChooser(FileDB.openImage(), "이미지를 선택하세요.");
            galleryResultLauncher.launch(sendIntent);
        });

        binding.createComplete.setOnClickListener(view12 -> { //완료 버튼 클릭했을 때
            changedChatRoomName = binding.chatNameText.getText().toString();
            if(changedChatRoomName.trim().length()<=0 && !isNewRoom) {
                Toast.makeText(SetUpRoomMetaActivity.this,"공백만 입력하실 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
            else {
                if (!changedChatRoomPictureUrl.equals(originalChatRoomPictureUrl) && !changedChatRoomPictureUrl.equals("") && !changedChatRoomPictureUrl.isEmpty()) {
                    //새로운 이미지로 바뀌었을 경우, 파이어스토어 및 파이어베이스에 업로드 ( 파이어 스토리지 업로드 필요한 경우 )
                    //TODO : 기존 이미지는 지우기
                    uploadChatRoomPicture();
                } else if (!changedChatRoomName.equals(originalChatRoomName) || ((changedChatRoomPictureUrl.equals("") || !changedChatRoomPictureUrl.isEmpty()) && !changedChatRoomPictureUrl.equals(originalChatRoomPictureUrl))) {
                    //채팅방 이름만 바뀌었을 경우, 또는 기본 이미지로만 바뀌었을 경우 ( 파이어 스토리지 업로드가 필요 없는 경우 )
                    if (isNewRoom)
                        newRoomUpload();
                    else
                        changeRoomUpload();
                    finish();
                } else {
                    //아무것도 안바뀌었을 경우
                    if (isNewRoom)
                        newRoomUpload();
                    finish();
                }
            }
        });
    }

    private void newRoomUpload() {
        //인텐트 후, UserListActivity 파이어베이스로 정보 업로드
        Intent finishIntent = new Intent(SetUpRoomMetaActivity.this, UserListActivity.class);
        finishIntent.putExtra("chatRoomName", changedChatRoomName);
        finishIntent.putExtra("chatRoomPicture", changedChatRoomPictureUrl);
        setResult(9001, finishIntent);
    }
    private void changeRoomUpload() {
        //파이어베이스에 정보 업로드
        ChatDB.changeChatRoomMeta(originalChatRoomKey,changedChatRoomName,changedChatRoomPictureUrl);
    }

    private void uploadChatRoomPicture() {
        progressDialog = new ProgressDialog(SetUpRoomMetaActivity.this);
        progressDialog.setTitle("업로드중...");
        progressDialog.show();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");

        String fileName = originalChatRoomName+" -> "+changedChatRoomName+"_"+sdf.format(new Date())+"."+FileDB.getFileType(this,imageUri);

        FileDB.uploadFileToFireStorage("ChatRoomPicture",fileName,imageUri, new IUploadFileEventListener() { //파이어스토리지 업로드
            @Override
            public void SuccessUpload(Uri uri) { //업로드 완료
                changedChatRoomPictureUrl = uri.toString();
                if (isNewRoom) {
                    newRoomUpload();
                }
                else {
                    changeRoomUpload();
                }
                progressDialog.dismiss();
                finish();
            }
            @Override
            public void FailUpload(Exception e) { //업로드 실패
                progressDialog.dismiss();
                finish();
                Log.e("FSE","upload fail!");
                Toast.makeText(SetUpRoomMetaActivity.this,"이미지 업로드에 실패했습니다. 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void ProgressUpload(double progress) { //업로드 진행중
                progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
