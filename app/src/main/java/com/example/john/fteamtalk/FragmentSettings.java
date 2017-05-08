package com.example.john.fteamtalk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import net.qiujuer.genius.ui.widget.CheckBox;
import net.qiujuer.genius.ui.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_USER_GENDER;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_USER_GENDER_ERROR;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_USER_ICON;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_USER_ICON_ERROR;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_USER_NICKNAME;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_USER_NICKNAME_ERROR;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_CAMERO;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_USUAL;
import static com.example.john.fteamtalk.UtilsFinalArguments.iconPath;
import static com.example.john.fteamtalk.UtilsFinalArguments.ifBackLive;
import static com.example.john.fteamtalk.UtilsFinalArguments.ifValid;

/**
 * Created by john on 2017/3/22.
 */

public class FragmentSettings extends TakePhotoFragment implements View.OnClickListener{

    private View view;
    private RelativeLayout settingUserIconRelaLayout;
    private RelativeLayout settingNickNameRelaLayout;
    private RelativeLayout settingGenderRelaLayout;
    private RelativeLayout settingPasswordRelaLayout;

    //layout UIs
    private android.widget.Button chooseIconBtn;
    private android.widget.ImageView userIconUIImg;
    private TextView userNameTxv;  //用户昵称显示区
    private android.widget.Button modifyNameBtn;  //修改用户昵称Button
    private TextView userGenderTxv;  //用户性别显示区
    private android.widget.Button modifyGenderBtn;  //修改用户性别
    private android.widget.Button modifyPasswordBtn;  //修改密码按钮
    private TextView logOffTxv;  //退出登录

    //选择头像弹出的Dialog
    private DialogChooseIcon dialogChooseIcon;

    //TakePhoto
    private TakePhoto takePhoto;
    private CropOptions cropOptions;
    private CompressConfig compressConfig;
    private File file;
    private Uri imageUri;

    //User Data
    private String userSex, userName;
    private File img;  //用于头像的拍照文件
    private Bitmap userBitmap;  //用于上传用户头像的Bitmap

