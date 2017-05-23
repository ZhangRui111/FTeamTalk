package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/23.
 */

public class DataReceivePicture {
    private String code;
    private String msg;
    private Data data;

    public class Data{
        private String username;
        private String head;
        private String friendName;
        private String time;

        public String getUsername() {
            return username;
        }

        public String getHead() {
            return head;
        }

        public String getFriendName() {
            return friendName;
        }

        public String getTime() {
            return time;
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
