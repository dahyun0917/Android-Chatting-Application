package com.example.chat_de.datas;

// RecyclerView ViewHolder 때문에 enum으로 못 바꿈
public class ViewType {
    public static final int LOADING = -1;
    public static final int LEFT_CONTENT_TEXT = 0;
    public static final int LEFT_CONTENT_IMAGE = 1;
    public static final int RIGHT_CONTENT_TEXT = 2;
    public static final int RIGHT_CONTENT_IMAGE = 3;
    public static final int CENTER_CONTENT = 4;
}
