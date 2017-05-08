package com.example.john.fteamtalk;

import android.Manifest;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_USUAL;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_WRITE_EXTERNAL_STORAGE;


/**
 * Created by ZhangRui on 2016/10/31.
 */

public class ActivityLogin extends BaseActivity implements View.OnClickListener {
    //基本的UI部件
    private Toolbar toolbar;

    private EditText userPhoneInputEdt, passwordInputEdt;  //用户手机号（用户名）和密码的输入框
    private ImageView userIcon;  //用户头像
    private Button loginInBtn;  //登录按钮
    private TextView registerTxv;  //注册界面跳转按钮
    private String userPhoneInput, passwordInput;  //保存手机号和密码的字符串
    private ImageButton hideOrShowPasswordImgBtn;  //显示隐藏密码按钮
    private Boolean isShowpassword = false;  //全局变量，用于控制密码是否明文显示

    private Bitmap bitmap;  //保存用户头像到本地
    private AlertDialog dialog;

    //网络请求
    private RequestQueue mQueue;
    //Handler
    Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==0x123){
                String pathLocalTmp = Environment.getExternalStorageDirectory().getPath()+"/WeMeet";
                String realPathWithName = SavaImage(bitmap, pathLocalTmp);  //返回带有头像文件名的真实本地路径
                funcWriteSharePreferencesIcon(realPathWithName, ActivityLogin.this);

                //下载完成后转入主界面
                ActivityMain.actionStart(ActivityLogin.this);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.dismiss();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        setImmersiveMode();

        initView();
        initClickEvent();
        initData();
        initPermission();
    }

    private void initView() {
        loginInBtn = (Button) findViewById(R.id.button_login);
        userIcon = (ImageView) findViewById(R.id.image_view_logo);
        userPhoneInputEdt = (EditText) findViewById(R.id.edit_text_phone);
        passwordInputEdt = (EditText) findViewById(R.id.edit_text_password);
        hideOrShowPasswordImgBtn = (ImageButton) findViewById(R.id.Img_btn_hide);
        registerTxv = (TextView) findViewById(R.id.text_view_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.background_toolbar);
        passwordInputEdt.setText("");
    }

    private void initClickEvent() {
        loginInBtn.setOnClickListener(this);
        hideOrShowPasswordImgBtn.setOnClickListener(this);
        registerTxv.setOnClickListener(this);
    }


    private void initData() {
        //加载用户头像
        /*iconPath = funcReadSharePreferencesIcon(ActivityLogin.this);
        //Toast.makeText(this, "" + iconPath, Toast.LENGTH_SHORT).show();
        if (iconPath != null) {
            Glide.with(this).load(iconPath).into(userIcon);
        }*/
    }


    /**
     * 获取某些权限
     */
    private void initPermission() {
        // 先判断是否有权限。
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
            if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
                // TODO 相应代码。
                //do nothing
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(ActivityLogin.this, deniedPermissions)) {

                AndPermission.defaultSettingDialog(ActivityLogin.this, REQUEST_USUAL)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //登录按钮
            case R.id.button_login:
                funcCheckInput();
                break;
            //密码是否明文显示按钮
            case R.id.Img_btn_hide:
                funcIfShowPassword();
                break;
            case R.id.text_view_register:
                ActivityRegister.actionStart(ActivityLogin.this);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
    }

    //控制密码是否明文显示
    private void funcIfShowPassword() {
        //明文显示
        if (isShowpassword.equals(true)) {
            passwordInputEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            hideOrShowPasswordImgBtn.setBackgroundResource(R.mipmap.ic_eye);
        }
        //密文显示
        if (isShowpassword.equals(false)) {
            passwordInputEdt.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
            hideOrShowPasswordImgBtn.setBackgroundResource(R.mipmap.ic_lock);
        }
        isShowpassword = !isShowpassword;
    }

    //检查登陆的合法性并登录进入APP
    private void funcCheckInput() {
        userPhoneInput = userPhoneInputEdt.getText().toString();
        passwordInput = passwordInputEdt.getText().toString();

        ActivityMain.actionStart(ActivityLogin.this);
        // 检查输入是否为空
        /*if (userPhoneInput == null || userPhoneInput.equals("")) {
            Toast.makeText(this, "手机号码不能为空哦", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (passwordInput == null || passwordInput.equals("")) {
            Toast.makeText(this, "密码不能为空哦", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            funcConnectAndLogin(userPhoneInput, passwordInput);
        }*/

    }

    private Boolean funcConnectAndLogin(String phone, String password) {

        //声明为final的变量才能用于内部类
        final String phoneTmp = phone;
        final String passwordTmp = password;

        //提示正在登陆
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityLogin.this, R.style.MyAlertDialogStyle);
        builder.setTitle("登录中");
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(ActivityLogin.this);
        builder.setView(progressBar,20,20,20,20);
        dialog = builder.create();
        dialog.show();

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ActivityLogin.this);
        }
        StringRequest loginRequest = new StringRequest(Request.Method.POST, "http://cmweb.top:3000/sessions", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.d("TAG", s);

                Gson gson = new Gson();

                //转入主界面
                ActivityMain.actionStart(ActivityLogin.this);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ActivityLogin.this, "密码或者用户名错误！", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user[mobile]", phoneTmp);
                map.put("user[password]", passwordTmp);
                return map;
            }
        };

        mQueue.add(loginRequest);
        //这里的返回值没有实际意义
        return true;
    }

    /**
     * 保存照片到本地
     */
    private void funcLoadPictureToLocal(String url) {
        //Toast.makeText(this, "" + url, Toast.LENGTH_SHORT).show();
        //url需要拼接上Http://等部分
        String realUrl = "http://cmweb.top:3000" + url;
        new Task().execute(realUrl);
    }

