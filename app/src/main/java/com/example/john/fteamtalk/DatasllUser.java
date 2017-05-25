package com.example.john.fteamtalk;

import java.util.List;

/**
 * Created by john on 2017/5/25.
 */

public class DatasllUser {
    private int code;
    private String msg;
    private List<Data> data;

    public class Data {
        private String username;

        public String getUsername() {
            return username;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public List<Data> getData() {
        return data;
    }

    public Data getItem(int i){
        return data.get(i);
    }
}
