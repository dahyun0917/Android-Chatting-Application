package com.example.chat_de;

import androidx.multidex.MultiDexApplication;

import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

public class ChatApplication extends MultiDexApplication {
    public static SimpleCache simpleCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024);
        simpleCache = simpleCache == null ? new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, new StandaloneDatabaseProvider(this)) : simpleCache;

        String[] chat_userData_arr = new String[]{
                "10006",
                "테스트안드",
                "http://192.168.0.22:9057/images/no-img01.png",
                "2",
                "001"};

        ChatDB.initChatDB(chat_userData_arr);
    }
}
