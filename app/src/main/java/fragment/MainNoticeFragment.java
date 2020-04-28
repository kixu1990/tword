package fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tword.R;
import com.example.tword.User;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import fmtadapter.MainNotice;
import fmtadapter.MainNoticeAdapter;
import litepal.SatffDB;
import litepal.departmentDB;

/**
 * Created by kixu on 2019/10/24.
 */

public class MainNoticeFragment extends Fragment {
    private List<MainNotice> datas;               //recyclerView 的数据源
    private HashMap<Integer,byte[]> userImageMap; //头像图片源
    private ExpandableListView expandableListView;
    private TextView allPercent;                 //全员选择百分比
    private TextView allSelect;                  //全员选择按钮
    private CardView noticeCardView;
    private LinearLayout addNotice;
    private CardView closeAddButton;             //取消按钮
    private CardView putAddButton;               //发布按钮
    private EditText addTitle;                   //添加标题
    private EditText addStartLine;               //添加抬头
    private EditText addConetnt;                 //添加内容
    private EditText addSignature;               //添加签名
    private TextView addDate;                    //添加日期

    private ArrayList<String> departments;
    private ArrayList<ArrayList<SatffDB>> satffs;

    private HashMap<String,HashSet<Integer>> selects;//发送新公告时选择接收成员
    private HashMap<String,Integer> departmentSize;  //各个部门的总人数
    private HashMap<Integer,String> userNames;       //用户名于用户ID的对应关系
    private List<SatffDB> satffDBS;
    private List<departmentDB> departmentDBS;

    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mian_notice_frament,container,false);

        datasInit();
        //公告栏recyclerView设置--------------------------------------------------------------------
        LinearLayoutManager manager = new LinearLayoutManager(container.getContext());
        final MainNoticeAdapter adapter = new MainNoticeAdapter(datas);

        selects = new HashMap<>();

        expandableListView = view.findViewById(R.id.main_notice_fmt_elv);
        noticeCardView = view.findViewById(R.id.main_notice_cv);
        addNotice = view.findViewById(R.id.main_notice_addnotice_cv);
        closeAddButton = view.findViewById(R.id.main_notice_closeadd_bt);
        putAddButton = view.findViewById(R.id.mian_notice_putadd_bt);
        addTitle = view.findViewById(R.id.main_notice_add_title_et);
        addStartLine = view.findViewById(R.id.main_notice_add_startline_et);
        addConetnt = view.findViewById(R.id.main_notice_add_content_et);
        addSignature = view.findViewById(R.id.main_notice_add_signature_et);
        addDate = view.findViewById(R.id.main_notice_add_date_tv);
        DateFormat mediumFormt = DateFormat.getDateInstance(DateFormat.MEDIUM);
        addDate.setText(mediumFormt.format(new Date(System.currentTimeMillis())));

        recyclerView = view.findViewById(R.id.main_notice_fmt_rv);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(datas.size() -1);
        //------------------------------------------------------------------------------------------

        //从数据库提取用户数据----------------------------------------------------------------------
        satffDBS = DataSupport.select("*").where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                .find(SatffDB.class);

        departmentDBS =  DataSupport.select("*").where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                .find(departmentDB.class);

        departmentSize = new HashMap<>();
        departments = new ArrayList<>();
        userNames = new HashMap<>();
        for(departmentDB departmentDB :departmentDBS){
            departments.add(departmentDB.getDepartmentName());
        }

        satffs = new ArrayList<>();
        for(String name:departments){
            ArrayList<SatffDB> satffList = new ArrayList<>();
            int i = 0;
            for(SatffDB s: satffDBS){
                if(s.getDepartment().equals(name)){
                    satffList.add(s);
                    i ++;
                    userNames.put(s.getSatffId(),s.getSatffName());
                }
            }
            departmentSize.put(name,i);//统计各个部门的总人数
            satffs.add(satffList);
        }


        ExAdapter exAdapter = new ExAdapter(container.getContext(),departments,satffs);
        expandableListView.setAdapter(exAdapter);
        expandableListView.setDivider(null);      //取消分割线
        expandableListView.setChildDivider(null); //取消子项分割线

        userImageMap = new HashMap<>(); //头像
        for(int i=0; i<satffDBS .size(); i++){
            userImageMap.put(satffDBS.get(i).getSatffId(),satffDBS.get(i).getUserImage());
        }
        //-------------------------------------------------------------------------------------------

        allPercent = view.findViewById(R.id.main_notice_add_allpercent_tv);
        allPercent.setText("0/"+satffDBS.size());
        allSelect = view.findViewById(R.id.main_notice_add_allselect_tv);

        allSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allSelect.getText().equals("全员")){
                    selectSafft("全厂",0,true);
                    allSelect.setText("取消全员");
                    allPercent.setText(satffDBS.size()+"/"+satffDBS.size());
                }else {
                    selectSafft("全厂",0,false);
                    allSelect.setText("全员");
                    allPercent.setText("0/"+satffDBS.size());
                }
                adapter.notifyDataSetChanged();
                for(int i=0; i<departments.size(); i++){
                    expandableListView.collapseGroup(i);
                    expandableListView.expandGroup(i);
                    expandableListView.collapseGroup(i);
                }
            }
        });
        /**
         * 消息按扭事件
         */
        closeAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTitle.setText("");
                addStartLine.setText("");
                addConetnt.setText("");
                addSignature.setText("");
                noticeCardView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        /**
         * 发布按钮事件
         */
        putAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTitle.setText("");
                addStartLine.setText("");
                addConetnt.setText("");
                addSignature.setText("");
                noticeCardView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    /**
     * 刷新expand
     * @param i
     * @param b
     */
    private void expandGroup(int i,boolean b){
        if(b) {
            expandableListView.collapseGroup(i);
            expandableListView.expandGroup(i);
        }else {
            expandableListView.expandGroup(i);
            expandableListView.collapseGroup(i);
        }
    }

    private void datasInit(){
        datas = new ArrayList<>();
        MainNotice  notice1 = new MainNotice("通知","各位员工:","行政部","2020-4-8");
        notice1.setContent("      1、星期天照常放假！\n      2、星期六放假一天\n      3、电机部夜班调入白班\n      4、\n      5、\n      6、");
        MainNotice  notice2 = new MainNotice("告示","各位员工:","人事部","2020-4-8");
        notice2.setContent("      1、星期天照常放假！\n      2、星期六放假一天\n      3、电机部夜班调入白班\n      4、\n      5、\n      6、\n      7、\n      8、\n      9、\n      10、");
        MainNotice  notice3 = new MainNotice("工作安排","各位员工:","行政部","2020-4-8");
        notice3.setContent("      1、星期天照常放假！\n      2、星期六放假一天\n      3、电机部夜班调入白班\n      4、\n      5、\n      6、");
        datas.add(notice1);
        datas.add(notice2);
        datas.add(notice3);
    }

    /**
     * 处理点击选择接收成员方法
     * @param department
     * @param userID
     * @return
     */
    private void selectSafft(String department,int userID,boolean b){
        //全厂选择
        if(department.equals("全厂")){
            if(b){
                for(String d:departments){
                    HashSet<Integer> set = new HashSet<>();
                    for(SatffDB satffDB:satffDBS){
                        if(satffDB.getDepartment().equals(d)){
                            set.add(satffDB.getSatffId());
                        }
                    }
                    selects.put(d,set);
                }
            }else {
                selects.clear();
            }
            return;
        }

        //单部门选择
        if(userID == 0){
            if(b){
                HashSet<Integer> set = new HashSet<>();
                for(SatffDB satffDB:satffDBS){
                    if(satffDB.getDepartment().equals(department)){
                        set.add(satffDB.getSatffId());
                    }
                }
                selects.put(department,set);
            }else {
                selects.remove(department);
            }
            return;
        }

        //单个人选择
        HashSet<Integer> set;
        if(selects.get(department) ==  null){
            set = new HashSet<>();
            selects.put(department,set);
        }
        set = selects.get(department);
        if(b){
            set.add(userID);
        }else {
            set.remove(userID);
        }
        selects.put(department,set);
        return ;
    }

    public void openAddNotice(){
        recyclerView.setVisibility(View.GONE);
        noticeCardView.setVisibility(View.VISIBLE);
    }

    public void closeAddNotice(){
        recyclerView.setVisibility(View.VISIBLE);
        noticeCardView.setVisibility(View.GONE);
    }

    class ExAdapter extends BaseExpandableListAdapter{
        private Context mContext;
        private ArrayList<String> groupDatas;          //父项数据
        private ArrayList<ArrayList<SatffDB>> itemDatas;  //子项数据
        private LayoutInflater mLayoutInflater;

        public ExAdapter(Context context,ArrayList<String> groupDatas,ArrayList<ArrayList<SatffDB>> itemDatas){
            this.mContext = context;
            this.groupDatas = groupDatas;
            this.itemDatas = itemDatas;
            mLayoutInflater = LayoutInflater.from(context);
        }

        //取得父项数量
        @Override
        public int getGroupCount() {
            return groupDatas.size();
        }

        //取得某个子项数量
        @Override
        public int getChildrenCount(int groupPosition) {
            return itemDatas.get(groupPosition).size();
        }

        //获得父项
        @Override
        public Object getGroup(int groupPosition) {
            return groupDatas.get(groupPosition);
        }

       //获得子项
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return itemDatas.get(groupPosition).get(childPosition);
        }

        //父项的ID
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //子项的ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        //获得的父项的VIEW
        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = mLayoutInflater.inflate(R.layout.main_notice_expand_adapter_group,parent,false);
            }
            final String departmentName = groupDatas.get(groupPosition);
            final TextView departmentTV = convertView.findViewById(R.id.main_notice_group_department_tv);
            departmentTV.setText(departmentName);
            final TextView percentTv = convertView.findViewById(R.id.main_notice_group_percent_tv);
            final TextView getAllTv = convertView.findViewById(R.id.main_notice_group_getall_tv);
            //在这里做父项的事件监听
            getAllTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView groupGetAll = (TextView)v;
                    if(groupGetAll.getText().equals("全选")){
                        selectSafft(departmentName,0,true);
                        getAllTv.setText("取消全选");
                        expandGroup(groupPosition,false);
                    }else {
                        selectSafft(departmentName,0,false);
                        getAllTv.setText("全选");
                        expandGroup(groupPosition,false);
                    }

                    int f = 0;
                    int z = satffDBS.size();
                    Set set = selects.keySet();
                    Iterator it = set.iterator();
                    while(it.hasNext()){
                        String department = (String) it.next();
                        HashSet s = selects.get(department);
                        f += s.size();
                    }
                    if(f == z){
                        allSelect.setText("取消全员");
                        allPercent.setText(f+"/"+z);
                    }else {
                        allSelect.setText("全员");
                        allPercent.setText(f+"/"+z);
                    }
                    int af = selects.get(departmentName) == null ? 0:selects.get(departmentName).size();
                    int az = departmentSize.get(departmentName) == null ? 0:departmentSize.get(departmentName);
                    percentTv.setText(af+"/"+az);
                }
            });
            int f = selects.get(departmentName) == null ? 0:selects.get(departmentName).size();
            int z = departmentSize.get(departmentName) == null ? 0:departmentSize.get(departmentName);
            percentTv.setText(f+"/"+z);
            if(f == z){
                getAllTv.setText("取消全选");
            }else {
                getAllTv.setText("全选");
            }
            return convertView;
        }

        //获得子项的VIEW
        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
            final SatffDB user = itemDatas.get(groupPosition).get(childPosition);
            if(convertView == null){
                convertView = mLayoutInflater.inflate(R.layout.main_notice_expand_adaper_child,parent,false);
            }
            CircleImageView imageView = convertView.findViewById(R.id.main_notice_adapter_child_userimage_tv);
            RequestOptions options = new RequestOptions().override(80,80).centerCrop();
            Glide.with(mContext).load(user.getUserImage()).apply(options).into(imageView);
            TextView userName = convertView.findViewById(R.id.main_notice_adapter_child_username_tv);
            userName.setText(user.getSatffName());
            CheckBox checkBox = convertView.findViewById(R.id.main_notice_adapter_child_select_cb);

            String department = departments.get(groupPosition);
            HashSet set = selects.get(department);
            boolean b = false;
            if(set != null) {
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    int id = (int) iterator.next();
                    if (id == user.getSatffId()) {
                        b = true;
                    }
                }
            }
            checkBox.setChecked(b);
            //在这里做子项的事件监听
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox) v).isChecked()){
                        selectSafft(groupDatas.get(groupPosition),user.getSatffId(),true);
                    }else {
                        selectSafft(groupDatas.get(groupPosition),user.getSatffId(),false);
                    }
                    int f = 0;
                    int z = satffDBS.size();
                    Set set = selects.keySet();
                    Iterator it = set.iterator();
                    while(it.hasNext()){
                        String department = (String) it.next();
                        HashSet s = selects.get(department);
                        f += s.size();
                    }
                    if(f == z){
                        allSelect.setText("取消全员");
                        allPercent.setText(f+"/"+z);
                    }else {
                        allSelect.setText("全员");
                        allPercent.setText(f+"/"+z);
                    }
                    expandGroup(groupPosition,true);
                }
            });

            return convertView;
        }

        // 子项是否可选中,如果要设置子项的点击事件,需要返回true
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}
