package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/24.
 */

public class DataInitUserInfo {
    private int code;
    private String msg;
    private Data data;

    public class Data {
        private String username;
        private String nickname;
        private String sex;
        private String depart;
        private String signature;

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }

        public String getSex() {
            return sex;
        }

        public String getDepart() {
            return depart;
        }

        public String getSignature() {
            return signature;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }
}