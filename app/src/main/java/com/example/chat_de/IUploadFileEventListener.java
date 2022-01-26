package com.example.chat_de;

import android.net.Uri;

public interface IUploadFileEventListener {
    void SuccessUpload(Uri uri);
    void FailUpload(Exception e);
    void ProgressUpload(double progress);
}
