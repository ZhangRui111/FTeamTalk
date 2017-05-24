package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/24.
 */

public class DataItemNewMessage {
    private String userName;
    private String msg;
    private int num;

    public DataItemNewMessage(String userName, String msg, int num) {
        this.userName = userName;
        this.msg = msg;
        this.num = num;
    }

    public String getUserName() {
        return userName;
    }

    public String getMsg() {
        return msg;
    }

    public int getNum() {
        return num;
    }
}
