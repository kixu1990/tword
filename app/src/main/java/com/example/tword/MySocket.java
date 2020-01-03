package com.example.tword;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import message.MyMessage;

/**
 * Created by Administrator on 2019/9/12.
 */

public class MySocket {
    public static void sendMessage(final MyMessage message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket;
                OutputStream ops;
                ObjectOutputStream oos;
                try{
                    socket = new java.net.Socket(ServerInfo.SERVER_IP,ServerInfo.PORT);
                    ops = socket.getOutputStream();
                    oos = new ObjectOutputStream(ops);
                    oos.writeObject(message);
                    ops.close();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
