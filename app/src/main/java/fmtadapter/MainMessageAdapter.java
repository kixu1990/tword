package fmtadapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tword.MainActivity;
import com.example.tword.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kixu on 2019/12/6.
 */

public class MainMessageAdapter extends RecyclerView.Adapter<MainMessageAdapter.ViewHolder>{

    private List<MainMessage> mMainMessages;

    public MainMessageAdapter(List<MainMessage> mainMessages){
        this.mMainMessages = mainMessages;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_message_rv_adapter,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mainMsgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                MainMessage mainMessage = mMainMessages.get(position);
                long msgId = mainMessage.getMessageId();
                MainActivity.actionStart(parent.getContext(),msgId,mainMessage.getHeadlin());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainMessage mainMessage = mMainMessages.get(position);
        holder.messageHeadlin.setText(mainMessage.getHeadlin());
        holder.lastContent.setText(mainMessage.getLastContent());
        String date = "";
        SimpleDateFormat formatTime = new SimpleDateFormat("a h:mm");
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        if(mainMessage != null) {
            if (DateFormat.getDateInstance().format(mainMessage.getLastTime()).equals(DateFormat.getDateInstance().format(new Date(System.currentTimeMillis())))) {
                date = formatTime.format(mainMessage.getLastTime());
            } else {
                date = formatDate.format(mainMessage.getLastTime());
            }
        }
        holder.lastContentTime.setText(mainMessage.getLastTime() == null?"": date);
    }

    @Override
    public int getItemCount() {
        return mMainMessages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView messageHeadlin;
        private TextView lastContent;
        private TextView lastContentTime;
        View mainMsgView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainMsgView = itemView;
            messageHeadlin = itemView.findViewById(R.id.main_message_rv_tv);
            lastContent = itemView.findViewById(R.id.main_message_last_msg_tv);
            lastContentTime = itemView.findViewById(R.id.main_message_last_time_tv);
        }
    }
}
