package com.example.chat_de;

public class ChatMode {
    private static int CHAT_MODE;
    public static void generalMode()    { CHAT_MODE = 0; }
    public static void groupMode()      { CHAT_MODE = 1; } //추후 기수 번호 담기
    public static int getChatMode() {
        return CHAT_MODE;
    }
}
