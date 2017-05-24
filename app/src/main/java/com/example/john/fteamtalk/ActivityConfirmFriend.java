package com.example.john.fteamtalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static com.example.john.fteamtalk.UtilsFinalArguments.urlHead;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;

/**
 * Created by john on 2017/5/24.
 */

public class ActivityConfirmFriend extends BaseActivity {

    //UIs
    private Toolbar toolbar;
    private TextView infoTxv;
    private Button confirmBtn;
    //data
    private String friendName;
    //Volley
    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_friend);
        setImmersiveMode();

        initView();
        initPermission();
        initClickEvent();
        initData();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        infoTxv = (TextView) findViewById(R.id.text_view_msg);
        confirmBtn = (Button) findViewById(R.id.btn_confirm_ok);
    }

    private void initPermission() {

    }

    private void initClickEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mQueue == null) {
                    mQueue = Volley.newRequestQueue(ActivityConfirmFriend.this);
                }

                StringRequest setNicknameRequest = new StringRequest(Request.Method.POST, urlHead + "addfriend.action?username="
                        + userInfoStatic.getUsername(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ActivityConfirmFriend.this, "无法添加好友", Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(setNicknameRequest);

                onBackPressed();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendName");
        String info = "Will you accept \n" + friendName + "\n as your friend?";
        infoTxv.setText(info);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,ActivityConfirmFriend.class);
        context.startActivity(intent);
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

}
