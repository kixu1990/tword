package com.example.tword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import litepal.MainMessageDB;
import litepal.MessageContentDB;
import litepal.MsgMemberDB;
import litepal.SatffDB;
import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import mutils.GetTopActivity;
import nio.NioSocketChannel;
import nio.NioSocketClient;

/**
 * kixu 2020-02-24
 * 对话显示主体类
 */
public class MainActivity extends BaseActivity{

    private List<Msg> msgList = new ArrayList<>();  //对话内容
    private EditText inputText;                     //输入文本框
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;                    //支技器
    private ImageView contentAdd;                  //加号按钮
    private CardView contentSend;                  //发送按钮
    private long messageId;                        //本对话的ID
    private String messageHeard;
    private TextView toolbarTV;                    //状态栏
    private FlexboxLayout heardFBL;                //群组成员
    private CircleImageView leftCIV,rightCIV;

    private IntentFilter intentFilter;
    private GetMessageReceiver getMessageReceiver; //对话接收器
    private List<MainMessageDB>  mainMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //侵入式状态栏-------------------------------------------------------------------------------------------
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this,Color.argb(255,235,235,235));
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);
        //--------------------------------------------------------------------------------------------------------

        inputText = (EditText)findViewById(R.id.input_text);
        contentAdd = (ImageView)findViewById(R.id.content_add_iv);
        contentSend = (CardView)findViewById(R.id.content_send);
        heardFBL = (FlexboxLayout)findViewById(R.id.content_FBL);
        leftCIV = (CircleImageView)findViewById(R.id.left_image_CIV);
        rightCIV = (CircleImageView)findViewById(R.id.right_image_CIV);
        toolbarTV = (TextView)findViewById(R.id.toolbar_tv);
        msgRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        final User user = User.getINSTANCE();

        GetTopActivity.getINSTANCE().setTopActivity(getClass().getName());    //标识本Activity为正在活动页面

        Intent intent = getIntent();                                          //得到传入的Intent
        messageId = intent.getLongExtra("messageId",0);  //得到需要打开的消息ID
        mainMessages = DataSupport.select("*")                    //从数据库中提取对话内容
                .where("messageId = ? and userId = ?",String.valueOf(messageId),String.valueOf(User.getINSTANCE().userId))
                .find(MainMessageDB.class);
        String title = intent.getStringExtra("messageHeard");
        toolbarTV.setText(title);                                             //显示消息标题

        //防止活动被回收后，再次重建时，数据丢失
        if(savedInstanceState != null){                                          //检查是否有保存的数据
            msgList = savedInstanceState.getParcelableArrayList("msgList"); //有的话，提取出来
        }else {
            msgInit();                                                            //没有的话，初始化数据
        }
        heardImageInit();                                                        //初始化成员头像
        //设置RedyclerView---------------------------------------------------------------------------------
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);                   //填充数据
        msgRecyclerView.setAdapter(adapter);                 //设置支持器
        msgRecyclerView.scrollToPosition(msgList.size() - 1);//显示最后一行
        //--------------------------------------------------------------------------------------------------
        //文本框事件监听
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0){
                    contentAdd.setVisibility(View.GONE);
                    contentSend.setVisibility(View.VISIBLE);
                }else {
                    contentAdd.setVisibility(View.VISIBLE);
                    contentSend.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

         //发送按扭事件监听
         contentSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //创建需要发送的消息
                 MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"messageContent");
                 message.setStringContent(inputText.getText().toString());
                 message.setMessageId(messageId);

                 inputText.setText("");                  //文本框清空
                 contentAdd.setVisibility(View.VISIBLE); //显示加号
                 contentSend.setVisibility(View.GONE);   //隐藏发送按扭
                 try {
                     NioSocketChannel.getInstance().sendMessage(message);  //将消息了送给服务器
                 }catch (IOException e){
                     e.printStackTrace();
                 }
             }
         });

    }

    @Override
    protected void onPostResume() {

        super.onPostResume();

        //起动消息广播接收器--------------------------------------------------------------------------------
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETCONTENT_BROADCAST");
        getMessageReceiver = new GetMessageReceiver();
        registerReceiver(getMessageReceiver,intentFilter);
        //--------------------------------------------------------------------------------------------------
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    //显示成员方法
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.content_show_member :
                if(heardFBL.getVisibility() == View.GONE){
                    heardFBL.setVisibility(View.VISIBLE);
                }else {
                    heardFBL.setVisibility(View.GONE);
                }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        GetTopActivity.getINSTANCE().setTopActivity(" ");
    }

    /**
     * 初始化成员头像方法
     */
    private void heardImageInit(){

        List<SatffDB> satffs = DataSupport.select("*")
                .where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                .find(SatffDB.class);

        List<MsgMemberDB> msgMembers = DataSupport.select("*")
                                                  .where("messageId = ?",String.valueOf(messageId))
                                                  .find(MsgMemberDB.class);
        for(MsgMemberDB members : msgMembers){
            CircleImageView imageView = new CircleImageView(this);
            byte[] src = null;
            for(SatffDB satff :satffs){
                if(members.getMembers() == satff.getSatffId()){
                    src = satff.getUserImage();
                }
            }
            RequestOptions options = new RequestOptions().override(65,65).centerCrop();
            Glide.with(this).load(src).apply(options).into(imageView);
            heardFBL.addView(imageView);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("msgList", (ArrayList<? extends Parcelable>) msgList); //防止活动被回收数据丢失，回收时存好数据
    }

    /**
     * 初始化对话内容
     */
    private void msgInit(){
        String userId = String.valueOf( User.getINSTANCE().getUserId());

        List<MessageContentDB> contents = DataSupport.select("*")
                                          .where("userId = ? and msgId = ?",userId,String.valueOf(messageId))
                                          .find(MessageContentDB.class);
        List<SatffDB> satffs = DataSupport.select("*")
//                                          .where("userId = ?",userId)
                                          .find(SatffDB.class);

        for(MessageContentDB content:contents){
            int type;
            if (content.getSender() == User.getINSTANCE().getUserId()) {
                type = Msg.TYPE_SENT;
            } else {
                type = Msg.TYPE_RECEIVED;
            }
            byte[] src = null;
            for(SatffDB satff :satffs){
                if(content.getSender() == satff.getSatffId()){
                    src = satff.getUserImage();
                }
            }
            Msg msg = new Msg(content.getStringContent(), type,src);
//           Log.d("getStringContent",String.valueOf(content.getStringContent()));
            msgList.add(msg);
        }

    }

    /**
     * 请用此方法启动本Activity
     * @param context
     * @param messageId    消息ID
     * @param messageHeard 消息标题
     */
    public static void actionStart(Context context,long messageId,String messageHeard){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("messageId",messageId);
        intent.putExtra("messageHeard",messageHeard);
        context.startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(getMessageReceiver);
    }

    /**
     * 接收消息的广播接收器
     */
    class GetMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("message");
            messageId = message.getMessageId();
            if(message.getMessageId() == messageId) {

                int type;
                if (message.getSender() == User.getINSTANCE().getUserId()) {
                    type = Msg.TYPE_SENT;
                } else {
                    type = Msg.TYPE_RECEIVED;
                }
                List<SatffDB> satffs = DataSupport.select("*")
//                                                  .where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                                  .find(SatffDB.class);
                byte[] src = null;
                for(SatffDB satff : satffs){
                    if (message.getSender() == satff.getSatffId()){
                        src = satff.getUserImage();
                    }
                }
                Msg msg = new Msg(message.getStringContent(), type,src);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.size() - 1);
                msgRecyclerView.scrollToPosition(msgList.size() - 1);

                abortBroadcast();
            }
        }
    }

}
