package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/23.
 */

public class DataLoginRegister {
    private int code;
    private String msg;
    private Data data;

    public class Data{
        private int ID;

        public int getID() {
            return ID;
        }
    }

    public Data getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}