package com.example.john.fteamtalk;

import java.util.List;

/**
 * Created by john on 2017/3/14.
 */

public class UtilsFinalArguments {

    public static long clickTime = 0; // 退出应用时第一次点击的时间

    //权限控制
    public static final int REQUEST_USUAL = 100;
    public static final int REQUEST_CAMERO = 101;   //调用相机权限
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 103;
    public static final int REQUEST_READ_CONTACT = 104;


    //handler
    public static final int HANDLER_USER_NICKNAME = 150;
    public static final int HANDLER_USER_NICKNAME_ERROR = 151;
    public static final int HANDLER_USER_GENDER = 152;
    public static final int HANDLER_USER_GENDER_ERROR = 153;
    public static final int HANDLER_USER_ICON = 154;
    public static final int HANDLER_USER_ICON_ERROR = 155;
    public static final int HANDLER_NONE_MESSAGE = 156;
    public static final int HANDLER_MODIFY_PASSWORD_OK = 157;
    public static final int HANDLER_MODIFY_PASSWORD_ERROR = 158;
    public static final int HANDLER_NEW_FRIEND = 159;

    //用户信息
    public static Boolean isPicLocal = false;  //头像是否保存到了本地
    public static String iconPath;  //用户头像本地地址
    public static String imagePicPath;  //发送图片的本地地址

    public static Boolean ifBackLive = false;  //应用退出时是否后台运行
    public static Boolean ifValid = false;  //用户是否勾选了不再提醒
    public static Boolean ifAnonymous = false;  //保存ifAnonymousCBox的值，表示是否匿名

    //好友列表
    public static List<DataFriendInfo> dataList;
    public static List<DataFriendInfo> contactList;

    //用户信息
    public static DataUserInfo userInfoStatic;
}
