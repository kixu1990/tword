package com.example.tword.erp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tword.R;

import java.util.List;

import message.MyMessage;

/**
 * 内层RecyclerView的 Adapter
 */
public class ErpReportInAdapter extends RecyclerView.Adapter<ErpReportInAdapter.ViewHolder> {
    private List<ErpReportItemData> itemDatas; //数据源

    //需要传入数据源的构造方法
    public ErpReportInAdapter(List<ErpReportItemData> itemDatas){this.itemDatas = itemDatas;}

    /**
     * 创建ViewHolder的实例，加载布局，并把布局传入ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.erp_report_in_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //在这里添加子项事件监听
        viewHolder.colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 //               Toast.makeText(view.getContext(),viewHolder.department+" : "+viewHolder.lotNumber.getText(),Toast.LENGTH_SHORT).show();
                MyMessage message = new MyMessage(0,new int[]{0},"erpColorDatas");
                message.setObjects(new Object[]{viewHolder.department,viewHolder.lotNumber.getText(),viewHolder.erpReportItemData.getErpColorDataList()});

//                ErpColorActivity.actionStart(parent.getContext(),message);
                Intent intent = new Intent(parent.getContext(),ErpColorActivity.class);
                intent.putExtra("datas",message);
                parent.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    /**
     * 每次将ITEM滚入屏幕时，都会调用此方法，在这个方法内为子项各个VIEW赋值
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ErpReportItemData erpReportItemData = itemDatas.get(position);
        holder.lotNumber.setText(erpReportItemData.getLotNumber());
        holder.toltal.setText(String.valueOf(erpReportItemData.getToltal()));
        holder.todayProduction.setText(String.valueOf(erpReportItemData.getTodayProduction()));
        holder.production.setText(String.valueOf(erpReportItemData.getProduction()));
        holder.notProduction.setText(String.valueOf(erpReportItemData.getNotProduction()));
        holder.progress.setText(String.valueOf(erpReportItemData.getProgress())+"%");
        holder.department = erpReportItemData.getDepartment();
        holder.erpReportItemData = erpReportItemData;
        if(erpReportItemData.getProgress() >= 100){
            holder.progress.setTextColor(Color.GREEN);
        }else if(erpReportItemData.getProgress() < 100){
            holder.progress.setTextColor(Color.RED);
        }

    }

    /**
     * 返回总行数
     * @return
     */
    @Override
    public int getItemCount() {
        return itemDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private  String department;      //部门
        private TextView lotNumber;      //批号
        private TextView toltal;         //总数
        private TextView todayProduction;//今日完成数
        private TextView production;     //总完成数
        private TextView notProduction;  //未完成数
        private TextView progress;       //进度
        private ErpReportItemData erpReportItemData; //单项数据
        private View colorView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView;
            lotNumber = itemView.findViewById(R.id.rep_lotnumber_tv);
            toltal = itemView.findViewById(R.id.rep_total_tv);
            todayProduction = itemView.findViewById(R.id.rep_todayproduction_tv);
            production = itemView.findViewById(R.id.rep_production_tv);
            notProduction = itemView.findViewById(R.id.rep_notproduction_tv);
            progress = itemView.findViewById(R.id.rep_progress_tv);
        }
    }
}
