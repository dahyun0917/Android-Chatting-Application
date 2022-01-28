package com.example.chat_de;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FileDB {
    public static final Intent intent = new Intent();
    private static StorageReference ref;
    private static String rootPath;

    public static void setReference(String root){
        ref = FirebaseStorage.getInstance().getReference(root);
        rootPath = root;
    }
    public static StorageReference getReference(){return ref;}
    public static String getRootPath(){
        return rootPath;
    }
    public static Intent openImage(){
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }
    public static Intent openVideo(){
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }
    public static Intent openFile(){
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    //파일 이름 가져오기
    public static String getFileName(String fileStr){
        return fileStr.substring(fileStr.lastIndexOf("_") + 1);
    }
    public static String getFileName(Uri uri,Context context) {
        /*파일명 찾기*/
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }
    //파일 확장자 가져오기
    public static String getFileType(String fileStr) {
        //String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
        //uri 스트링의 마지막 . 뒤부터 마지막 ? 까지의 스트링을 받아옴
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.lastIndexOf("?"));
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }
    public static String getFileType(Context context, Uri uri) {
        /*파일 확장자 가져오기*/
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    public static void uploadFileToFireStorage(String chatRoomName, String fileName, Uri filePath, IUploadFileEventListener listener){
        /*이미지 파일 업로드*/
        StorageReference uploadRef = ref.child(makePath(chatRoomName,fileName));
        UploadTask uploadTask = uploadRef.putFile(filePath);
        uploadTask.addOnSuccessListener(taskSnapshot -> uploadRef.getDownloadUrl().addOnSuccessListener(listener::SuccessUpload));
        uploadTask.addOnFailureListener(listener::FailUpload);
        uploadTask.addOnProgressListener(taskSnapshot -> listener.ProgressUpload((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount()));
    }

    public static long downloadFile(DownloadManager downloadManager, String fileUrl, String filename, String localPath){
        Uri urlToDownload = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(urlToDownload);
        request.setTitle(filename); //제목
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //알림창에 다운로드 중 , 다운로드 완료 창이 보이게 설정
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,localPath); //다운로드한 파일을 저장할 경로를 지정
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs(); //디렉토리가 존재하지 않을 경우 디렉토리를 생성하도록 구현
        //long latestId = downloadManager.enqueue(request); //latestID : 다운로드매니저 큐에 잘 들어갔는지 확인하는 변수로 사용하는 것으로 추정

        return downloadManager.enqueue(request);
    }
    @NonNull
    private static String makePath(@NonNull String... strings) {
        StringBuilder ret = new StringBuilder();
        for (String str : strings) {
            ret.append("/").append(str);
        }

        return ret.toString();
    }


}
