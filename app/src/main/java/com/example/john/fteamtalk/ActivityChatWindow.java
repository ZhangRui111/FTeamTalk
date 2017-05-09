package com.example.john.fteamtalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/5/8.
 */

public class ActivityChatWindow extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private String userNickName;
    private int userId;
    private TextView toolbarTitle;
    private Button sendBtn;
    private EditText msgEdt;

    private AdapterChatMessage chatAdapter;
    private ListView lv_chat_listView;
    private List<DataChatMessage> personChats = new ArrayList<>();

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    //ListView条目控制在最后一行
                    lv_chat_listView.setSelection(personChats.size());
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        setImmersiveMode();

        initPermission();
        initView();
        initData();
        initClickEvent();

        initSend();
    }

    private void initPermission() {

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sendBtn = (Button) findViewById(R.id.btn_chat_message_send);
        msgEdt = (EditText) findViewById(R.id.et_chat_message);
        lv_chat_listView = (ListView) findViewById(R.id.lv_chat_dialog);
    }

    private void initData() {
        Intent intent = getIntent();
        userNickName = intent.getStringExtra("userNickName");
        userId = intent.getIntExtra("userId",0);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(userNickName);

        chatAdapter = new AdapterChatMessage(this,R.layout.item_msg,personChats);
    }

    private void initClickEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_chat_message_send:
                if (TextUtils.isEmpty(msgEdt.getText().toString())) {
                    Toast.makeText(ActivityChatWindow.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = msgEdt.getText().toString();
                DataChatMessage msg = new DataChatMessage(userId,userNickName,content,DataChatMessage.TYPE_SEND);
                personChats.add(msg);
                chatAdapter.notifyDataSetChanged(); //当有新消息的时候，刷新ListView中的显示
                lv_chat_listView.setSelection(personChats.size());  //将ListView定位到最后一行
                msgEdt.setText(""); //清空输入框的内容
                break;
            default:
                break;
        }
    }

    private void initSend() {
        /**
         * 虚拟3条发送方的消息
         */
        DataChatMessage msg;
        msg = new DataChatMessage(userId,userNickName,"Hello guy",DataChatMessage.TYPE_RECEIVED);
        personChats.add(msg);
        msg = new DataChatMessage(userId,userNickName,"Hello.Who is that?",DataChatMessage.TYPE_SEND);
        personChats.add(msg);
        msg = new DataChatMessage(userId,userNickName,"This is Tom.Nice talking to you.",DataChatMessage.TYPE_RECEIVED);
        personChats.add(msg);
        lv_chat_listView.setAdapter(chatAdapter);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,ActivityChatWindow.class);
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
