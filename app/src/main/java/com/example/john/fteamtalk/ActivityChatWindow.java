package com.example.john.fteamtalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.imagePicPath;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;
import static com.example.john.fteamtalk.UtilsLibrary.bitmapToBase64;
import static com.example.john.fteamtalk.UtilsLibrary.decodeFileUtils;

/**
 * Created by john on 2017/5/8.
 */

public class ActivityChatWindow extends TakePhotoActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private String friNickName;
    private int userId;
    private TextView toolbarTitle;
    private Button sendBtn;
    private ImageButton sendPicBtn;
    private EditText msgEdt;

    private AdapterChatMessage chatAdapter;
    private ListView lv_chat_listView;
    private List<DataChatMessage> personChats = new ArrayList<>();

    private RequestQueue mQueue;

    //TakePhoto
    private TakePhoto takePhoto;
    private CompressConfig compressConfig;
    private File file;
    private Uri imagePicUri;
    private String picStr;

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

        //initSend();
    }

    private void initPermission() {

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sendBtn = (Button) findViewById(R.id.btn_chat_message_send);
        msgEdt = (EditText) findViewById(R.id.et_chat_message);
        lv_chat_listView = (ListView) findViewById(R.id.lv_chat_dialog);
        sendPicBtn = (ImageButton) findViewById(R.id.picBtn);
    }

    private void initData() {
        Intent intent = getIntent();
        friNickName = intent.getStringExtra("userNickName");
        userId = intent.getIntExtra("userId",0);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(friNickName);

        chatAdapter = new AdapterChatMessage(this,R.layout.item_msg,personChats);
        //takePhoto
        takePhoto = getTakePhoto();  //初始化TakePhoto对象
        //cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();  //设置裁剪参数
        compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();  //设置压缩参数
        takePhoto.onEnableCompress(compressConfig,true);  //设置为需要压缩

    }

    private void initClickEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendBtn.setOnClickListener(this);
        sendPicBtn.setOnClickListener(this);
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
                DataChatMessage msg = new DataChatMessage(userId,friNickName,content,DataChatMessage.TYPE_SEND,DataChatMessage.IF_PIC_NO,"");
                personChats.add(msg);
                chatAdapter.notifyDataSetChanged(); //当有新消息的时候，刷新ListView中的显示
                lv_chat_listView.setSelection(personChats.size());  //将ListView定位到最后一行
                msgEdt.setText(""); //清空输入框的内容
                funcSendMsgToFriend(userInfoStatic.getUsername(),friNickName,content);
                break;
            case R.id.picBtn:
                takePhoto.onPickFromGallery();
                break;
            default:
                break;
        }
    }

    //选择头像，从相册/拍照成功返回
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        //图片本地路径
        imagePicPath = result.getImage().getOriginalPath();
        Toast.makeText(this, "pic:" + imagePicPath, Toast.LENGTH_SHORT).show();
        funcSendPicToFri(imagePicPath);
    }

    //发送图片，从相册/拍照调用失败
    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    //发送图片，从相册/拍照调用取消
    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    private void funcSendMsgToFriend(String nickname, String friendName, String msg) {
        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        String urlNewMsg = "http://115.28.66.165:8080/SendMessage.action?username=" + nickname + "&friendName=" + friendName + "&message" +
                msg +"&time" + "201705121123";


        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlNewMsg, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(ActivityChatWindow.this, "OK" + s, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(loginRequest);
    }

    private void funcSendPicToFri(String path) {
        picStr = bitmapToBase64(decodeFileUtils(path));
        //Log.i("TAGPIC",picStr);

        DataChatMessage msg = new DataChatMessage(userId,friNickName,"",DataChatMessage.TYPE_SEND,DataChatMessage.IF_PIC_YES, path);
        personChats.add(msg);
        chatAdapter.notifyDataSetChanged(); //当有新消息的时候，刷新ListView中的显示
        lv_chat_listView.setSelection(personChats.size());  //将ListView定位到最后一行

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        String urlSendPic = "http://115.28.66.165:8080/SendPicture:.action?username=" + userInfoStatic.getUsername() + "&friendName=" + friNickName + "&head" +
                picStr +"&time" + "201705121123";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlSendPic, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(ActivityChatWindow.this, "OK" + s, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(loginRequest);
    }

/*    private void initSend() {
        *//**
         * 虚拟3条发送方的消息
         *//*
        DataChatMessage msg;
        msg = new DataChatMessage(userId,friNickName,"Hello guy",DataChatMessage.TYPE_RECEIVED,DataChatMessage.IF_PIC_NO,"");
        personChats.add(msg);
        msg = new DataChatMessage(userId,friNickName,"Hello.Who is that?",DataChatMessage.TYPE_SEND,DataChatMessage.IF_PIC_NO,"");
        personChats.add(msg);
        msg = new DataChatMessage(userId,friNickName,"This is Tom.Nice talking to you.",DataChatMessage.TYPE_RECEIVED,DataChatMessage.IF_PIC_NO,"");
        personChats.add(msg);
        lv_chat_listView.setAdapter(chatAdapter);
    }*/

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
