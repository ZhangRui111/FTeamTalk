package com.example.john.fteamtalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.john.fteamtalk.UtilsFinalArguments.urlHead;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;


/**
 * Created by ZhangRui on 2016/10/22.
 */

public class ActivityRegister extends BaseActivity implements View.OnClickListener {

    //UIs
    private Toolbar toolbar;
    private EditText phoneInputEdt,passwordInputEdt;  //手机号输入框
    private Button okLoginBtn;  //注册登录按钮
    private ImageButton isHideImgBtn;  //控制密码显示
    private Boolean isShowOrHide = false;  //控制密码是否明文显示
    //data
    private String phoneInputString, passwordInputString;

    //网络请求
    private RequestQueue mQueue;
    private Bitmap bitmap;
    private AlertDialog dialog;

    //输入法管理器
    private InputMethodManager inputMethodManager;
    //Handler
    Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==0x123){

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setImmersiveMode();

        initView();
        initClickEvent();
        initData();
        initPermission();
    }

    private void initView() {
        //backImgBtn = (ImageButton) findViewById(R.id.back_Img);
        phoneInputEdt = (EditText) findViewById(R.id.edit_text_register_phone);
        passwordInputEdt = (EditText) findViewById(R.id.edit_text_register_password);
        okLoginBtn = (Button) findViewById(R.id.btn_sign_in_on);
        isHideImgBtn = (ImageButton) findViewById(R.id.Img_btn_register_hide);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.background_toolbar);
        //给自定义的返回按钮添加监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);;
    }

    private void initClickEvent() {
        //backImgBtn.setOnClickListener(this);
        okLoginBtn.setOnClickListener(this);
        isHideImgBtn.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initPermission() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_in_on:
                funcRegister();
                break;
            case R.id.Img_btn_register_hide:
                funcIfShowPassword();
                break;
            default:
                break;
        }
    }

    //注册逻辑
    private void funcRegister() {
        phoneInputString = phoneInputEdt.getText().toString();
        passwordInputString = passwordInputEdt.getText().toString();

        //ActivityMain.actionStart(ActivityRegister.this);
        if (phoneInputString == null || phoneInputString.equals("")) {
            Toast.makeText(this, "用户名不能为空哦", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (passwordInputString == null || passwordInputString.equals("")) {
            Toast.makeText(this, "密码不能为空哦", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            //清除EditText焦点，关闭软键盘
            passwordInputEdt.clearFocus();
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(passwordInputEdt.getWindowToken(),0);// 隐藏输入法
            }
            //根据输入的手机号和密码与后台通信对比，检查登陆的合法性
            funcCheckLegitimacy(phoneInputString, passwordInputString);
        }
    }

    private void funcCheckLegitimacy(String phoneInputString, String passwordInputString) {

        //声明为final的变量才能用于内部类
        final String phoneTmp = phoneInputString;
        final String passwordTmp = passwordInputString;

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityRegister.this);
        }

        String urllogin = urlHead + "login.action?username=" + phoneTmp + "&password=" + passwordTmp;

        //提示正在登陆
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityRegister.this, R.style.MyAlertDialogStyle);
        builder.setTitle("登录中");
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(ActivityRegister.this);
        builder.setView(progressBar,20,20,20,20);
        dialog = builder.create();
        dialog.show();

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityRegister.this);
        }
        StringRequest loginRequest = new StringRequest(Request.Method.POST, urllogin, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.d("TAG", s);

                Gson gson = new Gson();
                DataLoginRegister dataLoginRegister = gson.fromJson(s,DataLoginRegister.class);


                userInfoStatic.setUsername(phoneTmp);
                //转入主界面
                ActivityInitInfo.actionStart(ActivityRegister.this);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ActivityRegister.this, "密码或者用户名错误！", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        mQueue.add(loginRequest);
    }



    //控制密码是否明文显示
    private void funcIfShowPassword() {
        //明文显示
        if (isShowOrHide.equals(true)) {
            passwordInputEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isHideImgBtn.setBackgroundResource(R.mipmap.ic_eye);
        }
        //密文显示
        if (isShowOrHide.equals(false)) {
            passwordInputEdt.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
            isHideImgBtn.setBackgroundResource(R.mipmap.ic_lock);
        }
        isShowOrHide = !isShowOrHide;
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
        Intent intent = new Intent(context,ActivityRegister.class);
        context.startActivity(intent);
    }

}
