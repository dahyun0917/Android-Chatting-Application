package com.example.chat_de;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class NetworkConnectionManager {
    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public NetworkConnectionManager(Context context) {
        this.context = context;
        networkRequest =
                new NetworkRequest.Builder()                                        // addTransportType : 주어진 전송 요구 사항을 빌더에 추가
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)   // TRANSPORT_CELLULAR : 이 네트워크가 셀룰러 전송을 사용함을 나타냅니다.
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)       // TRANSPORT_WIFI : 이 네트워크가 Wi-Fi 전송을 사용함을 나타냅니다.
                        .build();
        connectivityManager = context.getSystemService(ConnectivityManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    Log.e("TAG", "The default network is now: " + network);
                }

                @Override
                public void onLost(Network network) {
                    Log.e("TAG", "The application no longer has a default network. The last default network was " + network);
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    Log.e("TAG", "The default network changed capabilities: " + networkCapabilities);
                }

                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                    Log.e("TAG", "The default network changed link properties: " + linkProperties);
                }
            });
        }
    }


}
