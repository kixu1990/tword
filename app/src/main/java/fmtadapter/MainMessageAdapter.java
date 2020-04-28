package fmtadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tword.MainActivity;
import com.example.tword.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mutils.DoubleClickUtil;

/**
 * Created by kixu on 2019/12/6.
 * RecyclerView 的支持器类
 */

public class MainMessageAdapter extends RecyclerView.Adapter<MainMessageAdapter.ViewHolder>{

    private List<MainMessage> mMainMessages;  //数据源

    /**
     * 传入数据源的构造方法
     * @param mainMessages
     */
    public MainMessageAdapter(List<MainMessage> mainMessages){
        this.mMainMessages = mainMessages;
    }

    /**
     * 创建ViewHolder实例，在这个方法内加载布局，并把加载出来的布局传入构造函数当中
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        //加载布局进来
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_message_rv_adapter,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);//构造出ViewHolder
        //在这里做点击事件监听
        viewHolder.mainMsgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //防止连击
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                int position = viewHolder.getAdapterPosition();
                MainMessage mainMessage = mMainMessages.get(position);
                long msgId = mainMessage.getMessageId();
                MainActivity.actionStart(parent.getContext(),msgId,mainMessage.getHeadlin());
            }
        });
        return viewHolder;
    }

    /**
     * 用于对RecyclerView子项的数据进行赋值，会在每个子项被滚动到屏幕内的时候执行
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainMessage mainMessage = mMainMessages.get(position);
        RequestOptions options = new RequestOptions().override(80,80).centerCrop();
        Glide.with(holder.itemView).load(mainMessage.getImage()).apply(options).into(holder.imageView);
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
        private CircleImageView imageView;
        View mainMsgView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainMsgView = itemView;
            imageView = itemView.findViewById(R.id.main_message_image_civ);
            messageHeadlin = itemView.findViewById(R.id.main_message_rv_tv);
            lastContent = itemView.findViewById(R.id.main_message_last_msg_tv);
            lastContentTime = itemView.findViewById(R.id.main_message_last_time_tv);
        }
    }
}
