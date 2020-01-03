package com.example.tword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import nio.NioSocketChannel;


public class AddMessage extends BaseActivity {
    private String[] problem = new String[]{"漏针","杂毛","爆洞","长短袖","油污","杂毛","爆洞","长短袖","油污","杂毛","爆洞","长短袖","油污"};
    private String[] report = new String[]{"上报问题","工作交接","询问"};
    private String[] groupData; //= new String[]{"织机部","生产部","营业部","办房"};
    private int[][] childrenData; //= new String[][]{
//            {"小明","大李","天天"},
//            {"吕布","刘备","张飞","关羽"},
//            {"小燕子","紫微","五阿哥","香妃"},
//            {"孙悟空","唐三藏","猪八戒","沙和尚"}
//    };
    private  HashMap<Integer,String> userNameMap;

    private String[] lotNum = new String[]{"sp1120","jw0125","sp1125","sp0136","jw0214","sp365","jw0125","sp1125","sp0136","jw0125","sp1125","sp0136"};
 //   private String[] lotNum = new String[]{"sp1120","jw0125","sp1125","sp1120","jw0125","sp1125","sp1120","jw0125","sp1125","sp1120","jw0125","sp1125","sp1120","jw0125","sp1125","sp1120","jw0125","sp1125","sp1120","jw0125","sp1125"};
    private FlexboxLayout flexboxLayout,lableFlexboxLayout,problemFlexboxLayout;
    private TextView reportTextView,topLableTextView,memberTextView;
    private LinearLayout topLableLinearlayout,lableLinearlayout,lotNumLinearLayout;
    private CardView lotNumcardView,problemCardView,headlineCardView;
    private ImageView reportOffonIamgeView,lotNumOffonIamgeView,problemOffonIamgeView;
    private EditText lotNumEditText,problemEditText,headlineEditText;
    private String lotNumText = "",problemText = "",memberText = "";
    private ExpandableListView memberElv;
    private TextView createMessageButton;
    private IntentFilter intentFilter;
    private GetMainMessageReceiver getMainMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_message_layout);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

        flexboxLayout = (FlexboxLayout)findViewById(R.id.add_message_flexboxlayout);
        lableFlexboxLayout = (FlexboxLayout)findViewById(R.id.add_message_lable_flexboxlayout);
        topLableLinearlayout = (LinearLayout)findViewById(R.id.top_lable_linearlayout);
        lotNumcardView = (CardView)findViewById(R.id.lot_num_cardview);
        topLableTextView = (TextView)findViewById(R.id.top_lable_textview);
        reportOffonIamgeView = (ImageView)findViewById(R.id.repot_offon_imageview);
        lotNumOffonIamgeView = (ImageView)findViewById(R.id.lot_num_offon_imageview);
        lotNumEditText = (EditText)findViewById(R.id.lot_num_edit_text);
        problemFlexboxLayout = (FlexboxLayout)findViewById(R.id.porblem_flexboxlayout);
        problemCardView = (CardView)findViewById(R.id.porblem_cardview);
        problemEditText = (EditText)findViewById(R.id.porblem_edit_text);
        problemOffonIamgeView = (ImageView)findViewById(R.id.porblem_offon_imageview);
        memberElv = (ExpandableListView)findViewById(R.id.member_expandablelistview);
        memberTextView = (TextView)findViewById(R.id.member_tv);
        headlineCardView = (CardView)findViewById(R.id.headline_cardview);
        headlineEditText = (EditText)findViewById(R.id.headline_edit_text);
        createMessageButton = (TextView)findViewById(R.id.create_message_but);

        Intent intent = getIntent();
        MyMessage rsMessage = (MyMessage) intent.getSerializableExtra("rsMessage");
        groupData = (String[]) rsMessage.getObjects()[0];
        childrenData = (int[][]) rsMessage.getObjects()[1];
        userNameMap = (HashMap<Integer, String>) rsMessage.getObjects()[2];

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.tword.GETMAINMESSAGE_BROADCAST");
        getMainMessageReceiver = new GetMainMessageReceiver();
        registerReceiver(getMainMessageReceiver,intentFilter);

        RadioGroup reportRadioGroup = new RadioGroup(this);
        float density = getResources().getDisplayMetrics().density;
        RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                (int)(108*density),
                (int)(38*density));
        int margin = (int)(6*density);
        params_rb.setMargins(margin, margin, margin, margin);
        reportRadioGroup.setOrientation(RadioGroup.HORIZONTAL);
        for(int i=0; i<report.length; i++){
            final RadioButton rb = new RadioButton(this);
            rb.setText(report[i]);
            rb.setBackgroundResource(R.drawable.lable2_48_32);
            reportRadioGroup.addView(rb,params_rb);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rb.isChecked()) {
                        switch ((String) rb.getText()) {
                            case "上报问题":
                                topLableTextView.setText((String) rb.getText());
                                HiddenAnimUtils.newInstance(view.getContext(), lableFlexboxLayout, reportOffonIamgeView, 30).toggle();
                                HiddenAnimUtils.newInstance(view.getContext(),headlineCardView,reportOffonIamgeView,50).toggle("off");
                                reportOffonIamgeView.setVisibility(View.VISIBLE);
                                lotNumcardView.setVisibility(View.VISIBLE);
                                problemCardView.setVisibility(View.VISIBLE);
                                return;
                            case "工作交接":
                            case "询问":
                                topLableTextView.setText(rb.getText());
                                reportOffonIamgeView.setVisibility(View.VISIBLE);
                                HiddenAnimUtils.newInstance(view.getContext(), lableFlexboxLayout, reportOffonIamgeView, 30).toggle();
                                HiddenAnimUtils.newInstance(view.getContext(),headlineCardView,reportOffonIamgeView,50).toggle("on");
                                lotNumcardView.setVisibility(View.GONE);
                                problemCardView.setVisibility(View.GONE);
                                return;
                        }
                    }
                }
            });
        }
        lableFlexboxLayout.addView(reportRadioGroup);

        for(int i=0; i<lotNum.length; i++){
            CheckBox cb = new CheckBox(this);
            cb.setText(lotNum[i]);
            cb.setBackgroundResource(R.drawable.lable2_48_32);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    lotNumText = lotNumEditText.getText().toString();
                    if(b){
                        lotNumText += " "+compoundButton.getText();
                        lotNumEditText.setText(lotNumText.trim());
                    } else {
                        lotNumEditText.setText(lotNumText.replace(compoundButton.getText(),"").trim());
                    }
                }

            });
            flexboxLayout.addView(cb);
        }

        for(int i=0; i<problem.length; i++){
            CheckBox cb = new CheckBox(this);
            cb.setText(problem[i]);
            cb.setBackgroundResource(R.drawable.lable2_48_32);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    problemText = problemEditText.getText().toString();
                    if(b){
                        problemText += " "+compoundButton.getText();
                        problemEditText.setText(problemText.trim());
                    } else {
                        problemEditText.setText(problemText.replace(compoundButton.getText(),"").trim());
                    }
                }

            });
            problemFlexboxLayout.addView(cb);
        }

        HiddenAnimUtils.newInstance(this,flexboxLayout,lotNumOffonIamgeView,0).toggle();
        HiddenAnimUtils.newInstance(this,problemFlexboxLayout,problemOffonIamgeView,0).toggle();

        reportOffonIamgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HiddenAnimUtils.newInstance(view.getContext(),lableFlexboxLayout,reportOffonIamgeView,2).toggle();
            }
        });

        lotNumOffonIamgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
 //               int height = flexboxLayout.getFlexLines().size() * 38;
                HiddenAnimUtils.newInstance(view.getContext(),flexboxLayout,lotNumOffonIamgeView,0).toggle();
            }
        });

        problemOffonIamgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
 //               int height = problemFlexboxLayout.getFlexLines().size() * 38;
                HiddenAnimUtils.newInstance(view.getContext(),problemFlexboxLayout,problemOffonIamgeView,0).toggle();
            }
        });

        lotNumEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
 //               int height = flexboxLayout.getFlexLines().size() * 38;
                if(b){
                    HiddenAnimUtils.newInstance(view.getContext(),flexboxLayout,lotNumOffonIamgeView,0).toggle("on");
                }else {
                    HiddenAnimUtils.newInstance(view.getContext(),flexboxLayout,lotNumOffonIamgeView,0).toggle("off");
                }
            }
        });

        problemEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
