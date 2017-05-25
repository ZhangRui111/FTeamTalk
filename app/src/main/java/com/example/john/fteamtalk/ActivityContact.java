package com.example.john.fteamtalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.urlHead;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;

/**
 * Created by john on 2017/5/23.
 */

public class ActivityContact extends BaseActivity {
    //UIs
    private Toolbar toolbar;
    private ListView contactListView;
    private AdapterContactList myAdapter;

    //dialog
    private AlertDialog dialog;

    //Volley
    private RequestQueue mQueue;

    //data
    private List<DataFriendInfo> lists;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setImmersiveMode();

        lists = new ArrayList<>();
        initData();
        //initView();
        //initClickAction();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toast.makeText(this, "" + lists.size(), Toast.LENGTH_SHORT).show();
        for(int i=0;i<lists.size();i++){
            Toast.makeText(this, "" + i + lists.get(i).getUserNickName(), Toast.LENGTH_SHORT).show();
        }
        myAdapter = new AdapterContactList(ActivityContact.this, R.layout.item_friendlist,lists);  //实例化适配器，注意和构造函数的参数的意义结合来看，第一个参数是上下文，第二个是自定义的布局id，第三个是要显示的数据源
        contactListView = (ListView) findViewById(R.id.contact_listView);
        contactListView.setAdapter(myAdapter);  //将适配器传给实例化的listView
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DataFriendInfo selectedEntity = lists.get(i);//得到点击的那一项
                //以下是点击逻辑行
                //Toast.makeText(ActivityContact.this, selectedEntity.getUserNickName(), Toast.LENGTH_SHORT).show();
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityContact.this, R.style.MyAlertDialogStyle);
                builder.setTitle("New Friend");
                builder.setMessage("Will add " + selectedEntity.getUserNickName() + " as friend.Are you sure?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        funcRequestNewFriend(selectedEntity.getUserNickName());
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void initClickAction() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        //contactList.clear();
       //网络Http修改昵称
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityContact.this);
        }

        StringRequest setNicknameRequest = new StringRequest(Request.Method.PUT, urlHead + "sendAllUserInfo.action?", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //doNothing
                Log.i("TTT","allusers" + s);
                Gson gson = new Gson();
                DatasllUser data = gson.fromJson(s,DatasllUser.class);
                for(int i = 0;i<data.getData().size();i++) {
                    DataFriendInfo tmpData;
                    //Toast.makeText(ActivityContact.this, "" + i + data.getData().get(i).getUsername(), Toast.LENGTH_SHORT).show();
                    tmpData = new DataFriendInfo(i, data.getData().get(i).getUsername(), "");
                    lists.add(tmpData);
                }
                initView();
                initClickAction();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(setNicknameRequest);
    }

    private void funcRequestNewFriend(String friendName) {
        Toast.makeText(this, "Request has been send!", Toast.LENGTH_SHORT).show();
        //friendName = "123";
        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityContact.this);
        }

        String urlNewFriend = urlHead + "RequestAddFriend.action?username=" + userInfoStatic.getUsername() + "&friendName=" + friendName;

        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlNewFriend, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(ActivityContact.this, "OK" + s, Toast.LENGTH_SHORT).show();

                //handler.sendEmptyMessage(HANDLER_NEW_FRIEND);//发送消失到handler，通知主线程修改昵称成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(loginRequest);
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ActivityContact.class);
        context.startActivity(intent);
    }

    /**
     * 重写回退逻辑
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_out,R.anim.push_right_in);
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
