package com.example.tword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
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

public class MainActivity extends BaseActivity{

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private ImageView contentAdd;
    private CardView contentSend;
    private long messageId;
    private String messageHeard;
    private TextView toolbarTV;
    private FlexboxLayout heardFBL;
    private CircleImageView leftCIV,rightCIV;

    private IntentFilter intentFilter;
    private GetMessageReceiver getMessageReceiver;
    private List<MainMessageDB>  mainMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this,Color.argb(255,235,235,235));
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
//        getWindow().setNavigationBarColor(Color.argb(255,235,235,235));

 //       getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);

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
//        Msg msg = new Msg(">>>我是："+user.getUserName()+"<<<",Msg.TYPE_SENT);
//        msgList.add(msg);
//        Msg msg1 = new Msg("测试显示 ！！！",Msg.TYPE_RECEIVED);
//        msgList.add(msg1);

        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);

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

         contentSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"messageContent");
                 message.setStringContent(inputText.getText().toString());
                 inputText.setText("");
                 contentAdd.setVisibility(View.VISIBLE);
                 contentSend.setVisibility(View.GONE);
                 message.setMessageId(messageId);
                 try {
                     NioSocketChannel.getInstance().sendMessage(message);
                 }catch (IOException e){
                     e.printStackTrace();
                 }
             }
         });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

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


        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETCONTENT_BROADCAST");
        getMessageReceiver = new GetMessageReceiver();
        registerReceiver(getMessageReceiver,intentFilter);

 //       Log.d("MainActivity","onResume :"+getClass().getName());
        GetTopActivity.getINSTANCE().setTopActivity(getClass().getName());

        Intent intent = getIntent();
        messageId = intent.getLongExtra("messageId",0);
//        messageHeard = intent.getStringExtra("messageHeard");
//        Log.d("接收到广播的消息名",String.valueOf(messageId));
        mainMessages = DataSupport.select("*")
                .where("messageId = ? and userId = ?",String.valueOf(messageId),String.valueOf(User.getINSTANCE().userId))
                .find(MainMessageDB.class);
        String title = intent.getStringExtra("messageHeard");
//       Log.d("数据库提取的成员组",String.valueOf(mainMessages.get(0).getMembers())+mainMessages.get(0).getMessageId()+mainMessages.get(0).getHeadlin());
        toolbarTV.setText(title);
        msgInit();
        heardImageInit();
        adapter.notifyItemInserted(msgList.size() - 1);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
  //      Log.d("MainActivity","onPause :"+getClass().getName());
        GetTopActivity.getINSTANCE().setTopActivity(" ");
    }

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
//            Log.d("getStringContent",String.valueOf(content.getStringContent()));
            msgList.add(msg);
        }
    }

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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(getMessageReceiver);
//    }

    class GetMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("message");
            messageId = message.getMessageId();
 //           Log.d("触发广播的ID ",String.valueOf(messageId));
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