//                int height = problemFlexboxLayout.getFlexLines().size() * 38;
                if(b){
                    HiddenAnimUtils.newInstance(view.getContext(),problemFlexboxLayout,problemOffonIamgeView,0).toggle("on");
                }else {
                    HiddenAnimUtils.newInstance(view.getContext(),problemFlexboxLayout,problemOffonIamgeView,0).toggle("off");
                }
            }
        });

        final ExAdapter adapter = new ExAdapter(groupData,childrenData,this);
        memberElv.setAdapter(adapter);
        memberElv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {

            }
        });

        createMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Integer> userId = adapter.getCheckChild();
                int[] receivers = new int[userId.size()];
                for(int i=0; i<receivers.length; i++){
                    receivers[i] = userId.get(i);
                }

                MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),receivers,"createMessage");
                message.setStringContent(headlineEditText.getText().toString());
                message.setMessageLable("null");
                try {
                    NioSocketChannel.getInstance().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        memberElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                TextView cb = (TextView) view.findViewById(R.id.department_checkedview);
//                if(expandableListView.isGroupExpanded(i)){
//                    cb.setVisibility(View.GONE);
//                }else {
//                    cb.setVisibility(View.VISIBLE);
//                }
//                return false;
//            }
//        });

    }

    private void finishActivity(){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(getMainMessageReceiver);
    }

    class  GetMainMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyMessage message = (MyMessage) intent.getSerializableExtra("createMessage");
            long messageId = message.getMessageId();
            MainActivity.actionStart(AddMessage.this,messageId,message.getStringContent());
            finishActivity();
        }
    }

    class ExAdapter extends BaseExpandableListAdapter{
        private String[] group;
        private int[][] children;
        private Context context;
        private LayoutInflater mInflater;
        private Map<String,Map<Integer,Boolean>> childCheckMap;

        public ExAdapter(String[] group,int[][] children,Context context){
            super();
            this.group = group;
            this.children = children;
            this.context = context;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            childCheckMap = new HashMap<>();
            for(int i=0; i<children.length; i++){
                Map<Integer,Boolean> m = new HashMap<>();
                for(int j=0; j<children[i].length; j++){
//                    Log.d("For",children[i][j]);
                    m.put(children[i][j],false);
                }
                childCheckMap.put(group[i],m);
            }
        }

        @Override
        public int getGroupCount() {
            int count = group.length;
            return count;
        }

        @Override
        public int getChildrenCount(int i) {
            int conut = children[i].length;
            return conut;
        }

        @Override
        public Object getGroup(int i) {

            return group[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return children[i][i1];
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int i, boolean b, View view, final ViewGroup viewGroup) {
          if(view == null){
                view = mInflater.inflate(R.layout.add_message_expand_adapter_group,null);
          }
            TextView groupName = (TextView)view.findViewById(R.id.group_name_textview);
            groupName.setText(group[i]);
            TextView selectAll = (TextView)view.findViewById(R.id.department_checkedview);
            selectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(AddMessage.this,"你点击了 "+group[i].toString()+"的 全选",Toast.LENGTH_SHORT).show();
                    int size = childCheckMap.get(group[i]).size();
                    Map<Integer,Boolean> m = new HashMap<>();
                    childCheckMap.put(group[i],m);
                    memberText = "";
                    for(int j=0; j<size; j++){
                        m.put(children[i][j],true);
                    }
                    ArrayList<Integer> userId = getCheckChild();
                    for(int id:userId){
                        memberText += (userNameMap.get(id)+" ");
                    }
                    memberTextView.setText(memberText.trim());
                    memberElv.collapseGroup(i);
                    memberElv.expandGroup(i);
                }
            });
            if(b){
                selectAll.setVisibility(View.VISIBLE);
            }else {
                selectAll.setVisibility(View.GONE);
            }
            return view;

        }

        @Override
        public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
            final ChildViewHolder childViewHolder;
            if(view == null){
               view =  mInflater.inflate(R.layout.add_message_expand_adapter_child,null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.childNname = (TextView)view.findViewById(R.id.name_textview);
                childViewHolder.checkBox = (CheckBox)view.findViewById(R.id.select_checkbox);
                view.setTag(childViewHolder);
            }else {
                childViewHolder = (ChildViewHolder)view.getTag();
            }
            childViewHolder.childNname.setText(userNameMap.get(getChild(i,i1)));
//            Log.d("add", String.valueOf(childCheckMap.get(group[i])));

            boolean b1 = childCheckMap.get(group[i]).get(children[i][i1]);
            childViewHolder.checkBox.setChecked(b1);
            childViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox)view;
