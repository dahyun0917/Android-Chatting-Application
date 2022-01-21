package com.example.chat_de;

public class ChatMode {
    private static int chatMode;
    //public static boolean adminMode;

    public static void setGeneralMode()                     { chatMode = 0; }
    public static void setGroupMode()                       { chatMode = 1; } //추후 chatMode에 기수 번호 담으면 좋을듯
    public static int getChatMode()                      { return chatMode; }
    public static void changeMode() {
        if(chatMode > 0)
            chatMode = 0;
        else
            chatMode = 1;
    }

    /*public static boolean isAdminMode()                  { return adminMode; }
    public static void setAdminMode(boolean adminMode)   { ChatMode.adminMode = adminMode; }*/
}
