package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/23.
 */

public class DataReceiveMessage {
    private String code;
    private String msg;
    private Data data;

    public class Data{
        private String username;
        private String friendName;
        private String tiem;
        private String message;

        public String getUsername() {
            return username;
        }

        public String getFriendName() {
            return friendName;
        }

        public String getTiem() {
            return tiem;
        }

        public String getMessage() {
            return message;
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
