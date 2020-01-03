package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tword.R;
import com.example.tword.User;

import org.litepal.crud.DataSupport;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import fmtadapter.MainMessage;
import fmtadapter.MainMessageAdapter;
import litepal.MainMessageDB;
import litepal.MessageContentDB;
import message.MyMessage;

/**
 * Created by kixu on 2019/10/24.
 */

public class MainMessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainMessageAdapter adapter;
    private List<MainMessage> mainMessages = new ArrayList<>();
    private IntentFilter intentFilter, contentFilter;
    private GetMainMessageReceiver getMainMessageReceiver;
    private GetContentReceiver getContentReceiver;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_messsage_fragment,container,false);
        recyclerView = view.findViewById(R.id.main_message_fmt_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

        context = view.getContext();

        recyclerView.setLayoutManager(layoutManager);
        mainMessages.clear();
        String userId = String.valueOf( User.getINSTANCE().getUserId());
        List<MainMessageDB> mainMessageDBs = DataSupport.select("*")
                                                        .where("userId = ?",userId)
                                                        .find(MainMessageDB.class);
//        List<MainMessageDB> mainMessageDBs = DataSupport.findAll(MainMessageDB.class);
//        Log.d("启动","MainMessageFragment");

        for(MainMessageDB mm:mainMessageDBs){
            MainMessage mainMessage = new MainMessage(mm.getHeadlin());
            mainMessage.setMessageId(mm.getMessageId());
            String msgId = String.valueOf(mm.getMessageId());
            List<MessageContentDB> contents = DataSupport.select("*")
                                                         .where("userId = ? and msgId =?",userId,msgId)
                                                         .find(MessageContentDB.class);
            String lastContent = "";
            java.util.Date lastTime = null;
            if(contents.size() > 0){
                lastContent = contents.get(contents.size() -1).getStringContent();
                lastTime = new java.util.Date(contents.get(contents.size() - 1).getSenderTime());
//                if(lastTime == null){
//                    Log.d("lastTime","null");
//                }else {
//                    Log.d("lastTime","11111111");
//                }
//                Log.d("000000",contents.get(contents.size() - 1).getSenderTime().toString());
            }else {
                lastTime = mm.getDate();
            }

            mainMessage.setLastContent(lastContent);
            mainMessage.setLastTime(lastTime);
//            Log.d("DB里的mainMessage",mm.getHeadlin()+": "+mm.getUserId());
            mainMessages.add(mainMessage);
        }
        adapter = new MainMessageAdapter(mainMessages);
        recyclerView.setAdapter(adapter);

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETMAINMESSAGE_BROADCAST");
        getMainMessageReceiver = new GetMainMessageReceiver();
        view.getContext().registerReceiver(getMainMessageReceiver,intentFilter);

        contentFilter = new IntentFilter();
        contentFilter.addAction("com.example.tword.GETCONTENT_BROADCAST");
        getContentReceiver = new GetContentReceiver();
        view.getContext().registerReceiver(getContentReceiver,contentFilter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(getMainMessageReceiver);
    }

    class  GetMainMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("createMessage");
            MainMessage mm = new MainMessage(message.getStringContent());
            mm.setMessageId(message.getMessageId());
            mm.setLastTime(new java.util.Date(message.getDate().getTime()));
            mainMessages.add(mm);
 //           Log.d("主页得到消息",mm.getHeadlin());
            adapter.notifyItemInserted(mainMessages.size() -1);
            recyclerView.scrollToPosition(mainMessages.size() -1);
        }
    }

    class  GetContentReceiver extends  BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("message");
            for(int i=0; i<mainMessages.size(); i++){
                if(mainMessages.get(i).getMessageId() == message.getMessageId()){
                    mainMessages.get(i).setLastContent(message.getStringContent());
                    mainMessages.get(i).setLastTime(new java.util.Date(message.getDate().getTime()));
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }
}
