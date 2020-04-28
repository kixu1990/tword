package com.example.tword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
import fragment.MainNoticeFragment;
import litepal.SatffDB;
import litepal.departmentDB;
import materialdesignutil.StatusBarUtil;

public class AddNoticeActivity extends AppCompatActivity {
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

    private HashMap<String, HashSet<Integer>> selects;//发送新公告时选择接收成员
    private HashMap<String,Integer> departmentSize;  //各个部门的总人数
    private HashMap<Integer,String> userNames;       //用户名于用户ID的对应关系
    private List<SatffDB> satffDBS;
    private List<departmentDB> departmentDBS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notice_layout);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

        selects = new HashMap<>();

        expandableListView = findViewById(R.id.add_notice_fmt_elv);
//        noticeCardView = findViewById(R.id.add_notice_cv);
        addNotice = findViewById(R.id.add_notice_addnotice_cv);
        closeAddButton = findViewById(R.id.add_notice_closeadd_bt);
        putAddButton = findViewById(R.id.add_notice_putadd_bt);
        addTitle = findViewById(R.id.add_notice_add_title_et);
        addStartLine = findViewById(R.id.add_notice_add_startline_et);
        addConetnt = findViewById(R.id.add_notice_add_content_et);
        addSignature = findViewById(R.id.add_notice_add_signature_et);
        addDate = findViewById(R.id.add_notice_add_date_tv);
        DateFormat mediumFormt = DateFormat.getDateInstance(DateFormat.MEDIUM);
        addDate.setText(mediumFormt.format(new Date(System.currentTimeMillis())));

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


        AddNoticeActivity.ExAdapter exAdapter = new AddNoticeActivity.ExAdapter(this,departments,satffs);
        expandableListView.setAdapter(exAdapter);
        expandableListView.setDivider(null);      //取消分割线
        expandableListView.setChildDivider(null); //取消子项分割线

        userImageMap = new HashMap<>(); //头像
        for(int i=0; i<satffDBS .size(); i++){
            userImageMap.put(satffDBS.get(i).getSatffId(),satffDBS.get(i).getUserImage());
        }
        //-------------------------------------------------------------------------------------------

        allPercent = findViewById(R.id.add_notice_add_allpercent_tv);
        allPercent.setText("0/"+satffDBS.size());
        allSelect = findViewById(R.id.add_notice_add_allselect_tv);

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
                addConetnt.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n");
                addSignature.setText("");
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
                addConetnt.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n");
                addSignature.setText("");
            }
        });

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

    class ExAdapter extends BaseExpandableListAdapter {
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
