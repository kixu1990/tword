package fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tword.R;
import com.example.tword.User;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import litepal.SatffDB;
import litepal.departmentDB;

/**
 * Created by kixu on 2019/10/24.
 */

public class MainSatfflistFragment extends Fragment {

    ExpandableListView satfflist;
    String[] groupData;
    int[][] childData;
    HashMap<Integer,String> userNameMap;
    HashMap<Integer,byte[]> userImageMap;
    HashMap<Integer,String> userPostMap, userPoneNumberMap,userEmailMap,userStateMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_stafflist_fragment,container,false);

        satfflist = (ExpandableListView) view.findViewById(R.id.stafflist_expandablelistview);

        List<departmentDB> departmentDBs = DataSupport.select("*")
                                                      .where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                                      .find(departmentDB.class);
        List<SatffDB> satffDBS = DataSupport.select("*")
                                            .where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                                            .find(SatffDB.class);
        userImageMap = new HashMap<>();
        userNameMap = new HashMap<>();
        userPostMap =new HashMap<>();
        userPoneNumberMap = new HashMap<>();
        userEmailMap = new HashMap<>();
        userStateMap = new HashMap<>();
        for( SatffDB satff : satffDBS){
            userNameMap.put(satff.getSatffId(),satff.getSatffName());
            userImageMap.put(satff.getSatffId(),satff.getUserImage());
            userPostMap.put(satff.getSatffId(),satff.getPost());
            userPoneNumberMap.put(satff.getSatffId(),satff.getPhoneNumber());
            userEmailMap.put(satff.getSatffId(),satff.getEmail());
            userStateMap.put(satff.getSatffId(),satff.getState());
        }
        String[] ss = new String[departmentDBs.size()];
        for(int i=0; i<ss.length; i++){
            ss[i] = departmentDBs.get(i).getDepartmentName();
        }
        groupData = ss;

        int[][] children = new int[departmentDBs.size()][];
        for(int i=0; i<departmentDBs.size(); i++){
            ArrayList<Integer> intList = new ArrayList();
            for(int j=0; j<satffDBS.size(); j++){
                if(departmentDBs.get(i).getDepartmentName().equals(satffDBS.get(j).getDepartment())){
                    if(satffDBS.get(j).getSatffId() != User.getINSTANCE().getUserId()) {
                        intList.add(satffDBS.get(j).getSatffId());
                    }
                }
            }
            int[] is = new int[intList.size()];
            for(int g=0; g<is.length; g++){
                if(intList.get(g) != User.getINSTANCE().getUserId()) {
                    is[g] = intList.get(g);
                }
            }
            children[i] = is;
        }
        childData = children;

        ExAdapter adapter = new ExAdapter(groupData,childData,this.getContext());
        satfflist.setAdapter(adapter);

        return view;
    }

    class ExAdapter extends BaseExpandableListAdapter{

        private String[] group;
        private int[][] child;
        private Context context;
        private LayoutInflater inflater;

        public ExAdapter(String[] group, int[][] child, Context context){
            super();
            this.group = group;
            this.child = child;
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getGroupCount() {
            int count = group.length;
            return count;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int count = child[groupPosition].length;
            return count;
        }

        @Override
        public Object getGroup(int groupPosition) {

            return group[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return child[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.main_stafflist_expand_adapter_group,null);
            }
            TextView groupName = convertView.findViewById(R.id.stafflist_group_name_tv);
            TextView childCount = convertView.findViewById(R.id.stafflist_count_tv);

            int[] is = child[groupPosition];
            int count = 0;
            for(int i=0; i<is.length; i++){
                if(userStateMap.get(is[i]).equals("班")){
                    count ++;
                }
            }

            groupName.setText(group[groupPosition]);
            childCount.setText(count + "/" +String.valueOf(child[groupPosition].length));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder childViewHolder;
            if(convertView == null){
                childViewHolder = new ChildViewHolder();
                convertView = inflater.inflate(R.layout.main_stafflist_expand_adapter_child,null);
                childViewHolder.childImage = convertView.findViewById(R.id.main_stafflist_user_image_civ);
                childViewHolder.childName = convertView.findViewById(R.id.main_stafflist_name_textview);
                childViewHolder.childPost = convertView.findViewById(R.id.main_stafflist_post_tv);
                childViewHolder.childPoneNumber = convertView.findViewById(R.id.main_stafflist_pn_tv);
                childViewHolder.childEmail = convertView.findViewById(R.id.main_stafflist_email_tv);
                childViewHolder.childState = convertView.findViewById(R.id.main_stafflist_state_tv);
                convertView.setTag(childViewHolder);
            }else {
                childViewHolder = (ChildViewHolder)convertView.getTag();
            }

            childViewHolder.childName.setText(userNameMap.get(getChild(groupPosition,childPosition)));
            byte[] src = userImageMap.get(getChild(groupPosition,childPosition));
            Glide.with(convertView).load(src).into(childViewHolder.childImage);
            childViewHolder.childPost.setText(userPostMap.get(getChild(groupPosition,childPosition)));
            childViewHolder.childPoneNumber.setText(userPoneNumberMap.get(getChild(groupPosition,childPosition)));
            childViewHolder.childEmail.setText(userEmailMap.get(getChild(groupPosition,childPosition)));
            childViewHolder.childState.setText(userStateMap.get(getChild(groupPosition,childPosition)));
            switch (userStateMap.get(getChild(groupPosition,childPosition))){
                case "班": childViewHolder.childState.setTextColor(Color.argb(255,0,100,0)); break;
                default: childViewHolder.childState.setTextColor(Color.argb(255,200,200,200)); break;
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class ChildViewHolder{
            CircleImageView childImage;
            TextView childName;
            TextView childPost;
            TextView childEmail;
            TextView childPoneNumber;
            TextView childState;
        }
    }


}
