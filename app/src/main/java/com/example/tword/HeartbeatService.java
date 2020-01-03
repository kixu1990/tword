package com.example.tword;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.OutputStream;
import java.net.Socket;

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
                while(true){
                    heartbeat();
                    try {
                        Thread.sleep(60000);
                    }catch (Exception e){

                    }
                }

            }
        }).start();
    }
    private void heartbeat(){
        try{
            Socket socket = new Socket(ServerInfo.SERVER_IP,ServerInfo.HEARTPORT);
            OutputStream os = socket.getOutputStream();
//            os.write((new User().getUserName()+"\n<OO><oo><00><oo><OO>").getBytes());
            os.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