    //网络请求
    private RequestQueue mQueue;
    private AlertDialog waitingDialog;  //等待提示AlertDialog

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case HANDLER_USER_ICON:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    break;
                case HANDLER_USER_ICON_ERROR:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    String error = msg.getData().getString("ErrorMessage");//接受msg传递过来的参数
                    Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_USER_NICKNAME:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    userNameTxv.setText(userName);
                    //nav_header 用户名更新
                    ((ActivityMain) getActivity()).funcSetNavHeader(userName, null);
                    break;
                case HANDLER_USER_NICKNAME_ERROR:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "昵称更改失败", Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_USER_GENDER:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    userGenderTxv.setText(userSex);
                    break;
                case HANDLER_USER_GENDER_ERROR:
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "性别更改失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initPermission();
        initClickEvent();
        initData();
    }

    private void initView() {
        userIconUIImg = (android.widget.ImageView) view.findViewById(R.id.uiImageView_userIcon);  //当前头像
        chooseIconBtn = (android.widget.Button) view.findViewById(R.id.btn_choose_icon);  //修改头像Button
        userNameTxv = (TextView) view.findViewById(R.id.text_view_show_username);  //用户昵称
        modifyNameBtn = (android.widget.Button) view.findViewById(R.id.button_setting_name);  //修改用户昵称
        userGenderTxv = (TextView) view.findViewById(R.id.text_view_userSex);  //用户性别
        modifyGenderBtn = (android.widget.Button) view.findViewById(R.id.btn_sex_un);  //修改用户性别
        modifyPasswordBtn = (android.widget.Button) view.findViewById(R.id.btn_setting_password_un);  //修改密码Button
        logOffTxv = (TextView) view.findViewById(R.id.text_view_exit_login);  //退出登录Button

        //layout
        settingUserIconRelaLayout = (RelativeLayout) view.findViewById(R.id.item_layout_setting_icon);
        settingNickNameRelaLayout = (RelativeLayout) view.findViewById(R.id.item_layout_username_setting);
        settingGenderRelaLayout = (RelativeLayout) view.findViewById(R.id.item_layout_setting_sex);
        settingPasswordRelaLayout = (RelativeLayout) view.findViewById(R.id.item_layout_password_setting);
    }

    private void initPermission() {
        if (!AndPermission.hasPermission(getActivity(), Manifest.permission.CAMERA)) {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(REQUEST_CAMERO)
                    .permission(Manifest.permission.CAMERA)
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
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(getActivity(), deniedPermissions)) {
                //用自定义的提示语。
                AndPermission.defaultSettingDialog(getActivity(), REQUEST_USUAL)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

    private void initClickEvent() {
        chooseIconBtn.setOnClickListener(this);
        modifyNameBtn.setOnClickListener(this);
        modifyGenderBtn.setOnClickListener(this);
        modifyPasswordBtn.setOnClickListener(this);
        logOffTxv.setOnClickListener(this);

        //layout
        settingUserIconRelaLayout.setOnClickListener(this);
        settingNickNameRelaLayout.setOnClickListener(this);
        settingGenderRelaLayout.setOnClickListener(this);
        settingPasswordRelaLayout.setOnClickListener(this);
    }

    private void initData() {
        //UIs



        //takePhoto
        takePhoto = getTakePhoto();  //初始化TakePhoto对象
        cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();  //设置裁剪参数
        compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();  //设置压缩参数
        takePhoto.onEnableCompress(compressConfig,true);  //设置为需要压缩
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_layout_setting_icon:
            case R.id.btn_choose_icon:
                startDialog();
                break;
            case R.id.item_layout_username_setting:
            case R.id.button_setting_name:
                funcSetNickname();
                break;
            case R.id.item_layout_setting_sex:
            case R.id.btn_sex_un:
                funcChooseGender();
                break;
            case R.id.item_layout_password_setting:
            case R.id.btn_setting_password_un:
                ActivityModifyPassword.actionStart(getActivity());
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.text_view_exit_login:
                funcLogOff();
                break;
            default:
                break;
        }
    }


    //选择头像，从相册/拍照成功返回
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        //头像本地路径
        iconPath = result.getImage().getOriginalPath();
        funcWriteSharePreferencesIcon(iconPath, getActivity());

        /*new Thread() {
            @Override
            public void run() {
                //上传头像
                funcUploadUserIcon(decodeFileUtils(iconPath));
            }
        }.start();*/
        funcFreshIcon();

        //progressBar
        /*android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setTitle("正在上传");
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(getActivity());
        builder.setView(progressBar,20,20,20,20);
        builder.setCancelable(false);
        waitingDialog = builder.create();
        waitingDialog.show();*/
    }

    //选择头像，从相册/拍照调用失败
    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    //选择头像，从相册/拍照调用取消
    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    /**
     * 弹出Dialog选择头像
     */
    private void startDialog() {
        dialogChooseIcon = new DialogChooseIcon(getActivity(), new DialogChooseIcon.IDialogChooseIconEventListener() {
            @Override
            public void listenDialogChooseIconEvent(int chooseFlag) {
                switch (chooseFlag) {
                    case 0:
                        imageUri = getImageCropUri();
                        takePhoto.onPickFromCaptureWithCrop(imageUri,cropOptions);
                        break;
                    case 1:
                        imageUri = getImageCropUri();
                        takePhoto.onPickFromGalleryWithCrop(imageUri, cropOptions);
                        break;
                    default:
                        break;
                }
            }
        });
        //设置Dialog的位置
        Window dialog_window = dialogChooseIcon.getWindow();  //获取到当前Activity的Window
        dialog_window.setGravity(Gravity.CENTER | Gravity.BOTTOM);  //设置对话框的位置
        WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();  //获取到LayoutParams
        dialog_window_attributes.width = WindowManager.LayoutParams.MATCH_PARENT;  //设置Dialog的长宽
        dialog_window_attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog_window_attributes.windowAnimations = R.style.dialogAnim;
        dialog_window.setAttributes(dialog_window_attributes);

        dialogChooseIcon.show();

    }

    /**
     * 使用Volley
     * 上传用户头像到服务器
     * @param userBitmap
     */
    private void funcUploadUserIcon(final Bitmap userBitmap) {

        /*String url = "http://cmweb.top:3000/users/me";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.i("ICON_TAG", resultResponse);

                //handler处理UI
                handler.sendEmptyMessage(HANDLER_USER_ICON);//发送消失到handler，通知主线程修改头像成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("ICON_TAG", errorMessage);

                Message msg = new Message();
                msg.what = HANDLER_USER_ICON_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("ErrorMessage",errorMessage);  //往Bundle中存放数据
                msg.setData(bundle);//mes利用Bundle传递数据
                handler.sendMessage(msg);//发送消失到handler，通知主线程修改头像失败

                error.printStackTrace();
            }
        }) {
            //给Http请求增加请求参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user[gender]", staticUserInfo.getUser().getGender() + "");
                return map;
            }
            //给Http请求增加头部标签
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", staticUserInfo.getAuth_token());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("user[avatar]", new DataPart("file_avatar.jpg", UtilsLibrary.getFileDataFromDrawable(getActivity(), userBitmap), "image/jpeg"));
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);*/
    }

    private void funcFreshIcon() {
        Glide.with(this).load(iconPath).into(userIconUIImg);
        ((ActivityMain) getActivity()).funcSetNavHeader(null,iconPath);
    }

    /**
     * 弹出Dialog设置用户昵称nickname
     */
    private void funcSetNickname() {
        Toast.makeText(getActivity(), "funcSetNickname", Toast.LENGTH_SHORT).show();

        /*final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setTitle("Change your NickName");
        final EditText input = new EditText(getActivity());
        input.setSingleLine();
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        //自定义EditText颜色
        input.setBackgroundResource(R.drawable.edittext_input_line);
        input.setText(staticUserInfo.getUser().getNickname());
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
                userName = input.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行网络请求
                        funcUploadUserNickName(userName);
                    }
                }.start();

                //progressBar
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("正在上传");
                builder.setCancelable(false);
                ProgressBar progressBar = new ProgressBar(getActivity());
                builder.setView(progressBar,20,20,20,20);
                builder.setCancelable(false);
                waitingDialog = builder.create();
                waitingDialog.show();

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();*/
    }

    /**
     * 上传用户昵称
     * @param data
     */
    private void funcUploadUserNickName(String data) {

        /*final String userName = data;

        //网络Http修改昵称
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        StringRequest modifyPasswordRequest = new StringRequest(Request.Method.PUT, "http://cmweb.top:3000/users/me", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //成功修改昵称。handler处理UI
                handler.sendEmptyMessage(HANDLER_USER_NICKNAME);//发送消失到handler，通知主线程修改昵称成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(HANDLER_USER_NICKNAME_ERROR);//发送消失到handler，通知主线程修改昵称失败
            }
        }) {
            //给Http请求增加请求参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user[gender]", staticUserInfo.getUser().getGender() + "");
                map.put("user[nickname]", userName);
                return map;
            }
            //给Http请求增加头部标签
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", staticUserInfo.getAuth_token());
                return params;
            }
        };
        mQueue.add(modifyPasswordRequest);*/
    }

    /**
     * 弹出Dialog选择性别
     */
    private void funcChooseGender() {

        Toast.makeText(getActivity(), "funcChooseGender", Toast.LENGTH_SHORT).show();
        /*final String items[] = new String[]{"IT部","秘书处","后勤部","销售部","经理部"};

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setTitle("Setting");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userSex = items[which] + "";
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行网络请求
                        funcUploadUserGender();
                    }
                }.start();

                //progressBar
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("正在上传");
                builder.setCancelable(false);
                ProgressBar progressBar = new ProgressBar(getActivity());
                builder.setView(progressBar,20,20,20,20);
                builder.setCancelable(false);
                waitingDialog = builder.create();
                waitingDialog.show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();*/
    }

    /**
     * 上传修改用户性别
     */
    private void funcUploadUserGender() {
        //网络Http修改昵称
       /* if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        StringRequest modifyPasswordRequest = new StringRequest(Request.Method.PUT, "http://cmweb.top:3000/users/me", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //修改成功，handler修改UI
                handler.sendEmptyMessage(HANDLER_USER_GENDER);//发送消失到handler，通知主线程修改性别成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.sendEmptyMessage(HANDLER_USER_GENDER_ERROR);//发送消失到handler，通知主线程修改性别失败
            }
        }) {
            //给Http请求增加请求参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("user[gender]", (!userSex.equals("男")?0:1) + "");
                map.put("user[nickname]", staticUserInfo.getUser().getNickname());
                return map;
            }
            //给Http请求增加头部标签
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", staticUserInfo.getAuth_token());
                return params;
            }
        };

        mQueue.add(modifyPasswordRequest);*/
    }

    /**
     * 登出
     */
    private void funcLogOff() {

        ActivityCollector.finishAll();
        /*if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        StringRequest modifyPasswordRequest = new StringRequest(Request.Method.DELETE, "http://cmweb.top:3000/sessions", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ActivityCollector.finishAll();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "用户没有登录！", Toast.LENGTH_SHORT).show();
            }
        }) {
            //给Http网络请求增加头部标签
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", staticUserInfo.getAuth_token());
                return params;
            }
        };

        mQueue.add(modifyPasswordRequest);*/
    }

    /**
     * 获得图片保存路径
     * @return
     */
    private Uri getImageCropUri() {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return Uri.fromFile(file);
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
}
