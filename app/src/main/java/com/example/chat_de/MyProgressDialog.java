package com.example.chat_de;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class MyProgressDialog extends Dialog
{
    public MyProgressDialog(Context context)
    {
        super(context);
        // 다이얼 로그 제목을 안보이게...
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_element_loading);
    }
}