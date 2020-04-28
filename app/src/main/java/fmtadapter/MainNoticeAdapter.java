package fmtadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tword.R;

import java.util.List;

public class MainNoticeAdapter extends RecyclerView.Adapter<MainNoticeAdapter.ViewHolder> {
    private List<MainNotice> mainNotices;  //数据源

    /**
     * 带数据源的构造方法
     * @param mainNotices
     */
    public MainNoticeAdapter(List<MainNotice> mainNotices){
        this.mainNotices = mainNotices;
    }

    /**
     * 创建ViewHolder实例，在这个方法内加载布局，并把加载出来的布局传入构造函数当中
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载布局进来
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_notice_rv_adapter,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //在这里做事件点击
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.commentLinearLayout.getVisibility() == View.GONE){
                    viewHolder.commentLinearLayout.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.commentLinearLayout.setVisibility(View.GONE);
                }
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainNotice mainNotice = mainNotices.get(position);
        holder.title.setText(mainNotice.getTitle());
        holder.startline.setText(mainNotice.getStartline());
        holder.content.setText(mainNotice.getContent());
        holder.signature.setText(mainNotice.getSignature());
        holder.date.setText(mainNotice.getDate());
    }

    @Override
    public int getItemCount() {
        return mainNotices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;     //标题
        private TextView startline; //开头
        private TextView content;   //内容
        private TextView signature; //签名
        private TextView date;      //日期
        private ImageView good;      //点赞接钮
        private ImageView comment;   //评论按钮
        private LinearLayout commentLinearLayout; //评论区
        private View mainNoticeView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainNoticeView = itemView;
            title = itemView.findViewById(R.id.main_notice_child_title_tv);
            startline = itemView.findViewById(R.id.main_notice_child_startline_tv);
            content = itemView.findViewById(R.id.main_notice_child_content_tv);
            signature = itemView.findViewById(R.id.main_notice_child_signature_tv);
            date = itemView.findViewById(R.id.main_notice_child_date_tv);
            good = itemView.findViewById(R.id.notice_comment_good_iv);
            comment = itemView.findViewById(R.id.notice_comment_button_iv);
            commentLinearLayout = itemView.findViewById(R.id.notice_comment_linearlayout);
        }
    }
}
