package nio;

import android.util.Log;

import com.example.tword.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import message.MyMessage;

/**
 * Created by kixu on 2019/11/30.
 */

public class NioSocketChannel{

    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
    private  SocketAddress socketAddress = new InetSocketAddress(ServerInfo.SERVER_IP,ServerInfo.PORT);
    private Selector selector = null;

    private static NioSocketChannel INSTANCE = new NioSocketChannel();

    private NioSocketChannel(){}

    public static NioSocketChannel getInstance(){
        return  INSTANCE;
    }

    public SocketChannel getSocketChannel(){
        return  socketChannel;
    }

    public ByteBuffer getByteBuffer(){
        return  byteBuffer;
    }

    public Selector openSelector(){
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return selector;
    }

    public Selector getSelector(){
        if (selector == null){
            openSelector();
        }
        return selector;
    }

    public Boolean login(String loginName, String password){
        Boolean b = false;
        MyMessage message = new MyMessage(0,new int[]{0},"login");
        message.setLoginName(loginName);
        message.setLoginPassword(password);
        byte[] messageBytes = ObjectFlidByte.objectToByteArray(message);
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
            this.socketChannel = socketChannel;
            Log.d("NioSocketChannel",socketChannel.toString());
            byteBuffer.clear();
            byteBuffer.put(messageBytes);
            byteBuffer.flip();

            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  b;
    }

    public void sendMessage(final  MyMessage message) throws IOException{
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] messageBytes = ObjectFlidByte.objectToByteArray(message);
                byteBuffer.clear();
                byteBuffer.put(messageBytes);

                byteBuffer.flip();
                try {
                    socketChannel.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteBuffer.clear();

            }
        }).start();
    }
}
