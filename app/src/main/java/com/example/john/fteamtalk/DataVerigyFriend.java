package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/23.
 */

public class DataVerigyFriend {
    private int code;
    private String msg;
    private Data data;

    public class Data{
        private String friendName;
        private String username;
        private String Flag;

        public String getFriendName() {
            return friendName;
        }

        public String getUsername() {
            return username;
        }

        public String getFlag() {
            return Flag;
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