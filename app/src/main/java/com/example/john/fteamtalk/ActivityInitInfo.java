package com.example.john.fteamtalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;

/**
 * Created by john on 2017/5/24.
 */

public class ActivityInitInfo extends BaseActivity implements View.OnClickListener {

    //UIs
    private Toolbar toolbar;
    private EditText userNickNameEdt, userSignEdt;
    private TextView userSexTev, userDepartTxv;
    private Button logInBtn;

    //data
    private String nickNameStr, sexStr, departStr, signStr;

    //dialog
    private AlertDialog dialog;

    //Volley
    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initinfo);
        setImmersiveMode();

        initView();
        initClickEvent();
        initData();
        initPermission();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.background_toolbar);
        //给自定义的返回按钮添加监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userNickNameEdt = (EditText) findViewById(R.id.edit_init_userNickname);
        userSignEdt = (EditText) findViewById(R.id.edit_init_userSign);
        userSexTev = (TextView) findViewById(R.id.edit_init_userSex);
        userDepartTxv = (TextView) findViewById(R.id.edit_init_userDepart);
        logInBtn = (Button) findViewById(R.id.btn_sign_init_user);
    }

    private void initClickEvent() {
        userSexTev.setOnClickListener(this);
        userDepartTxv.setOnClickListener(this);
        logInBtn.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initPermission() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_init_userSex:
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                        ActivityInitInfo.this, R.style.MyAlertDialogStyle);
                builder.setTitle("Init gender");
                builder.setItems(new String[]{"男", "女"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                sexStr = "男";
                                userSexTev.setText("男");
                                break;
                            case 1:
                                sexStr = "女";
                                userSexTev.setText("女");
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.edit_init_userDepart:
                final android.support.v7.app.AlertDialog.Builder myBuilder = new android.support.v7.app.AlertDialog.Builder(
                        ActivityInitInfo.this, R.style.MyAlertDialogStyle);
                myBuilder.setTitle("Init Department");
                myBuilder.setItems(new String[]{"IT部", "秘书处", "后勤部", "经理部", "销售部"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                departStr = "IT部";
                                userDepartTxv.setText("IT部");
                                break;
                            case 1:
                                departStr = "秘书处";
                                userDepartTxv.setText("秘书处");
                                break;
                            case 2:
                                departStr = "后勤部";
                                userDepartTxv.setText("后勤部");
                                break;
                            case 3:
                                departStr = "经理部";
                                userDepartTxv.setText("经理部");
                                break;
                            case 4:
                                departStr = "销售部";
                                userDepartTxv.setText("销售部");
                                break;
                            default:
                                break;
                        }
                    }
                });
                myBuilder.create().show();
                break;
            case R.id.btn_sign_init_user:
                funcInitInfo(sexStr, departStr);
                break;
            default:
                break;
        }
    }

    private void funcInitInfo(String sexStr,String departStr) {
        if (userNickNameEdt.getText()!=null){
            nickNameStr = userNickNameEdt.getText().toString();
        }
        if (userSignEdt.getText()!=null){
            signStr = userSignEdt.getText().toString();
        }
        //Toast.makeText(this, ":" + nickNameStr + signStr + sexStr + departStr, Toast.LENGTH_SHORT).show();

        String urlInitInfo = "http://115.28.66.165:8080/initInfo.action?username=" + userInfoStatic.getUsername() + "&nickname=" +
                nickNameStr + "&sex=" + sexStr + "&depart=" + departStr + "&signature=" + signStr;

        Log.i("TTTG",urlInitInfo);
        //提示正在登陆
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityInitInfo.this, R.style.MyAlertDialogStyle);
        builder.setTitle("更新用户信息");
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(ActivityInitInfo.this);
        builder.setView(progressBar,20,20,20,20);
        dialog = builder.create();
        dialog.show();

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityInitInfo.this);
        }
        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlInitInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.d("TAG", s);

                Gson gson = new Gson();
                DataInitInfo dataInitInfo = gson.fromJson(s,DataInitInfo.class);

                //设置用户信息
                userInfoStatic.setNickname(dataInitInfo.getData().getNickname());
                userInfoStatic.setUsersex(dataInitInfo.getData().getSex());
                userInfoStatic.setDepartment(dataInitInfo.getData().getDepart());
                userInfoStatic.setSignature(dataInitInfo.getData().getSignature());
                //转入主界面
                ActivityMain.actionStart(ActivityInitInfo.this);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Toast.makeText(ActivityInitInfo.this, "OK", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                Toast.makeText(ActivityInitInfo.this, "未知错误！", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(loginRequest);
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
            contentChild.setFitsSystemWindows(true);  // 这里设置成侵占状态栏?
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
        Intent intent = new Intent(context,ActivityInitInfo.class);
        context.startActivity(intent);
    }
}
