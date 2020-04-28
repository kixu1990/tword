package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_messsage_fragment,container,false);
        recyclerView = view.findViewById(R.id.main_message_fmt_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

        context = view.getContext();

        recyclerView.setLayoutManager(layoutManager);
        mainMessages.clear();

        //防止活动被回收后的数据丢失
        if(savedInstanceState != null){
            mainMessages = savedInstanceState.getParcelableArrayList("mainMessages");
        }else {
            mainMessagesInit();
        }

        adapter = new MainMessageAdapter(mainMessages);
        recyclerView.setAdapter(adapter);

        //开启两个广播接收器-------------------------------------------------------------------------
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETMAINMESSAGE_BROADCAST");
        getMainMessageReceiver = new GetMainMessageReceiver();
        view.getContext().registerReceiver(getMainMessageReceiver,intentFilter);

        contentFilter = new IntentFilter();
        contentFilter.addAction("com.example.tword.GETCONTENT_BROADCAST");
        getContentReceiver = new GetContentReceiver();
        view.getContext().registerReceiver(getContentReceiver,contentFilter);
        //-------------------------------------------------------------------------------------------

        return view;
    }

    /**
     * 初始化数据
     */
    private void mainMessagesInit(){
        String userId = String.valueOf( User.getINSTANCE().getUserId());
        List<MainMessageDB> mainMessageDBs = DataSupport.select("*")
                .where("userId = ?",userId)
                .find(MainMessageDB.class);

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
            }else {
                lastTime = mm.getDate();
            }
            mainMessage.setImage(mm.getImage());
            mainMessage.setLastContent(lastContent);
            mainMessage.setLastTime(lastTime);
            mainMessages.add(mainMessage);
        }
    }

    /**
     * 保存数据 防止手机后台重启活动
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mainMessages", (ArrayList<? extends Parcelable>) mainMessages);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(getMainMessageReceiver); //关闭广播接收器
        getActivity().unregisterReceiver(getContentReceiver);     //半闭广播接收器
    }

    /**
     * 接收创建消息的广播接收器内部类
     */
    class  GetMainMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("createMessage");
            MainMessage mm = new MainMessage(message.getStringContent());
            mm.setImage((byte[])message.getObjects()[0]);
            mm.setMessageId(message.getMessageId());
            mm.setLastTime(new java.util.Date(message.getDate().getTime()));
            mainMessages.add(mm);
            adapter.notifyItemInserted(mainMessages.size() -1);
            recyclerView.scrollToPosition(mainMessages.size() -1);
        }
    }

    /**
     * 接收消息内容的广播接收器内部类
     */
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