//                    memberText = memberTextView.getText().toString();
                    if(cb.isChecked()){
                        childCheckMap.get(group[i]).put(children[i][i1],true);
                    }else {
                        childCheckMap.get(group[i]).put(children[i][i1],false);
//                        memberTextView.setText(memberText.replace(childViewHolder.childNname.getText(),"").trim());
                    }
                    memberText = "";
                    ArrayList<Integer> userId = getCheckChild();
                    for(int id:userId){
                        memberText += (userNameMap.get(id)+" ");
                    }
                    memberTextView.setText(memberText.trim());
                }
            });
            return view;
        }

        public  ArrayList<Integer> getCheckChild(){
            ArrayList<Integer>  checkChild = new ArrayList<>();
            Set key = childCheckMap.keySet();
            Iterator it = key.iterator();
            while(it.hasNext()){
                String groupName = (String)it.next();
                HashMap<Integer,Boolean> map = (HashMap<Integer, Boolean>) childCheckMap.get(groupName);
                Set checkKey = map.keySet();
                Iterator it1 = checkKey.iterator();
                while(it1.hasNext()){
                    int userId = (int)it1.next();
                    if(map.get(userId)){
                        checkChild.add(userId);
                    }
                }
            }
            return  checkChild;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        class ChildViewHolder{
            TextView childNname;
            CheckBox checkBox;
        }
    }
}
