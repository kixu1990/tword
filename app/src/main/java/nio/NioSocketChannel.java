package nio;

import android.util.Log;

import com.example.tword.ServerInfo;
import com.example.tword.User;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;

import litepal.SatffDB;
import litepal.departmentDB;
import message.MyMessage;

/**
 * Created by kixu on 2019/11/30.
 */

public class NioSocketChannel{

    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10000);
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
        List<SatffDB> satffs = DataSupport.select("*")//.where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                          .find(SatffDB.class);
        HashMap<Integer,Integer> version = new HashMap<>();
        for(SatffDB satff : satffs){
            version.put(satff.getSatffId(),satff.getVersion());
 //           Log.d("发送的用户信息",satff.getSatffId()+" : "+satff.getVersion());
        }
        List<departmentDB> departments = DataSupport.select("*")//.where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                                    .find(departmentDB.class);
        HashMap<String,Integer> departmentVersion = new HashMap<>();
        for(departmentDB department: departments){
            departmentVersion.put(department.getDepartmentName(),department.getVersion());
//            Log.d("发送的部门信息",department.getDepartmentName()+" : "+department.getVersion());
        }

 //       Log.d("数据库读出的数量",satffs.size()+" : "+departments.size());
        MyMessage message = new MyMessage(0,new int[]{0},"login");
        message.setLoginName(loginName);
        message.setLoginPassword(password);
        message.setObjects(new Object[]{version,departmentVersion});
        byte[] messageBytes = ObjectFlidByte.objectToByteArray(message);
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
            this.socketChannel = socketChannel;
//            Log.d("NioSocketChannel",socketChannel.toString());
            byteBuffer.clear();
            byteBuffer.put(IntFlidByte.getHeadByte(messageBytes.length));
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
                byteBuffer.put(IntFlidByte.getHeadByte(messageBytes.length));
                byteBuffer.put(messageBytes);
                int i = messageBytes.length;
                byteBuffer.flip();
                try {
                    socketChannel.write(byteBuffer);
                    Log.d("发送消息成功","长度 "+String.valueOf(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteBuffer.clear();

            }
        }).start();
    }
}
