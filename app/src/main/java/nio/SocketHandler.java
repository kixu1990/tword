package nio;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.tword.GetMessageService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import message.MyMessage;

/**
 * Created by kixu on 2019/12/2.
 */

public class SocketHandler {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyMessage handleRead(SelectionKey selectionKey) throws IOException {
        MyMessage message = null;
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                                byteBuffer.clear();
                                int count = socketChannel.read(byteBuffer);
                                if(count > 0){
                                     message = (MyMessage) ObjectFlidByte.byteArrayToObject(byteBuffer.array());
                                    Log.d("收到信息：",message.getHeader());
//                                    if(message.getHeader().equals("login") & message.getStringContent().equals("false")){
//                                        Log.d("登录失败 停止继续注册！","11111");
//                                    }else {
                                        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ, byteBuffer);
//                                    }
                                }else  if(count == -1){
                                    socketChannel.close();
                                }
        return message;
    }
}
