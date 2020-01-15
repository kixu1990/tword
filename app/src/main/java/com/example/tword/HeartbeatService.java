package com.example.tword;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import message.MyMessage;
import nio.NioSocketChannel;

public class HeartbeatService extends Service {
    public HeartbeatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
 //       startForeground(-1315,new Notification());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("HeartbeatService","心跳启动！");
                while(true) {
                    sendHeartBeat();
                }
            }
        }).start();
    }

    /**
     * 暂时设为3分钟发一次心跳，后期重新优化
     */
    private void sendHeartBeat(){
        MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"heartBeat");
        try {
            NioSocketChannel.getInstance().sendMessage(message);
        } catch (IOException e) {
            NioSocketChannel.getInstance().login(User.getINSTANCE().getLoginName(),User.getINSTANCE().getPasswrd());
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
