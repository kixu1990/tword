package com.example.tword.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.example.tword.User;

import java.io.IOException;
import java.sql.Connection;

import message.MyMessage;
import nio.NioSocketChannel;

/**
 * 在接收到网络变动或点亮屏幕的广播时，给服务器发送一条更新NIO通道的信息
 * 防止正常使用时网络变动或自动熄屏后台截断了网络
 * 2020-3-27 by kixu
 */
public class NetwordCheckService extends Service {

    private NetwordChangeReceiver networdChangeReceiver;
    private ScreenOnReceiver screenOnReceiver;

    public NetwordCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //开启网络变动的广播接收器----------------------------------------------------------------
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        networdChangeReceiver = new NetwordChangeReceiver();
        registerReceiver(networdChangeReceiver,intentFilter);
        //-------------------------------------------------------------------------------------------
        //开启点亮屏幕的广播接收器
        IntentFilter screenOnIntentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        screenOnReceiver = new ScreenOnReceiver();
        registerReceiver(screenOnReceiver,screenOnIntentFilter);
        //------------------------------------------------------------------------------------------
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networdChangeReceiver);
        unregisterReceiver(screenOnReceiver);
    }

    class NetwordChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //给服务器发送一条信息，如果不通就会在sendMessage()中重新登录
            MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"networdCheck");
            try {
                NioSocketChannel.getInstance().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    class ScreenOnReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //给服务器发送一条信息，如果不通就会在sendMessage()中重新登录
            MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"networdCheck");
            Log.d("测试：","收到屏幕点亮广播！！");
            try {
                NioSocketChannel.getInstance().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