/*    *//**
     * 将用户信息写入SharePreferences保存
     * @param dataUserInfo
     *//*
    private void funcWriteSharePreferences(ResponseDataUserInfo dataUserInfo) {

        SharedPreferences.Editor editor = getSharedPreferences("user_info",MODE_PRIVATE).edit();
        editor.putInt("id",dataUserInfo.getUser().getId());
        editor.putString("mobile",dataUserInfo.getUser().getMobile());
        editor.putInt("gender",dataUserInfo.getUser().getGender());
        editor.putString("nickname",dataUserInfo.getUser().getNickname());
        editor.putString("avatar_url",dataUserInfo.getUser().getAvatar_url());
        editor.putString("auth_token",dataUserInfo.getAuth_token());
        editor.commit();
    }*/

    /**
     *
     * @param iconPath
     * @param context
     */
    private void funcWriteSharePreferencesIconNet(String iconPath, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user_icon_path",MODE_PRIVATE).edit();
        editor.putString("icon_path_net",iconPath);
        editor.commit();
    }

    /**
     *
     * @param iconPath
     * @param context
     */
    private void funcWriteSharePreferencesIcon(String iconPath, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user_icon_path",MODE_PRIVATE).edit();
        editor.putString("icon_path",iconPath);
        editor.commit();
    }

    /**
     *
     * @param nickname
     * @param context
     */
    private void funcWriteSharePreferencesLastLoginUser(String nickname, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user_last_login",MODE_PRIVATE).edit();
        editor.putString("nickname",nickname);
        editor.commit();
    }

    /**
     * 获取网络图片
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 异步线程下载图片
     *
     */
    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap=GetImageInputStream((String)params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message=new Message();
            message.what=0x123;
            handler.sendMessage(message);
        }

    }

    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return String
     */
    public String SavaImage(Bitmap bitmap, String path){
        File file=new File(path);
        FileOutputStream fileOutputStream=null;
        String pathLocal = null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        try {
            pathLocal = path+"/"+System.currentTimeMillis()+".png";
            fileOutputStream=new FileOutputStream(pathLocal);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回本地地址
        return pathLocal;
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
