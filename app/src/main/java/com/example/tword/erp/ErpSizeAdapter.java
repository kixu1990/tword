package com.example.tword.erp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tword.R;

import java.util.List;

public class ErpSizeAdapter extends RecyclerView.Adapter<ErpSizeAdapter.ViewHolder> {
    private List<ErpSizeData> datas;  //数据源
    private Context mContext;

    /**
     * 带数据源参数的构造方法
     * @param mContext
     * @param datas
     */
    public ErpSizeAdapter(Context mContext,List<ErpSizeData> datas){
        this.datas = datas;
        this.mContext = mContext;
    }

    /**
     * 在这个方法内传入布局，和创建ViewHolder实例，并传入加载布局的VIEW
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.erp_report_size_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        //在这里做子项的事件监听

        return viewHolder;
    }

    /**
     * 每次将ITEM滚入屏幕时，都会调用此方法，在这个方法内为子项各个VIEW赋值
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ErpSizeData erpSizeData = datas.get(position);
        holder.sizeTv.setText(erpSizeData.getSize());
        holder.todayProductionTv.setText(String.valueOf(erpSizeData.getTodayProduction()));
        holder.productionTv.setText(String.valueOf(erpSizeData.getProduction()));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView colorTv;
        private TextView sizeTv;
        private TextView todayProductionTv;
        private TextView productionTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorTv = itemView.findViewById(R.id.erp_report_size_item_color_tv);
            sizeTv = itemView.findViewById(R.id.erp_report_size_item_size_tv);
            todayProductionTv = itemView.findViewById(R.id.erp_report_size_item_todayproduction_tv);
            productionTv = itemView.findViewById(R.id.erp_report_size_item_production_tv);
        }
    }
}
