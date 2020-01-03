package nio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tword.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import message.MyMessage;

/**
 * 此类作废
 * Created by kixu on 2019/11/27.
 */

public class NioSocketClient extends Service {
    public void start(final String loginName, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyMessage message = new MyMessage(0,new int[]{0},"login");
                message.setLoginName(loginName);
                message.setLoginPassword(password);
                byte[] messageBytes = ObjectFlidByte.objectToByteArray(message);
                try(SocketChannel socketChannel = SocketChannel.open()){
                    SocketAddress socketAddress = new InetSocketAddress(ServerInfo.SERVER_IP,ServerInfo.PORT);
                    socketChannel.connect(socketAddress);

                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    buffer.clear();
                    buffer.put(messageBytes);
                    buffer.flip();
                    socketChannel.write(buffer);
                    buffer.clear();

                    int readLenth = socketChannel.read(buffer);
                    buffer.flip();
                    byte[] bytes = new byte[readLenth];
                    buffer.get(bytes);

                    MyMessage rm = (MyMessage) ObjectFlidByte.byteArrayToObject(bytes);
                    if(rm.getHeader().equals("login")){
                        Log.d("收到服务器回复： " ,rm.getHeader());
//                        buffer.clear();
//                        MyMessage mm  = new MyMessage(0,new int[]{0},"string");
//                        mm.setStringContent("你好！服务器！");
//                        byte[] rrm = ObjectFlidByte.objectToByteArray(mm);
//                        buffer.put(rrm);
//                        buffer.flip();
//                        socketChannel.write(buffer);
//                        buffer.clear();

                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendLoginBroadcast(MyMessage message){
        String msg = message.getStringContent();
        Intent intent = new Intent("com.example.tword.LOGIN_BROADCAST");
        intent.putExtra("login",true);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
