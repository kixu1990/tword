package com.example.tword;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import litepal.MainMessageDB;
import litepal.MessageContentDB;
import litepal.MsgMemberDB;
import litepal.SatffDB;
import litepal.departmentDB;
import message.MyMessage;
import mutils.GetTopActivity;
import litepal.MainMessageDB;
import nio.IntFlidByte;
import nio.NioSocketChannel;
import nio.ObjectFlidByte;

public class GetMessageService extends Service {

    private static final int PORT = 11002;

    private ByteBuffer cacheBuffer = ByteBuffer.allocate(1024 * 10000);
    private boolean cache = false;
    int bodyLen = -1;
    int cacheBufferLen = -1;

    public GetMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String chennelId = "tword";
            String chennelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(chennelId,chennelName,importance);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("GetMessageService : ","得到通道");
 //                   Selector selector = NioSocketChannel.getInstance().getSelector();
                    SocketChannel socketChannel = NioSocketChannel.getInstance().getSocketChannel();
                    ByteBuffer buffer = NioSocketChannel.getInstance().getByteBuffer();
                    Log.d("GetMessageService",socketChannel.toString());

                    Selector selector = Selector.open();
//            SocketChannel socketChannel = SocketChannel.open();
//            SocketAddress socketAddress = new InetSocketAddress(ServerInfo.SERVER_IP,ServerInfo.PORT);
//            socketChannel.connect(socketAddress);
//            ByteBuffer buffer = ByteBuffer.allocate(1024);
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ,buffer);

                    nio.SocketHandler handler = new nio.SocketHandler();
                    while(true){
                        selector.select();

                        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                        while (keyIterator.hasNext()){
                            SelectionKey key = keyIterator.next();
                            if(key.isReadable()){
                                   handleRead(key);
                                Log.d("触发可读事件： ","0000");
                            }
                            keyIterator.remove();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
//
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                int i=0;
//                while (true) {
//                    Log.d("后台服务", "正在运行！！"+(i++));
//                    try {
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
    }

    public void handleRead(SelectionKey selectionKey) throws IOException {
        int head_length = 4;
        byte[] headByte = new byte[4];

        MyMessage message = null;
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
        byteBuffer.clear();

        if(cache){
            cacheBuffer.flip();
            byteBuffer.put(cacheBuffer);
        }
        int count = socketChannel.read(byteBuffer);
        if(count > 0) {
            byteBuffer.flip();
            Log.d("ByteBuffer原始数量：", "--------------------------------------------" + String.valueOf(byteBuffer.remaining()));
            int position = 0;
            int i = 0;
            while (byteBuffer.remaining() > 0) {
//           Log.d("第 ",String.valueOf(i++)+" 次");
                if (bodyLen == -1) { //没有读出包头，先读包头
                    Log.d("进入读包头！", "0000");
                    if (byteBuffer.remaining() >= head_length) { //可以读出包头，否则缓存
                        byteBuffer.mark();
                        byteBuffer.get(headByte);
                        bodyLen = IntFlidByte.getHeadInt(headByte);
                        Log.d("包头长度：", String.valueOf(bodyLen));
                    } else {
                        byteBuffer.reset();
                        cache = true;
                        cacheBuffer.clear();
                        cacheBuffer.put(byteBuffer);
                    }
                } else {
                    Log.d("进入读消息体！还有多少未读：", String.valueOf(byteBuffer.remaining()) + "包体长度： " + bodyLen);
                    if (byteBuffer.remaining() >= bodyLen) { //大于等于一个包，否则缓存
                        byte[] bodyByte = new byte[bodyLen];
                        byteBuffer.get(bodyByte, 0, bodyLen);
                        position += bodyLen;
                        byteBuffer.mark();
                        bodyLen = -1;
                        Log.d("读到一个消息", "Position = " + String.valueOf(position));
                        message = (MyMessage) ObjectFlidByte.byteArrayToObject(bodyByte);
                        Log.d("消息头：", message.getHeader());
                        unbindHeadr(message, null);
                        cache = false;
                    } else {
                        Log.d("进入缓存：", "----------------------------------------上一个包头：" + String.valueOf(bodyLen));
                        byteBuffer.mark();
                        byteBuffer.reset();
                        cacheBuffer.clear();
                        Log.d("ByteBuffer打入缓存数量：", String.valueOf(byteBuffer.remaining()));
                        cacheBufferLen = byteBuffer.remaining();
                        cacheBuffer.put(byteBuffer);
                        cache = true;
                        break;
                    }
                }
            }
            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ, byteBuffer);
        } else if(count == -1){
            socketChannel.close();
        }
    }

    private void unbindHeadr(MyMessage message,InetAddress inetAddress){
//        Log.d("kkkkk",message.getHeader());
//        if(message.getHeader().equals("clicklink")) {
//            replyLink();
//        }else if(message.getHeader().equals("login")){
//            sendLoginBroadcast(message);
//        }else if(message.getHeader().equals("createMessage")){
//            sendMainMessageBroadcast(message);
//            insertMainMessage(message);
//        }

        switch (message.getHeader()){
            case ("login"): sendLoginBroadcast(message);
                            resSatffList(message);
                break;
            case ("createMessage"): sendMainMessageBroadcast(message);
                                      insertMainMessage(message);
                break;
            case ("rsMessages"):unbindMessage(message);
                break;
            case ("messageContent"): sendContentBroadcast(message);
                                        insertMessageContent(message);
                                        sendNotification(message);
                break;
        }
    }

    private void resSatffList(MyMessage message){
        ArrayList<Object[]> departments = (ArrayList<Object[]>) message.getObjects()[0];
        ArrayList<Object[]> users = (ArrayList<Object[]>) message.getObjects()[1];

        Log.d("接收到的部门数量",String.valueOf(departments.size())+"   接收到的用户数量"+String.valueOf(users.size()));

        List<departmentDB> departmentDBS = DataSupport.select("*").where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                                                             .find(departmentDB.class);
        for(int i=0; i<departments.size();i++){
            for(departmentDB departmentDB : departmentDBS){
                if(departmentDB.getDepartmentName().equals((String)(departments.get(i)[0]))){
                    DataSupport.deleteAll(departmentDB.class,"departmentName = ? and userId = ?",departmentDB.getDepartmentName(),String.valueOf(User.getINSTANCE().getUserId()));
                }
            }
            departmentDB db = new departmentDB();
            db.setDepartmentName((String)departments.get(i)[0]);
            db.setDepartmentManager((String)departments.get(i)[1]);
            db.setVersion((int)departments.get(i)[2]);
            db.setUserId(User.getINSTANCE().getUserId());
            db.save();
//           Log.d("写入数据库","添加部门信息！"+db.getDepartmentName());
        }

        List<SatffDB> satffDBS = DataSupport.select("*").where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                                                   .find(SatffDB.class);
        for(int i=0; i<users.size(); i++){
            for(SatffDB satff : satffDBS){
                if(satff.getSatffId() == (int)users.get(i)[0]){
                    DataSupport.deleteAll(SatffDB.class,"satffId = ? and userId = ?",String.valueOf(satff.getSatffId()),String.valueOf(User.getINSTANCE().getUserId()));
                }
            }
            SatffDB satffDB = new SatffDB();
            satffDB.setSatffId((int)users.get(i)[0]);
            satffDB.setSatffName((String)users.get(i)[1]);
            satffDB.setDepartment((String)users.get(i)[2]);
            satffDB.setUserImage((byte[])users.get(i)[3]);
            satffDB.setVersion((int)users.get(i)[4]);
            satffDB.setPost((String)users.get(i)[5]);
            satffDB.setEmail((String)users.get(i)[6]);
            satffDB.setPhoneNumber((String)users.get(i)[7]);
            satffDB.setState((String)users.get(i)[8]);
            satffDB.setUserId(User.getINSTANCE().getUserId());
            satffDB.save();
//           Log.d("写入数据库","添加用户信息！"+satffDB.getSatffName());
        }
    }

    private void sendNotification(MyMessage message){
        String topActivityName = GetTopActivity.getINSTANCE().getTopActivity();
 //       Log.d("通知",topActivityName +": "+MainActivity.class.getName()+": "+TWordMainActivity.class.getName());
        if(topActivityName.equals(MainActivity.class.getName()) || topActivityName.equals(TWordMainActivity.class.getName())){
 //           Log.d("通知",topActivityName +": "+MainActivity.class.getName());

        }else {
            List<MainMessageDB>  mainMessages = DataSupport.select("*")
                    .where("messageId = ? and userId = ?",String.valueOf(message.getMessageId()),String.valueOf(User.getINSTANCE().userId))
                    .find(MainMessageDB.class);
            String title = mainMessages.get(0).getHeadlin();
            Intent intent = new Intent(this,TWordMainActivity.class);
//            intent.putExtra("messageId",message.getMessageId());
//            intent.putExtra("messageHeard",title);//
//                Log.d("发送的",String.valueOf(message.getMessageId())+" : "+title);
            PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
            notification(pi,title,message.getStringContent(),15,NotificationCompat.PRIORITY_MAX);
        }
    }

    private void notification (PendingIntent pi,String title,String contentText,int id,int priority){
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this,"tword")
                .setContentTitle(title)
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(priority)
                .build();
        manager.notify(id,notification);
 //       Log.d("通知","进入发送方法");
    }

    @TargetApi(26)
    private void createNotificationChannel(String channelId, String channelName, int priority){
        NotificationChannel channel = new NotificationChannel(channelId,channelName,priority);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void insertMainMessage(MyMessage message){
        MainMessageDB mm = new MainMessageDB();
        mm.setMessageId(message.getMessageId());
        mm.setHeadlin(message.getStringContent());
        mm.setUserId(User.getINSTANCE().getUserId());
        mm.setDate(new Date(message.getDate().getTime()));
        mm.save();
//        Log.d("添加数据库",mm.getHeadlin() +": "+User.getINSTANCE().getUserId());
        for(int i : message.getReceivers()){
            MsgMemberDB msgMember = new MsgMemberDB();
            msgMember.setMessageId(message.getMessageId());
            msgMember.setMembers(i);
            msgMember.save();
        }
    }
    private void insertMessageContent(MyMessage message){
        MessageContentDB mc = new MessageContentDB();
        mc.setMsgId(message.getMessageId());
        mc.setStringContent(message.getStringContent());
 //       Log.d("服务收到数据 内容为",mc.getStringContent());
        mc.setSender(message.getSender());
        mc.setSenderTime(message.getDate().getTime());
//        Log.d("服务收到数据 时间为",mc.getSenderTime().toString());
        mc.setUserId(User.getINSTANCE().getUserId());
        mc.save();

        List<MessageContentDB> mcs = DataSupport.select("*")
                                                .find(MessageContentDB.class);
//        for(MessageContentDB content:mcs){
//            Log.d("用户",String.valueOf(content.getUserId()));
//            Log.d("消息ID",String.valueOf(content.getMsgId()));
//            Log.d("内容",String.valueOf(content.getStringContent()));
//            Log.d("时间",String.valueOf(new Date(content.getSenderTime())));
//        }
    }

    private void unbindMessage(MyMessage message){
        MyMessage[] messages = (MyMessage[]) message.getObjects();
        for(int i=0; i<messages.length; i++){
            unbindHeadr(messages[i],null);
        }
    }

    private void  replyLogin(MyMessage message){
        Boolean b = false;
        if(message.getStringContent().equals("ok")){
            b = true;
        }
        Intent intent = new Intent("com.example.tword.LOGIN_BROADCAST");
        intent.putExtra("login",b);
        sendBroadcast(intent);
    }

    private void replyLink(){
//        try{
//            Socket socket = new Socket(ServerInfo.SERVER_IP,ServerInfo.PORT);
//            MyMessage message = new MyMessage(new User().getUserName(),new String[]{new User().getUserName()},"reply_link");
//            message.setStringContent("this is checklink");
//            OutputStream os = socket.getOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(os);
//            oos.writeObject(message);
//            oos.close();
//            os.close();
//            socket.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void sendMsgBroadcast(MyMessage message){
        String msg = message.getStringContent();
        Intent intent = new Intent("com.example.tword.GETMESSAGE_BROADCAST");
        intent.putExtra("message",message);
        sendBroadcast(intent);
    }

    private void sendContentBroadcast(MyMessage message){
//        List<MainMessageDB> mainMessages = DataSupport.select("*")
//                                                      .where("userId = ? and messageId = ?",String.valueOf(User.getINSTANCE().getUserId()),String.valueOf(message.getMessageId()))
//                                                      .find(MainMessageDB.class);
//        if(mainMessages.size() == 0){
//            MyMessage mainMessage = new MyMessage(0, new int[]{User.getINSTANCE().getUserId()}, "createMessage");
//        }
        Intent intent = new Intent("com.example.tword.GETCONTENT_BROADCAST");
        intent.putExtra("message",message);
        sendOrderedBroadcast(intent,null);
    }

    private void sendLoginBroadcast(MyMessage message){
        String msg = message.getStringContent();
        User.getINSTANCE().setUserId(message.getReceivers()[0]);
        User.getINSTANCE().setUserName(message.getUserName());
        Intent intent = new Intent("com.example.tword.LOGIN_BROADCAST");
        intent.putExtra("login",message);
        sendBroadcast(intent);
    }

    private void sendMainMessageBroadcast(MyMessage message){
        Intent intent = new Intent("com.example.tword.GETMAINMESSAGE_BROADCAST");
        intent.putExtra("createMessage",message);
        sendBroadcast(intent);
        Log.d("发送MainMessage广播",message.getHeader());
    }

//    private void unbindHeadr(String message,InetAddress inetAddress){
//        String[] nubind = message.split("\n");
//        String userName = nubind[1].trim();
//        boolean b = userName.equals("lidajiao");
//        Log.d("判断", userName+"<>"+"lidajiao   "+Boolean.toString(b));
//        if(nubind[0].equals("clicklink")){
//            replyLink(userName);
//        }else {
//            sendMsgBroadcast(message);
//        }
//    }

//    private void replyLink(String s){
//        try {
//            Socket socket = new Socket(ServerInfo.SERVER_IP, ServerInfo.PORT);
//            String messageFull = s+"\n"+new User().getUserName()+"\n"+"reply_link"+"\n"+"this is checklink";
//            byte[] b = messageFull.getBytes("utf-8");
//            OutputStream ops = socket.getOutputStream();
//            ops.write(b);
//            ops.close();
//            socket.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    private void sendMsgBroadcast(String message){
        Intent intent = new Intent("com.example.tword.GETMESSAGE_BROADCAST");
        intent.putExtra("message",message);
        sendBroadcast(intent);
    }


}
