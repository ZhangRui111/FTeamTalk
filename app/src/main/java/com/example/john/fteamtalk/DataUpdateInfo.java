package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/23.
 */

public class DataUpdateInfo {
    private String code;
    private String msg;
    private Data data;

    public class Data{
        private String username;
        private String nickname;

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }
}
