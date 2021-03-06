package com.example.john.fteamtalk;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import net.qiujuer.genius.ui.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_CAMERO;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_USUAL;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.example.john.fteamtalk.UtilsFinalArguments.dataList;
import static com.example.john.fteamtalk.UtilsFinalArguments.urlHead;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;
import static com.example.john.fteamtalk.UtilsLibrary.decodeFileUtils;

public class ActivityMain extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //base UIs
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView toolbarTitle;

    //FragmentManager
    private FragmentManager mFragmentManager = null;
    private FragmentTransaction mFragmentTransaction = null;
    private FragmentMain fragmentMain = null;
    private FragmentSettings fragmentSettings = null;
    private FragmentMoments fragmentMoments = null;

    //nav_header
    private TextView drawerUserNameTxv;  //侧边栏上部姓名
    private ImageView drawerUserIcon;  //侧边栏上部头像
    private TextView drawerUserSignTxv;  //用户签名

    //网络请求
    private RequestQueue mQueue;

    //dialog
    private AlertDialog dialog;
    private int notificationId = 1;

    //定时任务
    private Timer timer;
    private TimerTask msgTask;
    private TimerTask msgPicTask;
    private TimerTask waitFriReqTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setImmersiveMode();

        initView();
        initPermission();
        initClickEvent();
        initData();
    }

    private void initView() {
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //隐藏Toolbar默认居左显示的Title
        if (toolbar != null) {
            toolbar.setTitle("");
            toolbar.setBackgroundResource(R.drawable.background_toolbar);
        } else {
            Toast.makeText(this, "toolbar == null!", Toast.LENGTH_SHORT).show();
        }
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("FTeamTalk");
        toolbarTitle.setTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //DrawerLayout
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Toast.makeText(this, "navigationView == null!", Toast.LENGTH_SHORT).show();
        }

        //初始化mFragmentManager
        mFragmentManager = getSupportFragmentManager();

        //nav_header
        View headerLayout = navigationView.getHeaderView(0);   //没有这一行时，drawerUserNameTxv的findviewById的结果是null
        drawerUserNameTxv = (TextView) headerLayout.findViewById(R.id.txt_name_header);
        drawerUserIcon = (ImageView) headerLayout.findViewById(R.id.imageView_user_icon_header);
        drawerUserSignTxv = (TextView) headerLayout.findViewById(R.id.txt_sign);
    }

    private void initPermission() {
        // 先判断是否有权限。
        if (!AndPermission.hasPermission(this, Manifest.permission.CAMERA)) {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(REQUEST_CAMERO)
                    .permission(Manifest.permission.CAMERA)
                    .send();
        }
        if (!AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(REQUEST_WRITE_EXTERNAL_STORAGE)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .send();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if(requestCode == REQUEST_CAMERO) {
                // TODO 相应代码。
                //do nothing
            } else if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
                // TODO 相应代码。
                //do nothing
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(ActivityMain.this, deniedPermissions)) {

                // 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(ActivityMain.this, REQUEST_USUAL)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

    private void initClickEvent() {
        drawerUserNameTxv.setOnClickListener(this);
        drawerUserIcon.setOnClickListener(this);
        drawerUserSignTxv.setOnClickListener(this);
    }

    private void initData() {
        //加载第一个Fragment
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        if(fragmentMoments == null){
            fragmentMoments = new FragmentMoments();
            mFragmentTransaction.replace(R.id.main_fragment_layout,fragmentMoments,"tagFragmentMoments");
        }else{
            mFragmentTransaction.show(fragmentMoments);
        }
        mFragmentTransaction.commit();

        //funcGetContact();
        initUserInfo();
        timer = new Timer();

        msgTask = new receiveMsgTask();
        msgPicTask = new receiveMsgPicTask();
        waitFriReqTask = new receiveFriendListTask();

        timer.schedule(msgTask,5000,3*1000);//5秒后每3秒执行该任务一次--接收消息
        //timer.schedule(msgTask,1*60*1000);//1分钟后执行该任务一次
        timer.schedule(msgPicTask,6000,5*1000);  //6秒后每15秒执行该任务一次--接收图片
        timer.schedule(waitFriReqTask,7000,20*1000);  //6秒后每20秒执行该任务一次--接收好友请求*/
    }

    private void initUserInfo() {
        //网络Http修改昵称
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityMain.this);
        }

        StringRequest modifyPasswordRequest = new StringRequest(Request.Method.PUT, urlHead + "sendUserInfo.action?username=" +
                userInfoStatic.getUsername(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.i("TTTS",s);
                Gson gson = new Gson();
                DataInitUserInfo data = gson.fromJson(s,DataInitUserInfo.class);
                if (data.getData() != null) {
                    if(data.getData().getSignature() != null) {
                        userInfoStatic.setSignature(data.getData().getSignature());
                    }
                    if (data.getData().getNickname() != null) {
                        userInfoStatic.setNickname(data.getData().getNickname());
                    }
                    if (data.getData().getSex() != null) {
                        userInfoStatic.setUsersex(data.getData().getSex());
                    }
                    if (data.getData().getDepart() != null) {
                        userInfoStatic.setDepartment(data.getData().getDepart());
                    }

                    if (userInfoStatic.getNickname() != null) {
                        drawerUserNameTxv.setText(userInfoStatic.getNickname());
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ActivityMain.this, "更新用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(modifyPasswordRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_name_header:
            case R.id.txt_sign:
                //doSomething
                funcEditUserSign();
                break;
            case R.id.imageView_user_icon_header:
                //doSomething
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final AlertDialog dialog;
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityMain.this, R.style.MyAlertDialogStyle);
            builder.setTitle("About App");
            builder.setMessage("Made By Rui Yuhan Wensheng\nGuided By XiaoYifei");
            builder.setCancelable(true);
            builder.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        //设置菜单项可被选中并设置为选中状态
        //item.setCheckable(true);
        //item.setChecked(true);

        switch (id){
            case R.id.nav_send:
                hideAllFragment(mFragmentTransaction);
                if(fragmentMain == null){
                    fragmentMain = new FragmentMain();
                    mFragmentTransaction.add(R.id.main_fragment_layout,fragmentMain,"tagFragmentMain");
                }else{
                    mFragmentTransaction.show(fragmentMain);
                }
                //修改标题栏标题
                toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
                toolbarTitle.setText("FTeamTalk");
                toolbarTitle.setTextColor(Color.WHITE);
                break;
            case R.id.nav_share:
                hideAllFragment(mFragmentTransaction);
                if (fragmentMoments == null){
                    fragmentMoments = new FragmentMoments();
                    mFragmentTransaction.add(R.id.main_fragment_layout,fragmentMoments,"tagFragmentMoments");
                }else {
                    mFragmentTransaction.show(fragmentMoments);
                }
                //修改标题栏标题
                toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
                toolbarTitle.setText("FMoments");
                toolbarTitle.setTextColor(Color.WHITE);
                break;
            case R.id.nav_manage:
                hideAllFragment(mFragmentTransaction);
                if (fragmentSettings == null){
                    fragmentSettings = new FragmentSettings();
                    mFragmentTransaction.add(R.id.main_fragment_layout,fragmentSettings,"tagFragmentSetting");
                }else {
                    mFragmentTransaction.show(fragmentSettings);
                }
                //修改标题栏标题
                toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
                toolbarTitle.setText(R.string.action_settings);
                toolbarTitle.setTextColor(Color.WHITE);
                break;
            default:
                break;
        }
        mFragmentTransaction.commit();
        //点击某一项菜单项取消选中状态并关闭drawer
        //item.setCheckable(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 实现连续两次点击退出APP
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - UtilsFinalArguments.clickTime) > 2000) {
                Toast.makeText(this, "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
                UtilsFinalArguments.clickTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        msgTask.cancel();
        msgPicTask.cancel();
        waitFriReqTask.cancel();
    }

    /**
     * 设置nav_header的头像和昵称
     * @param name
     * @param iconPath
     */
    public void funcSetNavHeader(String name, String iconPath) {
        if (name == null && iconPath != null) {
            //只修改头像
            drawerUserIcon.setImageBitmap(decodeFileUtils(iconPath));
        } else if (iconPath == null && name != null) {
            //只修改昵称
            drawerUserNameTxv.setText(name);
        } else {
            drawerUserNameTxv.setText(name);
            drawerUserIcon.setImageBitmap(decodeFileUtils(iconPath));
        }
    }

    private void funcEditUserSign() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityMain.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Change your Signature");
        final EditText input = new EditText(this);
        input.setSingleLine();
        //自定义EditText颜色
        input.setBackgroundResource(R.drawable.edittext_input_line);
        //input.setText(ActivityLogin.cosmicUserInfo.getNickname());
        builder.setView(input,20,20,20,20);;
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String userSign = input.getText().toString();

                //网络Http修改昵称
                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(ActivityMain.this);
                }

                StringRequest modifyPasswordRequest = new StringRequest(Request.Method.PUT, "http://115.28.66.165:8080/updateInfo.action?username="
                        + userInfoStatic.getUsername() + "&signature" + userSign + "&type=1", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        drawerUserSignTxv.setText(userSign);
                        userInfoStatic.setSignature(userSign);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ActivityMain.this, "签名更改失败", Toast.LENGTH_SHORT).show();
                    }
                });

                mQueue.add(modifyPasswordRequest);

                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     *子线程收到消息后在FragmentMoments创建Item
     */
    public void funcFragmentAddItem(DataItemNewMessage data) {
        //通过Tag获取Fragment实例，进而调用Fragment里的方法
        FragmentMoments fragmentMoments =
                (FragmentMoments) getSupportFragmentManager().findFragmentByTag("tagFragmentMoments");
        fragmentMoments.funcGetNewMessage(data);
    }

    public class receiveMsgTask extends TimerTask {
        @Override
        public void run() {
            //任务代码写在此处
            Log.i("TTT","task1");
            /*//通过Tag获取Fragment实例，进而调用Fragment里的方法
            FragmentMoments fragmentMoments =
                    (FragmentMoments) getSupportFragmentManager().findFragmentByTag("tagFragmentMoments");
            fragmentMoments.funcToast();*/
            //初始化一个网络请求队列
            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(ActivityMain.this);
            }

            String urlReceiveMsg = urlHead + "receiveMessage.action?username=" + userInfoStatic.getUsername();

            StringRequest receiveMsgRequest = new StringRequest(Request.Method.POST, urlReceiveMsg, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.i("TTT",s);
                    Gson gson = new Gson();
                    DataReceiveMessage data;
                    data = gson.fromJson(s,DataReceiveMessage.class);
                    if (data != null) {
                        if (data.getData() != null){
                            Log.i("TTTT","msg:" + data.getData().getMessage());
                            //
                            funcFragmentAddItem(new DataItemNewMessage(data.getData().getFriendName(),data.getData().getMessage(),1));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

            mQueue.add(receiveMsgRequest);
        }
    }

    public class receiveMsgPicTask extends TimerTask {
        @Override
        public void run() {
            //任务代码写在此处
            Log.i("TTT","task2");
            //初始化一个网络请求队列
            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(ActivityMain.this);
            }

            String urlReceiveMsgPic = urlHead + "receivePicture.action?username=" + userInfoStatic.getUsername() ;

            StringRequest receiveMsgPicRequest = new StringRequest(Request.Method.POST, urlReceiveMsgPic, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.i("TTT",s);
                    Gson gson = new Gson();
                    DataReceivePicture data;
                    data = gson.fromJson(s,DataReceivePicture.class);
                    if (data != null){
                        if(data.getData() != null){
                            Log.i("TTTT","Pic:" + data.getData().getHead());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

            mQueue.add(receiveMsgPicRequest);
        }
    }

    public class receiveFriendListTask extends TimerTask {
        @Override
        public void run() {

            //初始化一个网络请求队列
            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(ActivityMain.this);
            }

            String urlListMsg = urlHead + "isRequest.action?username=" + userInfoStatic.getUsername() ;

            StringRequest receiveFriListRequest = new StringRequest(Request.Method.POST, urlListMsg, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.i("TTTS","List" + s);
                    DataArrayFriend dat = new Gson().fromJson(s,DataArrayFriend.class);
                    if (dat.getData() != null){
                        List<String> list = dat.getData();
                        if (list != null) {
                            for(int i = 0;i<list.size();i++) {
                                funcNotification(list.get(i));
                            }
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

            mQueue.add(receiveFriListRequest);
        }
    }

    /**
     * 收到好友请求后发出Notification
     */
    private void funcNotification(String friendName) {
        Intent mainIntent = new Intent(this, ActivityConfirmFriend.class);
        mainIntent.putExtra("friendName",friendName);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder=new Notification.Builder(ActivityMain.this);
        builder.setContentTitle("A new friend request!");
        builder.setContentText("Hello,this is " + friendName);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setContentIntent(mainPendingIntent);
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    /*
    *此处的ID值作为Notification的唯一标示，
    */
        Notification notify = builder.build();
        manager.notify(notificationId++,notify);//触发Notification
    }

    /**
     * 隐藏所有Fragment
     * @param fragmentTransaction
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if (fragmentMain != null){
            fragmentTransaction.hide(fragmentMain);
        }
        if (fragmentSettings != null){
            fragmentTransaction.hide(fragmentSettings);
        }
        if (fragmentMoments != null){
            fragmentTransaction.hide(fragmentMoments);
        }
    }

    /**
     * 对于不同版本的安卓系统实现沉浸式体验
     */
    private void setImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //调用静态方法设置沉浸式状态栏
            UtilsLibrary.setStateBarColor(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //4.4 全透明状态栏
            //底部导航栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //顶部状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup contentLayout = (ViewGroup) this.findViewById(android.R.id.content);
            View contentChild = contentLayout.getChildAt(0);
            contentChild.setFitsSystemWindows(false);  // 这里设置成侵占状态栏，反而不会侵占状态栏，太坑
            UtilsLibrary.setupStatusBarView(this, contentLayout, Color.parseColor("#4A90E2"));
            //隐藏底部导航栏
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            //int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;  此时将同时隐藏状态栏
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 添加了actionStart()方法后——最佳启动Activity
     * @param context
     */
    public static void actionStart(Context context){
        Intent intent = new Intent(context,ActivityMain.class);
        context.startActivity(intent);
    }
}