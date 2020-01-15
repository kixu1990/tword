package com.example.tword;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fragment.FragmentViewPagerAdapter;
import fragment.MainMessageFragment;
import fragment.MainNoticeFragment;
import fragment.MainSatfflistFragment;
import fragment.MainToolsFragment;
import litepal.SatffDB;
import materialdesignutil.StatusBarUtil;
import message.MyMessage;
import mutils.GetTopActivity;

public class TWordMainActivity extends BaseActivity{

    private  ImageView tools,satffList,notice,message;
    private  TextView tools_text,satffList_text,notice_text,message_text,toolbar_text;
    private  LinearLayout tools_layout,satfflist_layout,notice_layout,message_layout,fab_layout;
    private CircleImageView mainUserCIV;

    private Fragment messageFragment,satfflistFragment,noticeFrament,toolsFragment;
    private List<Fragment> fragmentList;
    private ViewPager viewPager;

    private FloatingActionButton addFab;

    public String[] departments;
    public int[][] usersId;
    public HashMap<Integer,String> userName;
    public HashMap<Integer,byte[]> userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tword_main);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

//        Intent intent = getIntent();
//        final MyMessage rsMessage = (MyMessage) intent.getSerializableExtra("message");
//        departments = (String[]) rsMessage.getObjects()[0];
//        usersId = (int[][]) rsMessage.getObjects()[1];
//        userName= (HashMap<Integer, String>) rsMessage.getObjects()[2];
//        userImage = (HashMap<Integer, byte[]>) rsMessage.getObjects()[3];

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        tools = (ImageView) findViewById(R.id.tools_view);
        tools_text = (TextView)findViewById(R.id.tools_text);
        tools_layout = (LinearLayout)findViewById(R.id.tools_layout);
        satffList = (ImageView)findViewById(R.id.stafflist_view);
        satffList_text = (TextView)findViewById(R.id.stafflist_text);
        satfflist_layout = (LinearLayout)findViewById(R.id.stafflist_layout);
        notice = (ImageView) findViewById(R.id.ontice_view);
        notice_text = (TextView)findViewById(R.id.notice_text);
        notice_layout = (LinearLayout) findViewById(R.id.notice_layout);
        message = (ImageView) findViewById(R.id.message_view);
        message_text = (TextView)findViewById(R.id.message_text);
        message_layout = (LinearLayout)findViewById(R.id.message_layout);
        toolbar_text = (TextView)findViewById(R.id.main_toolbar_tv);
        mainUserCIV = (CircleImageView)findViewById(R.id.main_user_CIV);

        fab_layout = (LinearLayout)findViewById(R.id.add_layout);
        addFab = (FloatingActionButton)findViewById(R.id.add_fabutton);

        viewPager = (ViewPager)findViewById(R.id.main_viewpager);


        messageFragment = new MainMessageFragment();
        satfflistFragment = new MainSatfflistFragment();
        noticeFrament = new MainNoticeFragment();
        toolsFragment = new MainToolsFragment();

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(messageFragment);
        fragmentList.add(satfflistFragment);
        fragmentList.add(noticeFrament);
        fragmentList.add(toolsFragment);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TWordMainActivity.this,AddMessage.class);
//                intent.putExtra("rsMessage",rsMessage);

//                Intent intent = new Intent(TWordMainActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        List<SatffDB> satffDBS = DataSupport.select("*")
                                            .find(SatffDB.class);
        byte[] src = null;
        for(SatffDB satff : satffDBS){
            if(satff.getSatffId() == User.getINSTANCE().getUserId()){
                src = satff.getUserImage();
            }
        }
        RequestOptions options = new RequestOptions().override(120,120).centerCrop();
        Glide.with(this).load(src).apply(options).into(mainUserCIV);
        viewPager.setAdapter(new FragmentViewPagerAdapter(getSupportFragmentManager(),fragmentList));
        setIconColor();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_SETTLING){
                    setIconColor();
                }
            }
        });


        tools_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3,false);
                setIconColor();

            }
        });

        satfflist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1,false);
                setIconColor();

            }
        });

        notice_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2,false);
                setIconColor();

            }
        });

        message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0,false);
                setIconColor();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
 //       Log.d("MainActivity","onResume :"+getClass().getName());
        GetTopActivity.getINSTANCE().setTopActivity(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
 //       Log.d("MainActivity","onPause :"+getClass().getName());
        GetTopActivity.getINSTANCE().setTopActivity(" ");
    }

    private void setIconColor(){
        int i = viewPager.getCurrentItem();
        switch (i){
            case 0:
                message.setImageResource(R.drawable.message2_128_100);
                message_text.setTextColor(Color.BLACK);
                tools.setImageResource(R.drawable.tools1_128_100);
                tools_text.setTextColor(Color.GRAY);
                satffList.setImageResource(R.drawable.stafflist1_128_100);
                satffList_text.setTextColor(Color.GRAY);
                notice.setImageResource(R.drawable.ontlce1_128_100);
                notice_text.setTextColor(Color.GRAY);
                setAddFabVisibility(true);

                toolbar_text.setText(message_text.getText());
                return;
            case 1:
                message.setImageResource(R.drawable.message1_128_100);
                message_text.setTextColor(Color.GRAY);
                tools.setImageResource(R.drawable.tools1_128_100);
                tools_text.setTextColor(Color.GRAY);
                satffList.setImageResource(R.drawable.stafflist2_128_100);
                satffList_text.setTextColor(Color.BLACK);
                notice.setImageResource(R.drawable.ontlce1_128_100);
                notice_text.setTextColor(Color.GRAY);
                setAddFabVisibility(false);

                toolbar_text.setText(satffList_text.getText());
                return;
            case 2:
                message.setImageResource(R.drawable.message1_128_100);
                message_text.setTextColor(Color.GRAY);
                tools.setImageResource(R.drawable.tools1_128_100);
                tools_text.setTextColor(Color.GRAY);
                satffList.setImageResource(R.drawable.stafflist1_128_100);
                satffList_text.setTextColor(Color.GRAY);
                notice.setImageResource(R.drawable.ontlce2_128_100);
                notice_text.setTextColor(Color.BLACK);
                setAddFabVisibility(false);

                toolbar_text.setText(notice_text.getText());
                return;
            case 3:
                message.setImageResource(R.drawable.message1_128_100);
                message_text.setTextColor(Color.GRAY);
                tools.setImageResource(R.drawable.tools2_128_100);
                tools_text.setTextColor(Color.BLACK);
                satffList.setImageResource(R.drawable.stafflist1_128_100);
                satffList_text.setTextColor(Color.GRAY);
                notice.setImageResource(R.drawable.ontlce1_128_100);
                notice_text.setTextColor(Color.GRAY);
                setAddFabVisibility(false);

                toolbar_text.setText(tools_text.getText());
                return;
        }
    }

    private void setAddFabVisibility(boolean b){
        if(b){
            addFab.show();
            fab_layout.setVisibility(View.VISIBLE);
        }else{
            addFab.hide();
 //           fab_layout.setVisibility(View.GONE);
        }
    }
}
