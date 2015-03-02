package com.qhe.pcontroldroid.models;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by sunshine on 15-3-1.
 *
 * Manage the connection of android phone between personal computer.
 */
public class ConnectionManager {
    private static int SERVER_PORT = 2016;
    private String mPhoneIP;
    private String mServerIP;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private DhcpInfo mDhcpInfo;
    private boolean mWifiOn;
    private Context mAppContext;
    private Socket mSocket;
    private boolean mConneted = false;

    private static ConnectionManager sConnectionManager;

    private ConnectionManager(Context appContext) {
        mAppContext = appContext;
        mWifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        mDhcpInfo = mWifiManager.getDhcpInfo();
        mPhoneIP = (mWifiInfo == null) ? "0" : intToIp(mWifiInfo.getIpAddress());
        mServerIP = (mDhcpInfo == null) ? "0" : intToIp(mDhcpInfo.serverAddress);
        mWifiOn = mWifiManager.isWifiEnabled();
    }

    public static ConnectionManager get(Context c) {
        if(sConnectionManager == null) {
            sConnectionManager = new ConnectionManager(c.getApplicationContext());
        }
        return sConnectionManager;
    }

    public boolean connect() {
        OutputStream os = null;
        try {
            Log.i("MainFragment", "开始连接");
            mSocket = new Socket(mServerIP, SERVER_PORT);
            os = mSocket.getOutputStream();
            os.write("1".getBytes());
            mConneted = true;
            Log.i("MainFragment", "Server IP: " + mServerIP  + mConneted + "连接成功");
        } catch (IOException e) {
            Log.i("MainFragment", "Server IP: " + mServerIP + "连接失败");
            mConneted = false;
            return false;
        } finally {
            try {
                if (os != null)
                    os.close();
                if(mSocket != null)
                    mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public String getPhoneIP() {
        return mPhoneIP;
    }

    public boolean ismWifiOn() {
        return mWifiManager.isWifiEnabled();
    }

    public String getServerIP() {
        return mServerIP;
    }

    public boolean isConneted() {
        return mConneted;
    }

    public void setConneted(boolean mConneted) {
        this.mConneted = mConneted;
    }

    // 将int型的IP转换为一般的值
    private String intToIp(int ipAddress)  {
        if (ipAddress != 0)
            return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        else
            return "";
    }

}
