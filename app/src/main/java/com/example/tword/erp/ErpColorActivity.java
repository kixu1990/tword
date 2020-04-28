package com.example.tword.erp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tword.BaseActivity;
import com.example.tword.R;

import java.util.ArrayList;

import materialdesignutil.StatusBarUtil;
import message.MyMessage;

public class ErpColorActivity extends BaseActivity {
    private ArrayList<ErpColorData> datas; //数据源
    private String department;             //部门
    private String lotNumber;              //批号
    private TextView titleTv;              //标题
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erp_color_activity);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

        Intent intent  = getIntent();
        MyMessage message = (MyMessage) intent.getSerializableExtra("datas");
        department = (String) message.getObjects()[0];
        lotNumber = (String) message.getObjects()[1];
        datas = (ArrayList<ErpColorData>) message.getObjects()[2];

        titleTv = findViewById(R.id.erp_color_tv);
        recyclerView = findViewById(R.id.erp_color_rv);

        titleTv.setText(department + " - "+lotNumber);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ErpColorAdapter adapter = new ErpColorAdapter(this,datas);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 请用此方法启动本Activity
     * @param context
     * @param message
     */
    public static void actionStart(Context context, MyMessage message){
        Intent intent = new Intent(context, ErpColorActivity.class);
        intent.putExtra("datas", message);
        context.startActivity(intent);
    }
}
