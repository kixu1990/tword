package com.example.tword.erp;

import android.content.Context;
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
 * 2020-3-24 by kixu
 * ERP报表第二层 颜色活动 recycleView 支持器类
 */
public class ErpColorAdapter extends RecyclerView.Adapter<ErpColorAdapter.ViewHolder> {
    private List<ErpColorData> datas;   //数据源
    private Context mContext;

    //带数据源的构造方法
    public ErpColorAdapter(Context mContext,List<ErpColorData> datas){
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.erp_report_color_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //在这里添加子项的监听方法
        viewHolder.offonIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewHolder.offOn == ViewHolder.OFF){
                    viewHolder.offOn = ViewHolder.ON;
                    viewHolder.todayProductionTv.setText("");
                    viewHolder.productionTv.setText("");
                    viewHolder.offonIv.setImageResource(R.drawable.offon_24_24);

                    LinearLayoutManager manager = new LinearLayoutManager(mContext);
                    ErpSizeAdapter adapter = new ErpSizeAdapter(mContext,viewHolder.erpColorData.getErpSizeDatas());
                    viewHolder.recyclerView.setLayoutManager(manager);
                    viewHolder.recyclerView.setAdapter(adapter);
                }else if(viewHolder.offOn == ViewHolder.ON){
                    viewHolder.offOn = ViewHolder.OFF;
                    viewHolder.colorTv.setText(viewHolder.erpColorData.getColor());
                    viewHolder.todayProductionTv.setText(String.valueOf(viewHolder.erpColorData.getTodayProduction()));
                    viewHolder.productionTv.setText(String.valueOf(viewHolder.erpColorData.getProduction()));
                    viewHolder.offonIv.setImageResource(R.drawable.offon2_24_24);

                    LinearLayoutManager manager = new LinearLayoutManager(mContext);
                    ErpSizeAdapter adapter = new ErpSizeAdapter(mContext,new ArrayList<ErpSizeData>());
                    viewHolder.recyclerView.setLayoutManager(manager);
                    viewHolder.recyclerView.setAdapter(adapter);
                }
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
        ErpColorData erpColorData = datas.get(position);
        holder.erpColorData = erpColorData;
        holder.colorTv.setText(erpColorData.getColor());

        if(holder.offOn == ViewHolder.OFF){
            holder.colorTv.setText(erpColorData.getColor());
            holder.todayProductionTv.setText(String.valueOf(erpColorData.getTodayProduction()));
            holder.productionTv.setText(String.valueOf(erpColorData.getProduction()));
            holder.offonIv.setImageResource(R.drawable.offon2_24_24);
        }else if(holder.offOn == ViewHolder.ON){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            ErpSizeAdapter adapter = new ErpSizeAdapter(mContext,erpColorData.getErpSizeDatas());
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setAdapter(adapter);
            holder.offonIv.setImageResource(R.drawable.offon_24_24);

            holder.todayProductionTv.setText("");
            holder.productionTv.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private static final int OFF = 0;
        private static final int ON = 1;
        private int offOn = 1;
        private TextView colorTv ;
        private TextView todayProductionTv;
        private TextView productionTv;
        private ImageView offonIv;
        private RecyclerView recyclerView;
        private ErpColorData erpColorData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorTv = itemView.findViewById(R.id.erp_report_color_item_color_tv);
            todayProductionTv = itemView.findViewById(R.id.erp_report_color_item_todayproduction_tv);
            productionTv = itemView.findViewById(R.id.erp_report_color_item_production_tv);
            offonIv = itemView.findViewById(R.id.erp_report_color_item_offon_iv);
            recyclerView = itemView.findViewById(R.id.erp_report_size_rv);
        }
    }

}
