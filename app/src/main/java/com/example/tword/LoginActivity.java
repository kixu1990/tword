package com.example.tword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import litepal.MainMessageDB;
import litepal.MessageContentDB;
import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import nio.NioSocketChannel;

public class LoginActivity extends BaseActivity {
    private SharedPreferences spfs;
    private SharedPreferences.Editor editor;
    private boolean loginOk = false;
    private EditText loginName,password;
    private TextView loginButton;
    private GetLoginReceiver loginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

        loginName = (EditText)findViewById(R.id.loginName_et);
        loginButton = (TextView)findViewById(R.id.login_tv);
        password = (EditText)findViewById(R.id.password_et);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.LOGIN_BROADCAST");
        loginReceiver = new GetLoginReceiver();
        registerReceiver(loginReceiver,intentFilter);

//        Intent startIntent = new Intent(this,GetMessageService.class);
//        startService(startIntent);
//
//        Intent satrtHeartbeat = new Intent(this,HeartbeatService.class);
//        startService(satrtHeartbeat);

        spfs = getSharedPreferences("login",MODE_PRIVATE);
        String spfloginName = spfs.getString("loginName","");
        String spfpassword = spfs.getString("password","");
        //      Log.d("loginactivity",String.valueOf(spfs.getBoolean("autoLogin",false))+spfloginName+spfpassword);
        if(spfs.getBoolean("autoLogin",false)){
 //           User.login(spfloginName,spfpassword);
//           NioSocketChannel.getInstance().login(spfloginName,spfpassword);
        }else {
            loginName.setText(spfloginName);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                  new NioSocketClient().start(loginName.getText().toString(),password.getText().toString());
 //               User.login(loginName.getText().toString(),password.getText().toString());
           if(loginName.getText().toString().equals("deleteDB")) {
               DataSupport.deleteAll(MainMessageDB.class);
               DataSupport.deleteAll(MessageContentDB.class);
           }else{
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       if (NioSocketChannel.getInstance().login(loginName.getText().toString(), password.getText().toString())) {
                           stopGetMessageService();
                           startGetMessageService();
                       }
                   }
               }).start();
           }
            }
        });
    }

    private void stopGetMessageService(){
        Intent stopIntent = new Intent(this,GetMessageService.class);
        stopService(stopIntent);
    }

    private void startGetMessageService(){
        Intent startIntent = new Intent(this,GetMessageService.class);
        startService(startIntent);
    }
    private void toast(){
        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
    }

    private void finishActivity(){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginReceiver);
    }

    class GetLoginReceiver extends BroadcastReceiver{

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
              MyMessage message = (MyMessage) intent.getSerializableExtra("login");
              if(message.getStringContent().equals("true")) {
                  editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                  editor.putInt("userId", message.getReceivers()[0]);
                  editor.putString("loginName", loginName.getText().toString());
                  editor.putString("password", password.getText().toString());
                  editor.putBoolean("autoLogin", true);
                  editor.apply();

                  User.getINSTANCE().setUserId(message.getReceivers()[0]);
                  User.getINSTANCE().setUserName(message.getUserName());
                  Log.d("用户名 ：" + User.getINSTANCE().getUserName(), "  ID ：" + User.getINSTANCE().getUserId());

                  Intent it = new Intent(LoginActivity.this, TWordMainActivity.class);
                  it.putExtra("message",message);
                  startActivity(it);
                  finishActivity();
             }else if(message.getStringContent().equals("false")){
//                  try {
//                      NioSocketChannel.getInstance().getSocketChannel().shutdownInput();
//                      NioSocketChannel.getInstance().getSocketChannel().shutdownOutput();
//                      NioSocketChannel.getInstance().getSocketChannel().close();
//                  } catch (IOException e) {
//                      e.printStackTrace();
//                  }
                  toast();
             }
        }
    }
}
