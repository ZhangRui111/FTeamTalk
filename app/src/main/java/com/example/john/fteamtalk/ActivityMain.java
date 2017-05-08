package com.example.john.fteamtalk;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_CAMERO;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_USUAL;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_WRITE_EXTERNAL_STORAGE;
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
    }

    private void initData() {
        //加载第一个Fragment
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        if(fragmentMain == null){
            fragmentMain = new FragmentMain();
            mFragmentTransaction.replace(R.id.main_fragment_layout,fragmentMain);
        }else{
            mFragmentTransaction.show(fragmentMain);
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_name_header:
                //doSomething
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
                    mFragmentTransaction.add(R.id.main_fragment_layout,fragmentMain);
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
                    mFragmentTransaction.add(R.id.main_fragment_layout,fragmentMoments);
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
                    mFragmentTransaction.add(R.id.main_fragment_layout,fragmentSettings);
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