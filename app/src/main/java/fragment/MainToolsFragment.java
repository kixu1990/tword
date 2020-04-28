package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tword.R;
import com.example.tword.erp.ErpReportActivity;
import com.example.tword.erp.ErpReportInterimActivity;

import mutils.DoubleClickUtil;

/**
 * Created by kixu on 2019/10/24.
 * 主页工具fragment
 */

public class MainToolsFragment extends Fragment {
    private LinearLayout erpButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tools_fragment,container,false);

        erpButton  = view.findViewById(R.id.erp_report_linearlayout);
        erpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止连按
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                //跳转活动
                Intent intent = new Intent(v.getContext(), ErpReportInterimActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }
}
