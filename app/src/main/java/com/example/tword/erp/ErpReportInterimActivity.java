package com.example.tword.erp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tword.BaseActivity;
import com.example.tword.R;
import com.example.tword.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import nio.NioSocketChannel;

/**
 * kixu 2020-03-17
 * ERP报表中间过渡活动类
 */
public class ErpReportInterimActivity extends BaseActivity {
    private GetErpReportReceiver getErpReportReceiver;
    private TextView login1,login2,login3,erroTv;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erp_report_interim_activity);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        login1 = findViewById(R.id.erp_report_interim_1_tv);
        login2 = findViewById(R.id.erp_report_interim_2_tv);
        login3 = findViewById(R.id.erp_report_interim_5_tv);
        erroTv = findViewById(R.id.erp_report_interim_tv);

        getDatas();

        Thread loading = new Thread(new Runnable() {
            @Override
            public void run() {
                loading();
            }
        });

        loading.start();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETERPREPORT_BROADCAST");
        getErpReportReceiver = new GetErpReportReceiver();
        registerReceiver(getErpReportReceiver,intentFilter);
    }

    private void loading(){
        int grray = Color.argb(100,200,200,200);
        int balck = Color.argb(100,50,50,50);
        while(flag){
            try {
                login1.setBackgroundColor(balck);
                Thread.sleep(300);
                login2.setBackgroundColor(balck);
                login1.setBackgroundColor(grray);
                Thread.sleep(300);
                login3.setBackgroundColor(balck);
                login2.setBackgroundColor(grray);
                Thread.sleep(300);
                login3.setBackgroundColor(grray);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(getErpReportReceiver);
    }
    private void finishActivity(){
        this.finish();
    }

    /**
     * 给服务器发送请求 索取ERP数据
     */
    private void getDatas(){
        MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"getErpReport");
        try {
            NioSocketChannel.getInstance().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"服务器连接失败",Toast.LENGTH_SHORT).show();
            finishActivity();
        }
    }

    class GetErpReportReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("message");
            flag = false;
 //           if(message.getStringContent().equals("true")) {
                Intent it = new Intent(ErpReportInterimActivity.this, ErpReportActivity.class);
                it.putExtra("message", message);
                startActivity(it);
                finishActivity();
//            }else {
//                login1.setVisibility(View.GONE);
//                login2.setVisibility(View.GONE);
//                login3.setVisibility(View.GONE);
//                erroTv.setVisibility(View.VISIBLE);
//            }

        }
    }
}
