package com.example.tword.erp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tword.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 外层RecyclerView的 Adapter
 */
public class ErpReportOutAdapter extends RecyclerView.Adapter<ErpReportOutAdapter.ViewHolder> {
    private List<ErpReportData> reportDatas;  //数据源
    private Context mContext;

    //需传入数据源的构造方法
    public ErpReportOutAdapter(Context context,List<ErpReportData> reportDatas){
        this.reportDatas = reportDatas;
        this.mContext = context;
    }

    /**
     * 在这个方法内加载布局，和创建ViewHolder实例，并传入加载布局的VIEW
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.erp_report_out_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //在此地做点击事件监听
        viewHolder.offNoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("ERP","点击了OFFON按键  "+viewHolder.offOn);

                if(viewHolder.offOn == ViewHolder.OFF){
                    viewHolder.lotProductionTv.setText("");
                    viewHolder.todayProductionTv.setText("");
                    viewHolder.productionTv.setText("");
                    viewHolder.notProductionTv.setText("");
                    viewHolder.offNoIv.setImageResource(R.drawable.offon_24_24);

                    LinearLayoutManager manager = new LinearLayoutManager(mContext);
                    ErpReportInAdapter adapter = new ErpReportInAdapter( viewHolder.erpReportData.getItemDataList());
                    viewHolder.recyclerView.setLayoutManager(manager);
                    viewHolder.recyclerView.setAdapter(adapter);
                    viewHolder.offNoIv.setImageResource(R.drawable.offon_24_24);
                    viewHolder.offOn = ViewHolder.ON;
                }else if(viewHolder.offOn == ViewHolder.ON){
                    viewHolder.lotProductionTv.setText(viewHolder.erpReportData.getLotCount());
                    viewHolder.todayProductionTv.setText(viewHolder.erpReportData.getTodayProduction());
                    viewHolder.productionTv.setText(viewHolder.erpReportData.getProduction());
                    viewHolder.notProductionTv.setText(viewHolder.erpReportData.getNotProduction());

                    LinearLayoutManager manager = new LinearLayoutManager(mContext);
                    ErpReportInAdapter adapter = new ErpReportInAdapter(new ArrayList<ErpReportItemData>());
                    viewHolder.recyclerView.setLayoutManager(manager);
                    viewHolder.recyclerView.setAdapter(adapter);
                    viewHolder.offNoIv.setImageResource(R.drawable.offon2_24_24);
                    viewHolder.offOn = ViewHolder.OFF;
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
        ErpReportData erpReportData = reportDatas.get(position);
        holder.erpReportData = erpReportData;
        holder.departmentTv.setText(erpReportData.getDepartment());

        if(holder.offOn == ViewHolder.OFF){
            holder.lotProductionTv.setText(erpReportData.getLotCount());
            holder.todayProductionTv.setText(erpReportData.getTodayProduction());
            holder.productionTv.setText(erpReportData.getProduction());
            holder.notProductionTv.setText(erpReportData.getNotProduction());
            holder.offNoIv.setImageResource(R.drawable.offon2_24_24);
        }else if(holder.offOn == ViewHolder.ON){
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            ErpReportInAdapter adapter = new ErpReportInAdapter(erpReportData.getItemDataList());
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setAdapter(adapter);
            holder.offNoIv.setImageResource(R.drawable.offon_24_24);

            holder.lotProductionTv.setText("");
            holder.todayProductionTv.setText("");
            holder.productionTv.setText("");
            holder.notProductionTv.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return reportDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private static final int OFF = 0;
        private static final int ON = 1;
        private int offOn = 0;
        private ErpReportData erpReportData = null;
        private TextView departmentTv;          //部门
        private TextView lotProductionTv;       //发织数
        private TextView todayProductionTv;     //今天完成数
        private TextView productionTv;          //总完成数
        private TextView notProductionTv;       //未完成数
        private ImageView offNoIv;              //开关
        private RecyclerView recyclerView;
        View reportView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportView = itemView;
            recyclerView = itemView.findViewById(R.id.erp_report_in_rv);
            lotProductionTv = itemView.findViewById(R.id.erp_report_d_lotproduction_tv);
            todayProductionTv = itemView.findViewById(R.id.erp_report_d_todayproduction_tv);
            productionTv = itemView.findViewById(R.id.erp_report_d_production_tv);
            notProductionTv = itemView.findViewById(R.id.erp_report_d_ontproduction_tv);
            offNoIv = itemView.findViewById(R.id.erp_report_d_offon_iv);
            departmentTv = itemView.findViewById(R.id.erp_report_department_tv);
        }
    }
}
