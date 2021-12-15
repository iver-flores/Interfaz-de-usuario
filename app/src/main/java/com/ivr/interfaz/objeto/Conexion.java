package com.ivr.interfaz.objeto;

import static android.content.Context.WIFI_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;

import androidx.annotation.NonNull;

import com.ivr.interfaz.GlobalDatos;

import java.io.IOException;

public class Conexion{

    public boolean verEstadoWifi(Context context){
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(WIFI_SERVICE);
        int state = wifiManager.getWifiState();
        boolean conexion = false;
        switch (state){
            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_DISABLED:
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
            case WifiManager.WIFI_STATE_ENABLING:
            case WifiManager.WIFI_STATE_ENABLED:
                conexion = true;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
        return conexion;
    }


    public void habilitarWifi(Context context){
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public boolean executeCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            return mExitValue == 0;
        }
        catch (InterruptedException | IOException ignore) {
            ignore.printStackTrace();
        }
        return false;
    }

}
