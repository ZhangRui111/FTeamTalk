package com.example.john.fteamtalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_MODIFY_PASSWORD_ERROR;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_MODIFY_PASSWORD_OK;
import static com.example.john.fteamtalk.UtilsFinalArguments.urlHead;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;


/**
 * Created by ZhangRui on 2017/2/23.
 */

public class ActivityModifyPassword extends AppCompatActivity implements View.OnClickListener {

    private Button confirmBtn;  //确认修改Button
    private EditText oldPasswordEdt;  //旧密码EditText
    private String oldPassword = "";  //用户输入的旧密码
    private EditText newPasswordEdt;  //新密码EditText
    private String newPassword = "";  //用户第一次输入的新密码
    private EditText newPasswordCheckEdt;  //新密码EditText，用于确认
    private String newPasswordCheck = "";  //用户第二次输入的新密码
    private Toolbar toolbar;

    private RequestQueue mQueue;
    private AlertDialog waitingDialog;
    private AlertDialog tipDialog;
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case HANDLER_MODIFY_PASSWORD_OK:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityModifyPassword.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("WeMeet");
                    builder.setMessage("修改成功");
                    builder.setCancelable(true);
                    tipDialog = builder.create();
                    tipDialog.show();
                    tipDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            onBackPressed();
                        }
                    });
                    tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            onBackPressed();
                        }
                    });
                    break;
                case HANDLER_MODIFY_PASSWORD_ERROR:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    android.support.v7.app.AlertDialog.Builder errorbuilder = new android.support.v7.app.AlertDialog.Builder(ActivityModifyPassword.this, R.style.MyAlertDialogStyle);
                    errorbuilder.setTitle("WeMeet");
                    errorbuilder.setMessage("旧密码输入有误！");
                    errorbuilder.setPositiveButton("确定", null);
                    errorbuilder.setCancelable(true);
                    tipDialog = errorbuilder.create();
                    tipDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        setImmersiveMode();

        initView();
        initClickAction();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        confirmBtn = (Button) findViewById(R.id.btn_modify_password_ok);
        oldPasswordEdt = (EditText) findViewById(R.id.edit_text_last_password);
        newPasswordEdt = (EditText) findViewById(R.id.edit_text_new_password);
        newPasswordCheckEdt = (EditText) findViewById(R.id.edit_text_confirm_password);
    }

    private void initClickAction() {
        confirmBtn.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_modify_password_ok:
                funcCheckModify();
                break;
            default:
                break;
        }
    }

    /**
     * 重写回退逻辑
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_out,R.anim.push_right_in);
    }

    private void funcCheckModify() {
        oldPassword = oldPasswordEdt.getText().toString();
        newPassword = newPasswordEdt.getText().toString();
        newPasswordCheck = newPasswordCheckEdt.getText().toString();

        if (oldPassword.equals("") || newPassword.equals("") || newPasswordCheck.equals("")) {
            Toast.makeText(this, "输入不能为空！", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(newPasswordCheck)) {
            Toast.makeText(this, "前后两次密码输入不一致", Toast.LENGTH_SHORT).show();
        } else{
            new Thread() {
                @Override
                public void run() {
                    //在子线程中进行网络请求
                    funcCheckPassword(oldPassword, newPassword);
                }
            }.start();

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityModifyPassword.this, R.style.MyAlertDialogStyle);
            builder.setTitle("正在修改");
            ProgressBar progressBar = new ProgressBar(ActivityModifyPassword.this);
            builder.setView(progressBar,20,20,20,20);
            builder.setCancelable(false);
            waitingDialog = builder.create();
            waitingDialog.show();
        }
    }

    /**
     * 修改密码
     * @param oldString
     * @param newString
     */
    private void funcCheckPassword(String oldString, String newString) {
        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityModifyPassword.this);
        }

        String ulrdeleteFri = urlHead + "changePassword.action?username=" + userInfoStatic.getUsername() +
                "&password=" + oldString + "&newPassword=" + newString;

        //Log.i("TTTT",ulrFriendList);
        StringRequest deleteFriendRequest = new StringRequest(Request.Method.POST, ulrdeleteFri, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.i("LISTTTT",s);
                Toast.makeText(ActivityModifyPassword.this, "Change password ok!", Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.i("TTTT","error");
            }
        });
        mQueue.add(deleteFriendRequest);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,ActivityModifyPassword.class);
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
