package com.example.tword;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.tword.service.NetwordCheckService;

import org.litepal.crud.DataSupport;

import litepal.MainMessageDB;
import litepal.MessageContentDB;
import litepal.MsgMemberDB;
import litepal.SatffDB;
import litepal.departmentDB;
import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import mutils.DoubleClickUtil;
import nio.NioSocketChannel;

public class LoginActivity extends BaseActivity {
    private SharedPreferences spfs;
    private SharedPreferences.Editor editor;
    private boolean loginOk = false;
    private EditText loginName,password;
    private TextView loginButton;
    private GetLoginReceiver loginReceiver;
    private CardView loginCardView;
    private ImageView loginLandian,loginLandian2,loginLandain3;

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
        loginCardView = (CardView)findViewById(R.id.login_cardview);
        loginLandian = (ImageView)findViewById(R.id.login_landian_iv);
        loginLandian2 = (ImageView)findViewById(R.id.login_landian_iv2);
        loginLandain3 = (ImageView)findViewById(R.id.login_landian_iv3);
        loginLandian.setVisibility(View.GONE);
        loginLandian2.setVisibility(View.GONE);
        loginLandain3.setVisibility(View.GONE);
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
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
           if(loginName.getText().toString().equals("deleteDB")) {
               DataSupport.deleteAll(MainMessageDB.class);
               DataSupport.deleteAll(MessageContentDB.class);
               DataSupport.deleteAll(SatffDB.class);
               DataSupport.deleteAll(departmentDB.class);
               DataSupport.deleteAll(MsgMemberDB.class);
           }else{
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       if (NioSocketChannel.getInstance().login(loginName.getText().toString(), password.getText().toString())) {
                           stopGetMessageService();
                           startGetMessageService();
                       }else {
                           //子线程更新UI的方法 使用Hander
                           Message msg = new Message();
                           msg.what = 0;
                           msg.obj = false;
                           mHander.sendMessage(msg);
                       }
                   }
               }).start();
           }
           loginCardView.setVisibility(View.GONE);
           loginLandian.setVisibility(View.VISIBLE);
           loginLandian2.setVisibility(View.VISIBLE);
           loginLandain3.setVisibility(View.VISIBLE);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels;
                ObjectAnimator animator = ObjectAnimator.ofFloat(loginLandian,"translationX",-width,0,0,width-400);
                animator.setDuration(2000);
                animator.setRepeatCount(50);
                animator.start();
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(loginLandian2,"translationX",-width+200,0,0,width-200);
                animator2.setDuration(2000);
                animator2.setRepeatCount(50);
                animator2.start();
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(loginLandain3,"translationX",-width+400,0,0,width);
                animator3.setDuration(2000);
                animator3.setRepeatCount(50);
                animator3.start();
            }
        });
    }

    private void startNetwordCheckService(){
        Intent startIntent = new Intent(this, NetwordCheckService.class);
        startService(startIntent);
    }

    private void stopGetMessageService(){
        Intent stopIntent = new Intent(this,GetMessageService.class);
        stopService(stopIntent);
    }

    private void startGetMessageService(){
        Intent startIntent = new Intent(this,GetMessageService.class);
        startService(startIntent);
    }

    private void startHeartbeatService(){
        Intent intent = new Intent(this,HeartbeatService.class);
        startService(intent);
    }

    private void toast(){
        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
    }

    private void finishActivity(){
        this.finish();
    }

    /**
     * 登录失败调用方法
     */
    private void loginFails(){
        loginCardView.setVisibility(View.VISIBLE);
        loginLandian.setVisibility(View.GONE);
        loginLandian2.setVisibility(View.GONE);
        loginLandain3.setVisibility(View.GONE);
        toast();
    }

    /**
     * 子线程更新UI
     */
    Handler mHander = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0: loginFails();
                        break;
                default:
                        break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginReceiver);
    }

    class GetLoginReceiver extends BroadcastReceiver{

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
                  User.getINSTANCE().setLoginName(loginName.getText().toString());
                  User.getINSTANCE().setPasswrd(password.getText().toString());

                  startNetwordCheckService();

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
                  loginCardView.setVisibility(View.VISIBLE);
                  loginLandian.setVisibility(View.GONE);
                  loginLandian2.setVisibility(View.GONE);
                  loginLandain3.setVisibility(View.GONE);
                  toast();
             }
        }
    }
}
