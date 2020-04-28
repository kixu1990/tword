package com.example.tword.erp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tword.BaseActivity;
import com.example.tword.R;
import com.example.tword.User;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import mutils.DoubleClickUtil;
import nio.NioSocketChannel;

/**
 * ERP活动主类
 */
public class ErpReportActivity extends BaseActivity {
    private RecyclerView recyclerView;     //显示recyclerView
    private List<ErpReportData> datas;     //recyclerView需要的数据
    private Context mContext;
    private EditText searchEt;             //搜索框
    private TextView searchTv;             //搜索按键
    private LinearLayoutManager manager;              //recyclerView的布局
    private ErpReportOutAdapter adapter;              //recyclerView的支持器
    private GetErpReportReceiver getErpReportReceiver;//广播接收器
    private LinearLayout linearLayout;                //首行布局
    private TextView errTv;                           //没有搜索到记录提示行

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erp_report);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.erp_color_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.erp_report_out_rv);
        searchEt = findViewById(R.id.erp_report_search_et);
        searchTv = findViewById(R.id.erp_report_search_tv);
        linearLayout = findViewById(R.id.erp_report_ll);
        errTv = findViewById(R.id.erp_report_err_tv);
        this.mContext = getBaseContext();

        //初始化recyclerView数据，并赋值----------------------------------------------------
        datas = new ArrayList<>();
        Intent intent = getIntent();
        MyMessage message  = (MyMessage) intent.getSerializableExtra("message");
        datas = unbindErpData(message);

        if(message.getStringContent().equals("true")){
            linearLayout.setVisibility(View.VISIBLE);
            errTv.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.GONE);
            errTv.setVisibility(View.VISIBLE);
        }

        manager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(manager);
        adapter = new ErpReportOutAdapter(this,datas);
        recyclerView.setAdapter(adapter);
        //------------------------------------------------------------------------------------

        searchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                String searchLot = searchEt.getText().toString().trim();
//                Log.d("搜索文本框的内容：",searchLot);
                if(searchLot.equals("") && searchLot == null){

                }else {
                    MyMessage message = new MyMessage(User.getINSTANCE().getUserId(), new int[]{0}, "getErpReport");
                    message.setStringContent(searchLot);
                    try {
                        NioSocketChannel.getInstance().sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //防止空输入
                if(s.toString().trim().length() > 0 ){
                    searchTv.setVisibility(View.VISIBLE);
                }else {
                    searchTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETERPREPORT_BROADCAST");
        getErpReportReceiver = new GetErpReportReceiver();
        registerReceiver(getErpReportReceiver,intentFilter);
    }

    /**
     * 分解成Adapter需要的数据
     * @param message
     * @return
     */
    private List<ErpReportData> unbindErpData(MyMessage message){
        List<ErpReportData> datas;
        //----------------------------------------------------------------------------------------------
        //   数据构造：
        //   map<工序，批号>
        //              |
        //             ArrayList<object[]{0-批号,1-发织数，2-颜色}>
        //                                          |
        //                                         map<颜色，尺码>
        //                                                   |
        //                                                 map<尺码,数量>
        //                                                           |
        //                                                          int[]{0-今日完成数，1-总完成数}
        //  部门-department 批号-lotNumber 总数-toltal 今日完成数-todayProduction 总完成数-production
        //  未完成数-NotProduction 进度-progress
        //-----------------------------------------------------------------------------------------------
        //解包
        HashMap<String, ArrayList<Object[]>> lotNumberMap = (HashMap<String,ArrayList<Object[]>>)message.getObjects()[0];
        Set lotNumberKeySet = lotNumberMap.keySet();
        Iterator lotNumberIt = lotNumberKeySet.iterator();
        datas = new ArrayList<>();

        while(lotNumberIt.hasNext()){                                     //解包部门
            String department = (String)lotNumberIt.next();
            ArrayList<ErpReportItemData> erpReportItemDatas = new ArrayList<>();

            ArrayList<Object[]> lotNumbers = (ArrayList<Object[]>) lotNumberMap.get(department);
            for(Object[] objects : lotNumbers){
                String lotNumber = (String) objects[0];                    //批号
                int toltal = (int)objects[1];                              //总数
                HashMap<String,HashMap<String,int[]>> colourMap = (HashMap<String,HashMap<String,int[]>>) objects[2]; //颜色map
                Set colourKeySet = colourMap.keySet();
                Iterator colourIt = colourKeySet.iterator();
                int lotTodayProduction = 0;                                   //每批的今日完成数
                int lotProduction = 0;                                        //每批的总完成数
                ArrayList<ErpColorData> erpColorDatas = new ArrayList<>();
                while(colourIt.hasNext()){                                    //解包颜色
                    String colour = (String)colourIt.next();                  //颜色
                    HashMap<String,int[]> sizeMap = (HashMap<String,int[]>)colourMap.get(colour);//尺码map
                    Set sizeKeySet = sizeMap.keySet();
                    Iterator sizeIt = sizeKeySet.iterator();
                    int colourTodayProduction = 0;                            //每批每色的今日完成数
                    int colourProduction = 0;                                 //每批每色的总完成数
                    ArrayList<ErpSizeData> erpSizeDatas = new ArrayList<>();
                    while(sizeIt.hasNext()){                                 //解包尺码
                        String size = (String)sizeIt.next();
                        int[] counts = (int[])sizeMap.get(size);
                        int todayProduction = counts[0];                     //每批每色每码的今日完成数
                        colourTodayProduction += todayProduction;
                        int production = counts[1];                          //每批每色每码的总完成数
                        colourProduction += production;
                        ErpSizeData erpSizeData = new ErpSizeData(size,todayProduction,production);
                        erpSizeDatas.add(erpSizeData);
                    }
                    lotTodayProduction += colourTodayProduction;
                    lotProduction += colourProduction;
                    ErpColorData erpColorData = new ErpColorData(colour,colourTodayProduction,colourProduction,erpSizeDatas);
                    erpColorDatas.add(erpColorData);
                }

                //打包成下层recyclerView的数据
                int NotProduction = toltal - lotProduction;
                float d = (float) (lotProduction * 100 /  toltal);
                int progress = (int) d;
                ErpReportItemData erpReportItemData = new ErpReportItemData(department,lotNumber,toltal,lotTodayProduction,lotProduction,NotProduction,progress,erpColorDatas);
                //               Log.d("ErpReportActivity",erpReportItemData.getLotNumber()+erpReportItemData.getToltal()+erpReportItemData.getTodayProduction()+"");
                erpReportItemDatas.add(erpReportItemData);

            }
            ErpReportData erpReportData = new ErpReportData(department,erpReportItemDatas);
            datas.add(erpReportData);
        }
        return  datas;
    }

    /**
     * 此方法未启用，暂保留
     * 请用此方法启动本Activity
     * @param context
     * @param message
     */
    public static void actionStart(Context context, MyMessage message){
        Intent intent = new Intent(context, ErpReportActivity.class);
        intent.putExtra("datas", message);
        context.startActivity(intent);
    }

    /**
     * 关闭广播接收器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(getErpReportReceiver);
    }

    class GetErpReportReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("message");
            if(message.getStringContent().equals("true")){
                linearLayout.setVisibility(View.VISIBLE);
                errTv.setVisibility(View.GONE);
            }else {
                linearLayout.setVisibility(View.GONE);
                errTv.setVisibility(View.VISIBLE);
            }
            datas = new ArrayList<>();
            datas = unbindErpData(message);
            adapter = new ErpReportOutAdapter(mContext,datas);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
