package com.example.tword;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tword.service.NetwordCheckService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
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
import mutils.DoubleClickUtil;
import mutils.GetTopActivity;
import mutils.ImageToBytes;
import nio.NioSocketChannel;

/**
 * Created by kixu on 2019/10/24.
 * 主页框架类
 */
public class TWordMainActivity extends BaseActivity{

    private  ImageView tools,satffList,notice,message;
    private  TextView tools_text,satffList_text,notice_text,message_text,toolbar_text;
    private  LinearLayout tools_layout,satfflist_layout,notice_layout,message_layout,fab_layout;
    private CircleImageView mainUserCIV;

    private Fragment messageFragment,satfflistFragment,noticeFrament,toolsFragment;
    private List<Fragment> fragmentList;
    private ViewPager viewPager;

    private FloatingActionButton addFab;

    private DrawerLayout drawerLayout;
    private  NavigationView navView;
    private CircleImageView navImage;
    private TextView navMail,navUserName;

    public String[] departments;
    public int[][] usersId;
    public HashMap<Integer,String> userName;
    public HashMap<Integer,byte[]> userImage;
    private List<SatffDB> satffDBS;

    private ScreenOnReceiver screenOnReceiver;

    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tword_main);

        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        StatusBarUtil.setTranslucentStatus(this);
        if(!StatusBarUtil.setStatusBarDarkTheme(this,true)){
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }

        satffDBS = DataSupport.select("*").where("userId = ?",String.valueOf(User.getINSTANCE().getUserId()))
                .find(SatffDB.class);

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

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.main_nav_view);
        View heardView = navView.getHeaderView(0);
        navImage = (CircleImageView)heardView.findViewById(R.id.nav_icon_image);
        navMail = (TextView)heardView.findViewById(R.id.nav_mail);
        navUserName = (TextView)heardView.findViewById(R.id.nav_username);

        messageFragment = new MainMessageFragment();
        satfflistFragment = new MainSatfflistFragment();
        noticeFrament = new MainNoticeFragment();
        toolsFragment = new MainToolsFragment();

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(messageFragment);
        fragmentList.add(satfflistFragment);
        fragmentList.add(noticeFrament);
        fragmentList.add(toolsFragment);

        //开启点亮屏幕的广播接收器
        IntentFilter screenOnIntentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        screenOnReceiver = new ScreenOnReceiver();
        registerReceiver(screenOnReceiver,screenOnIntentFilter);
        //------------------------------------------------------------------------------------------

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DoubleClickUtil.isDoubleClick(1000)){
                    return;
                }
                switch (viewPager.getCurrentItem()){
                    case 0:
                        Intent intent = new Intent(TWordMainActivity.this,AddMessage.class);
                        startActivity(intent);
                        break;
                    case 2 :
                        Intent intent1 = new Intent(TWordMainActivity.this,AddNoticeActivity.class);
                        startActivity(intent1);
                        break;
                    default:
                        break;
                }

            }
        });

        List<SatffDB> satffDBS = DataSupport.select("*")
                                            .find(SatffDB.class);
        byte[] src = null;
        String mail = "";
        String userName = "";
        for(SatffDB satff : satffDBS){
            if(satff.getSatffId() == User.getINSTANCE().getUserId()){
                src = satff.getUserImage();
                mail = satff.getEmail();
                userName = satff.getSatffName();
            }
        }
        RequestOptions options = new RequestOptions().override(120,120).centerCrop();
        Glide.with(this).load(src).apply(options).into(mainUserCIV);

        Glide.with(this).load(src).apply(options).into(navImage);

        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查询是否有申请权限，没有的话进行申请，有的话打开相册
                if(ContextCompat.checkSelfPermission(TWordMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(TWordMainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });

        navMail.setText(mail);
        navUserName.setText(userName);

        mainUserCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

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

    /**
     * 打开相册
     */
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    /**
     * 运时权限注册结果反馈
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"你需要同意权限，才能打开相册！",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 隐式Intent结果反馈
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                handleImageOnKitKat(data);
                break;
            default:
                break;
        }
    }

    /**
     * 处理反馈回来的相片
     * @param data
     */
    private void handleImageOnKitKat(Intent data){
        if (data == null){
            return;
        }
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是 document 类型的Uri，则通过documentd处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的URI，直接获取图片路径
            imagePath = uri.getPath();
        }

        displayImage(imagePath);
    }

    /**
     * 通过URI和SELECTION来获取真实的图片路径
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return  path;
    }

    /**
     * 根据图片路径显示图片
     * 和上传服务器，通知服务器更改
     * @param imagePath
     */
    private void displayImage(String imagePath){
        if(imagePath != null){
            RequestOptions options = new RequestOptions().override(120,120).centerCrop();
            Glide.with(this).load(imagePath).apply(options).into(navImage);
            Glide.with(this).load(imagePath).apply(options).into(mainUserCIV);
            //后期看情况更改成接收到服务器回复成功后，更新自已的数据库，保证一致性
            //更新自身数据库
            byte[] imageBytes = ImageToBytes.imagePathToBytes(imagePath);
            SatffDB satffDB = new SatffDB();
            satffDB.setUserImage(imageBytes);
            satffDB.updateAll("satffId = ?",String.valueOf(User.getINSTANCE().getUserId()));
            //通知服务器更改
            //数据格式：Object[] {departments,usersId,usersName,usersPasswrod,usersImage}
            MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"resSatff");
            message.setObjects(new Object[]{null,User.getINSTANCE().getUserId(),null,null,imageBytes});
            try {
                NioSocketChannel.getInstance().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(this,"没有找到图片！",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
 //       Log.d("MainActivity","onResume :"+getClass().getName());
        GetTopActivity.getINSTANCE().setTopActivity(getClass().getName()); //识别为在正在运行的活动
    }

    @Override
    protected void onPause() {
        super.onPause();
 //       Log.d("MainActivity","onPause :"+getClass().getName());
        GetTopActivity.getINSTANCE().setTopActivity(" ");   //取消正在动行的活动
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

                setAddFabVisibility(true);

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

    /**
     * 启动服务
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNetwordCheckService(){
        Intent startIntent = new Intent(this, NetwordCheckService.class);
        startForegroundService(startIntent);
    }

    class ScreenOnReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            //给服务器发送一条信息，如果不通就会在sendMessage()中重新登录
//            MyMessage message = new MyMessage(User.getINSTANCE().getUserId(),new int[]{0},"networdCheck");
//            Log.d("测试：","收到屏幕点亮广播！！");
//            try {
//                NioSocketChannel.getInstance().sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            startNetwordCheckService();

        }
    }
}
