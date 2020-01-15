package com.example.tword;

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

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService","服务启动了！");
        String id ="1";

        String name ="channel_name_1";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification =null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel =new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);

            mChannel.setSound(null, null);

            notificationManager.createNotificationChannel(mChannel);

            notification =new Notification.Builder(this)

                    .setChannelId(id)

                    .setContentTitle(getResources().getString(R.string.app_name))

                    .setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消

                    .setOngoing(true)// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)

                    .setSmallIcon(R.mipmap.ic_launcher).build();

        }else {


        }

        startForeground(1, notification);
    }
}
